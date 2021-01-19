package com.github.worldoneo.ciphelet.connector.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RegisteredListener {
    private Method method;
    private EventListener instance;

    public RegisteredListener(EventListener instance, Method method) {
        this.method = method;
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }

    public boolean isRunning() {
        return instance.isRunning();
    }

    public Method getMethod() {
        return method;
    }

    public void fireEvent(Event chatExEvent){
        method.setAccessible(true);
        try {
            method.invoke(getInstance(), chatExEvent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
