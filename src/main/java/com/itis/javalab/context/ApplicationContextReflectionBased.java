package com.itis.javalab.context;


import com.itis.javalab.Default;
import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.context.interfaces.ApplicationContext;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class ApplicationContextReflectionBased implements ApplicationContext {

    private Map<String, Object> singletons = new HashMap();
    private Reflections reflections;
    private Map<String,Class<?>> exsComponents = new HashMap<>();
    private Map<Class<?>, List<Class<?>>> dictionary = new HashMap<>();

    public ApplicationContextReflectionBased(String[] properties) {
        initBaseConnection(properties);
        reflections = new Reflections(Default.class.getPackage().getName());
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);
        for(Class current: components){
            String[] names = current.getName().split("\\.");
            exsComponents.put(names[names.length - 1],current);
        }
        List<Class<?>> classes;
        for(Class current: exsComponents.values()) {
            classes = new ArrayList<>();
            Set<Class<?>> currentClasses = reflections.getSubTypesOf(current);
            if(currentClasses.size() != 0) {
                classes.addAll(currentClasses);
                dictionary.put(current, classes);
            }
        }
    }

    private void initBaseConnection(String[] properties) {
        try {
            Connection connection = DriverManager.getConnection(properties[0], properties[1], properties[2]);
            singletons.put("connection", connection);

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T getComponent(Class<T> componentType, String name) {
        if (singletons.get(name) == null) {
            try {
                loadComponent(componentType, name);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
        return (T) singletons.get(name);

    }

    private void loadComponent(Class componentType, String name) throws IllegalAccessException, InstantiationException {
        Class component = exsComponents.get(name);
        Set<Class<?>> currentClasses = reflections.getSubTypesOf(component);
        for(Class currentClass: currentClasses){
            if(currentClass.equals(componentType)){
                Object object = currentClass.newInstance();
                object = inject(object);
                singletons.put(name,object);
                return;
            }
        }
        throw new IllegalStateException("Компонент не найден в доступном списке компонентов");
    }

    private Object inject(Object component) throws IllegalAccessException, InstantiationException {
        for (Field field : component.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                if(field.getType().equals(Connection.class)){
                    boolean isAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(component, singletons.get("connection"));
                    field.setAccessible(isAccessible);
                    continue;
                }
                for(Object dependency: singletons.values()){
                    if(dependency.getClass().equals(field.getType())){
                        boolean isAccessible = field.isAccessible();
                        field.setAccessible(true);
                        field.set(component, dependency);
                        field.setAccessible(isAccessible);
                        continue;
                    }
                }
                String name = getComponentName(field.getType().getName());
                loadComponent(dictionary.get(field.getType()).get(0),name);
                Object object = singletons.get(name);
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