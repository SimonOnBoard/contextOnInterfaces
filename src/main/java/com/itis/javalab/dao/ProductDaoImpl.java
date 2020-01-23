package com.itis.javalab.dao;

import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.ProductDao;
import com.itis.javalab.dao.interfaces.RowMapper;
import com.itis.javalab.dto.entity.ProductDTO;
import com.itis.javalab.models.Product;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class ProductDaoImpl implements ProductDao {
    @Autowired
    private Connection connection;

    public ProductDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private RowMapper<Product> productRowMapper = (row) -> {
        Long id = row.getLong("id");
        String name = row.getString("name");
        Double price = row.getDouble("price");
        Boolean ended = row.getBoolean("ended");
        Integer count = row.getInt("count");
        return new Product(id, name, price, ended, count);
    };

    @Override
    public Optional<Product> findByName(String name) {
        Product product = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM product WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            //Если соответстующая строка найдена,обрабатываем её c помощью userRowMapper.
            //Соответствунно получаем объект User.
            if (resultSet.next()) {
                product = productRowMapper.mapRow(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(product);
    }

    @Override
    public LocalDateTime savePaymentAct(Long userId, Long productId, Integer count) {
        try (PreparedStatement statement = connection.prepareStatement(
                "Insert INTO payment_history (user_id, product_id, count,payment_time) values (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);) {
            LocalDateTime now = LocalDateTime.now();
            statement.setLong(1,userId);
            statement.setLong(2,productId);
            statement.setInt(3,count);
            statement.setObject(4, now);
            //Выполняем запрос и сохраняем колличество изменённых строк
            int updRows = statement.executeUpdate();
            if (updRows == 0) {
                //Если ничего не было изменено, значит возникла ошибка
                //Возбуждаем соответсвующее исключений
                throw new SQLException();
            }
            return now;

        } catch (SQLException e) {
            //Если сохранений провалилось, обернём пойманное исключение в непроверяемое и пробросим дальше(best-practise)
            throw new IllegalStateException(e);
        }
    }


    @Override
    public List<Product> findProductsOnPage(Long limit, Long offset) {
        List<Product> result = new ArrayList<>();

        //Создаём новый объект Statement
        //Использование try-with-resources необходимо для арантированного закрытия statement,
        // вне зависимости от успешности операции.
        String SQL_findById = "select * from product WHERE ended = false ORDER BY id" + " LIMIT " + limit + " OFFSET " + offset + " ;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_findById))
        {
            //ResultSet - итерируемый объект.
            //Пока есть что доставать, идём по нему и подаём строки в userRowMapper,
            // который возвращает нам готовый объект User.
            //Добавляем полученный объект в ArrayList.
            while (resultSet.next()) {
                Product product = productRowMapper.mapRow(resultSet);
                result.add(product);
            }
        } catch (SQLException e) {
            //Если операция провалилась, обернём пойманное исключение в непроверяемое и пробросим дальше(best-practise)
            throw new IllegalStateException(e);
        }
        //Возвращаем полученный в результате операции ArrayList
        return result;
    }

    @Override
    public List<ProductDTO> findAllPaymentsById(Long id) {
        return null;
    }

    @Override
    public Optional find(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Product model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO product (name, price, ended, count) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, model.getName());
            statement.setDouble(2,model.getPrice());
            statement.setBoolean(3,model.getEnded());
            statement.setInt(4,model.getCount());
            //Выполняем запрос и сохраняем колличество изменённых строк
            int updRows = statement.executeUpdate();
            if (updRows == 0) {
                //Если ничего не было изменено, значит возникла ошибка
                //Возбуждаем соответсвующее исключений
                throw new SQLException();
            }
            //Достаём созданное Id пользователя
            try (ResultSet set = statement.getGeneratedKeys();) {
                //Если id  существет,обновляем его у подели.
                if (set.next()) {
                    model.setId(set.getLong(1));
                } else {
                    //Модель сохранилась но не удаётся получить сгенерированный id
                    //Возбуждаем соответвующее исключение
                    throw new SQLException();
                }
            }

        } catch (SQLException e) {
            //Если сохранений провалилось, обернём пойманное исключение в непроверяемое и пробросим дальше(best-practise)
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void update(Product model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE product set name = ?, price = ?, ended = ?, count = ? WHERE id = ?")) {
            //На место соответвующих вопросительных знаков уставнавливаем параметры модели, которую мы хотим обновить
            statement.setString(1, model.getName());
            statement.setDouble(2, model.getPrice());
            statement.setBoolean(3, model.getEnded());
            statement.setInt(4,model.getCount());
            statement.setLong(5, model.getId());
            //Выполняем запрос и сохраняем колличество изменённых строк
            int updRows = statement.executeUpdate();

            if (updRows == 0) {
                //Если ничего не было изменено, значит возникла ошибка
                //Возбуждаем соответсвующее исключений
                throw new SQLException();
            }
        } catch (SQLException e) {
            //Если обноление провалилось, обернём пойманное исключение в непроверяемое и пробросим дальше(best-practise)
            throw new IllegalStateException(e);
        }
    }


    @Override
    public void delete(Long id) {

    }

    @Override
    public List findAll() {
        return null;
    }
}
