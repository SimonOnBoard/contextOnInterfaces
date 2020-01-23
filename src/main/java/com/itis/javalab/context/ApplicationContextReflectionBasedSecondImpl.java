package com.itis.javalab.context;

import com.itis.javalab.Default;
import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.context.interfaces.AnotherApplicationContext;
import com.itis.javalab.dao.interfaces.CrudDao;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class ApplicationContextReflectionBasedSecondImpl implements AnotherApplicationContext {
    private Map<String, Object> objects = new HashMap();
    private Reflections reflections;
    private Map<String, Class<?>> exsComponents = new HashMap<>();
    private Map<Class<?>, Class<?>> dictionary = new HashMap<>();

    public ApplicationContextReflectionBasedSecondImpl(String[] properties) {
        initBaseConnection(properties);
        reflections = new Reflections(Default.class.getPackage().getName());
        Set<Class<?>> components = reflections.getSubTypesOf(@Component)
        for (Class current : components) {
            String[] names = current.getName().split("\\.");
            exsComponents.put(names[names.length - 1], current);
        }
        List<Class<?>> classes;
        for (Class current : exsComponents.values()) {
            classes = new ArrayList<>();
            Set<Class<?>> currentClasses = reflections.getSubTypesOf(current);
            if (currentClasses.size() != 0) {
                if (currentClasses.size() >= 2) {
                    if(!current.equals(CrudDao.class))
                    throw new IllegalStateException("Найдено несколько релизаций для компонента");
                }
                classes.addAll(currentClasses);
                dictionary.put(current, classes.get(0));
            }
        }
    }

    private void initBaseConnection(String[] properties) {
        try {
            Connection connection = DriverManager.getConnection(properties[0], properties[1], properties[2]);
            objects.put("connection", connection);

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T getComponent(String name) {
        if (objects.get(name) == null) {
            try {
                loadComponent(name);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
        return (T) objects.get(name);
    }



    private void loadComponent(String name) throws IllegalAccessException, InstantiationException {
        Class component = exsComponents.get(name);
        if (component != null) {
            Class<?> current = dictionary.get(component);
            Object object = current.newInstance();
            object = inject(object);
            objects.put(name, object);
            return;
        }
        throw new IllegalStateException("Компонент не найден в доступном списке компонентов");
    }

    private Object inject(Object component) throws IllegalAccessException, InstantiationException {
        for (Field field : component.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                if (field.getType().equals(Connection.class)) {
                    boolean isAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(component, objects.get("connection"));
                    field.setAccessible(isAccessible);
                    continue;
                }
                for (Object dependency : objects.values()) {
                    Class<?> check = dictionary.get(field.getType());
                    if (dependency.getClass().equals(check)) {
                        boolean isAccessible = field.isAccessible();
                        field.setAccessible(true);
                        field.set(component, dependency);
                        field.setAccessible(isAccessible);
                        continue;
                    }
                }
                String name = getComponentName(field.getType().getName());
                loadComponent(name);
                Object object = objects.get(name);
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                field.set(component, object);
                field.setAccessible(isAccessible);
            }
        }
        return component;
    }

    private String getComponentName(String name) {
        String[] names = name.split("\\.");
        return names[names.length - 1];
    }
}
