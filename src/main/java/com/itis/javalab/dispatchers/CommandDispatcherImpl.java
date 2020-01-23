package com.itis.javalab.dispatchers;

import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dispatchers.interfaces.CommandDispatcher;
import com.itis.javalab.dto.entity.ProductDTO;
import com.itis.javalab.dto.interfaces.Dto;
import com.itis.javalab.dto.system.PaginationDto;
import com.itis.javalab.dto.system.ServiceDto;
import com.itis.javalab.servers.Request;
import com.itis.javalab.services.interfaces.MessageDtoTranslator;
import com.itis.javalab.services.interfaces.ProductService;

import java.util.HashMap;

public class CommandDispatcherImpl implements CommandDispatcher {
    @Autowired
    private MessageDtoTranslator messageDtoTranslator;
    @Autowired
    private ProductService productService;
    @Override
    public Dto getCurrentServe(Request request) {
        String command = request.getPayloadParam("command");
        PaginationDto dto = null;
        switch (command) {
            case "get messages":
                dto = this.getPaginationDto(request);
                return messageDtoTranslator.getMessagesViaPagination(dto);
            case "get products":
                dto = this.getPaginationDto(request);
                return productService.getProductListViaPagination(dto);
            case "remove product":
                if(checkRole("ADMIN",request)){
                    ProductDTO productDTO = request.readProductDto();
                    return productService.removeProduct(productDTO);
                }
                else{
                    return prepareFailAuthMessageInsideDto();
                }
            case "add product":
                if(checkRole("ADMIN",request)){
                    ProductDTO productDTO = request.readProductDto();
                    return productService.addNewProduct(productDTO);
                }
                else{
                    return prepareFailAuthMessageInsideDto();
                }
            case "buy":
                ProductDTO productDTO = request.readProductDto();
                return productService.registerPayment(productDTO, request.jwt);
            default:
                throw new IllegalStateException("Неуказанная команда");

        }
    }

    private Dto prepareFailAuthMessageInsideDto() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("typ","403");
        params.put("message","You are not an ADMIN, acc denied");
        return ServiceDto.builder().chatId(0).service(2).resultParams(params).build();
    }

    private boolean checkRole(String name, Request request) {
        return request.jwt.getClaim("role").asString().equals(name);
    }

    private PaginationDto getPaginationDto(Request request) {
        Long page = Long.parseLong(request.getPayloadParam("number"));
        Long size = Long.parseLong(request.getPayloadParam("size"));
        return PaginationDto.builder().size(size).number(page).build();
    }
}
