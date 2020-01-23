package com.itis.javalab.dao;

import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.AuthDTODao;
import com.itis.javalab.dao.interfaces.RowMapper;
import com.itis.javalab.dto.entity.AuthDataDTO;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class AuthDTODaoImpl implements AuthDTODao {
    @Autowired
    private Connection connection;


    public AuthDTODaoImpl(Connection connection) {
        this.connection = connection;
    }

    private RowMapper<AuthDataDTO> authDataRowMapper = row -> {
        Long id = row.getLong("id");

        String login = row.getString("login");
        String password = row.getString("password");
        Long userId = row.getLong("userId");
        return new AuthDataDTO(id,login,password,userId);
    };
    @Override
    public Optional<AuthDataDTO> findByUserId(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<AuthDataDTO> findByName(String login) {
        AuthDataDTO authDataDTO = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM auth_data WHERE login = ?")){
            statement.setString(1,login);
            ResultSet resultSet = statement.executeQuery();
            //Если соответстующая строка найдена,обрабатываем её c помощью userRowMapper.
            //Соответствунно получаем объект User.
            if (resultSet.next()) {
                authDataDTO = authDataRowMapper.mapRow(resultSet);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(authDataDTO);
    }

    @Override
    public Optional<AuthDataDTO> find(Long id) {
        AuthDataDTO dataDTO = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM auth_data WHERE id = ?")){
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            //Если соответстующая строка найдена,обрабатываем её c помощью userRowMapper.
            //Соответствунно получаем объект User.
            if (resultSet.next()) {
                dataDTO = authDataRowMapper.mapRow(resultSet);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(dataDTO);
    }

    @Override
    public void save(AuthDataDTO model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO auth_data (login, password) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1,model.getLogin());
            statement.setString(2, model.getPassword());
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
    public void update(AuthDataDTO model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE auth_data set login = ?, password = ?, userid = ? WHERE id = ?")) {
            //На место соответвующих вопросительных знаков уставнавливаем параметры модели, которую мы хотим обновить
            statement.setString(1, model.getLogin());
            statement.setString(2, model.getPassword());
            statement.setLong(3, model.getUserId());
            statement.setLong(4, model.getId());
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
    public List<AuthDataDTO> findAll() {
        return null;
    }
}
