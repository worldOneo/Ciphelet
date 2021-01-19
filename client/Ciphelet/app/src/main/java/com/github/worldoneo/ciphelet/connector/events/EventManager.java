package com.github.worldoneo.ciphelet.connector.events;

import androidx.annotation.MainThread;

import com.github.worldoneo.ciphelet.MainActivity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EventManager {
    private static final EventManager instance = new EventManager();
    private final Map<Class<? extends Event>, HashSet<RegisteredListener>> eventClassMap = new HashMap<>();

    private EventManager() {
    }

    public static EventManager getInstance() {
        return instance;
    }

    public void registerEvent(EventListener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            EventHandler cEEH = method.getAnnotation(EventHandler.class);
            Class<?> checkClass;
            if (cEEH == null) continue;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0]))
                continue;
            Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);

            if (!eventClassMap.containsKey(eventClass)) {
                eventClassMap.put(eventClass, new HashSet<RegisteredListener>());
            }
            eventClassMap.get(eventClass).add(new RegisteredListener(listener, method));
        }
    }

    public <T extends Event> void handleEvent(final T event) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!eventClassMap.containsKey(event.getClass())) return;
                HashSet<RegisteredListener> registeredListeners = eventClassMap.get(event.getClass());
                for (RegisteredListener registeredListener : registeredListeners) {
                    if (registeredListener.isRunning()) {
                        registeredListener.fireEvent(event);
                    } else {
                        registeredListeners.remove(registeredListener);
                    }
                }
            }
        });
    }
}
