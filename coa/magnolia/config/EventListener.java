package dev.coa.magnolia.config;

import java.util.Map;

public interface EventListener<C> {
    default EventListener<C> on(String event, C callback) {
        events().put(event, callback);
        return this;
    }

    default EventListener<C> on(Map<String, C> map) {
        events().putAll(map);
        return this;
    }

    void emit(String event);

    default C event(String name) {
        return events().get(name);
    }

    default EventListener<C> off(String... events) {
        for (String event : events) events().remove(event);
        return this;
    }

    Map<String, C> events();
}
