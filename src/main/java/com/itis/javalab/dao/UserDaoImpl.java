package com.itis.javalab.dao;

import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.AuthDTODao;
import com.itis.javalab.dao.interfaces.UserDao;
import com.itis.javalab.dao.interfaces.UserRowMapper;
import com.itis.javalab.dto.entity.AuthDataDTO;
import com.itis.javalab.models.User;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.*;

@NoArgsConstructor
public class UserDaoImpl implements UserDao {
    @Autowired
    private Connection connection;
    private UserRowMapper<User> userFindRowMapper = (row, dto) -> {
        Long id = row.getLong("id");
        String name = row.getString("username");
        String role = row.getString("user_role");
        return new User(id, name, role, dto);
    };
    @Autowired
    private AuthDTODao authDTODao;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
        authDTODao = new AuthDTODaoImpl(connection);
    }


    @Override
    public Optional<User> find(Long id) {
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            //Если соответстующая строка найдена,обрабатываем её c помощью userRowMapper.
            //Соответствунно получаем объект User.
            if (resultSet.next()) {
                Long authDataId = resultSet.getLong("authdataid");
                Optional<AuthDataDTO> dto = authDTODao.find(authDataId);
                if (dto.isPresent()) {
                    user = userFindRowMapper.mapRow(resultSet, dto.get());
                } else {
                    throw new IllegalStateException("Что-то не так, мб БД упала");
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void save(User model) {
        //Создаём новый объект PreparedStatement,с соотвествующим запросом для сохранния пользователя
        //Использование try-with-resources необходимо для гарантированного закрытия statement,вне зависимости от успешности операции.
        //Аргумент Statement.RETURN_GENERATED_KEYS даёт возможность хранения сгенерированных id (ключей)  внутри statement.
        authDTODao.save(model.getAuthDataDTO());
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users (username, authDataId) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, model.getUserName());
            statement.setLong(2, model.getAuthDataDTO().getId());
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
                    model.getAuthDataDTO().setUserId(model.getId());
                    authDTODao.update(model.getAuthDataDTO());
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
    public void update(User model) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public Optional<User> findByName(String login) {
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            //Если соответстующая строка найдена,обрабатываем её c помощью userRowMapper.
            //Соответствунно получаем объект User.
            if (resultSet.next()) {
                user = userFindRowMapper.mapRow(resultSet, null);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByDTO(AuthDataDTO dto) {
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setLong(1, dto.getUserId());
            ResultSet resultSet = statement.executeQuery();
            //Если соответстующая строка найдена,обрабатываем её c помощью userRowMapper.
            //Соответствунно получаем объект User.
            if (resultSet.next()) {
                user = userFindRowMapper.mapRow(resultSet, dto);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Map<Long, String> findNamesByIds(Set<Long> ids) {
        Map<Long, String> names = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT username from users where id = ?")) {
            for (Long id : ids) {
                statement.setLong(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String nick = resultSet.getString("username");
                    names.put(id, nick);
                }
                else{
                    throw new IllegalStateException
                            ("Ненайден существующий у сообщения пользователь");
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return names;
    }

    @Override
    public Double getBalance(Long id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT balance from users where id = ?")) {
                statement.setLong(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
                else{
                    throw new IllegalStateException
                            ("Ненайден существующий у сообщения пользователь");
                }
            } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void updateBalance(Long id, Double balance) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE users set balance = ? WHERE id = ?")) {
            //На место соответвующих вопросительных знаков уставнавливаем параметры модели, которую мы хотим обновить
            statement.setDouble(1,balance);
            statement.setLong(2, id);
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

}
