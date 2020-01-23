package com.itis.javalab.services.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.ProductDao;
import com.itis.javalab.dto.entity.ProductDTO;
import com.itis.javalab.dto.entity.ShowProductDTO;
import com.itis.javalab.dto.system.PaginationDto;
import com.itis.javalab.dto.system.ServiceDto;
import com.itis.javalab.models.Product;
import com.itis.javalab.services.interfaces.BalanceService;
import com.itis.javalab.services.interfaces.ProductService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BalanceService balanceService;
    @Override
    public ServiceDto getProductListViaPagination(PaginationDto paginationDto) {
        Long page = paginationDto.getNumber();
        Long size = paginationDto.getSize();
        List<Product> products = productDao.findProductsOnPage(size, (page - 1) * size);
        List<ShowProductDTO> products1 = new ArrayList<>();
        products.stream().forEach(product -> products1.add(new ShowProductDTO(
                product.getName(), product.getPrice(), product.getCount())));
        return prepareDto(products1);
    }

    @Override
    public ServiceDto removeProduct(ProductDTO product) {
        Optional<Product> productCandidate = productDao.findByName(product.getName());
        if (productCandidate.isPresent()) {
            Product product1 = productCandidate.get();
            product1.setEnded(true);
            product1.setCount(0);
            productDao.update(product1);
            return prepareSuccessMessageIntoDto("Товар успешно изъят из продажи, " +
                    "Вы можете просмотреть его в истории");
        } else {
            return prepareCustomFailMessageIntoDto("Товар не найден");
        }
    }

    @Override
    public ServiceDto addNewProduct(ProductDTO productDto) {
        Product product = new Product(0L, productDto.getName(), productDto.getPrice(),
                Boolean.FALSE,productDto.getCount());
        productDao.save(product);
        return prepareSuccessMessageIntoDto("Товар успешно сохранён");
    }

    @Override
    public ServiceDto registerPayment(ProductDTO productDTO, DecodedJWT jwt) {
        Optional<Product> product = productDao.findByName(productDTO.getName());
        if(product.isPresent()){
            Product product1 = product.get();
            if(!product1.getEnded().booleanValue()){
                if(product1.getCount() >= productDTO.getCount()){
                    if(!balanceService.checkAvaliableBalance(jwt, product1.getPrice(),productDTO.getCount())){
                        return prepareCustomFailMessageIntoDto("Недостаточно средств");
                    }
                    product1.setCount(product1.getCount() - productDTO.getCount());
                    productDao.update(product1);
                    Long id = jwt.getClaim("id").asLong();
                    LocalDateTime now = productDao.savePaymentAct(id, product1.getId(), productDTO.getCount());
                    productDTO.setDateTime(Timestamp.valueOf(now).getTime());
                    balanceService.setBalance(jwt, product1.getPrice(), productDTO.getCount());
                    return preparePaymentSuccessBuy(productDTO);
                }
                else{
                    return prepareCustomFailMessageIntoDto("Столько товара нет, вы хотите купить слишком много," +
                            "или обладает неактуальными данными");
                }
            }
            else{
                return prepareCustomFailMessageIntoDto("Товар временно удалён из продажи");
            }
        }
        else{
            return prepareCustomFailMessageIntoDto("Товар не найден");
        }
    }

    private ServiceDto preparePaymentSuccessBuy(ProductDTO productDTO) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("typ","300S");
        params.put("product",productDTO);
        params.put("message","Покупка успешно совершена");
        return ServiceDto.builder().chatId(0).service(4).resultParams(params).build();
    }


    private ServiceDto prepareCustomFailMessageIntoDto(String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("typ", "300E");
        params.put("message", message);
        return ServiceDto.builder().chatId(0).service(2).resultParams(params).build();
    }

    private ServiceDto prepareSuccessMessageIntoDto(String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("typ", "200S");
        params.put("message", message);
        return ServiceDto.builder().chatId(0).service(2).resultParams(params).build();
    }

    private ServiceDto prepareDto(List<ShowProductDTO> products1) {
        Map<String, Object> params = new HashMap<>();
        params.put("typ", "200P");
        params.put("data", products1);
        return ServiceDto.builder().service(3).chatId(0).resultParams(params).build();
    }
}
