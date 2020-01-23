package com.itis.javalab.context.interfaces;

public interface ApplicationContext {
    // при получении компонента, если у этого компонента
    // есть зависимости - то нужно их тоже проставить
    <T> T getComponent(Class<T> componentType, String name);
}