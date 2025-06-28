package dev.coa.magnolia.config;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Setting<T> extends Element<Setting<T>, Consumer<Setting<T>>> {
    private final T defaultValue;
    private T raw;
    private UnaryOperator<T> inspector = UnaryOperator.identity();

    protected Setting(T defaultValue, T value) {
        this.defaultValue = defaultValue;
        this.raw = value;
    }

    public static <T> Setting<T> make(T value, Consumer<Setting<T>> maker) {
        return edit(of(value), maker);
    }

    public static <T> Setting<T> make(T defaultValue, T value, Consumer<Setting<T>> maker) {
        return edit(of(defaultValue, value), maker);
    }

    public static <T> Setting<T> of(T defaultValue, T value) {
        return new Setting<>(defaultValue, value);
    }

    public static <T> Setting<T> of(T value) {
        return of(value, value);
    }

    public static <T> Setting<T> of(T defaultValue, T value, UnaryOperator<T> inspector) {
        return of(defaultValue, value).inspector(inspector);
    }

    public static <T> Setting<T> of(T value, UnaryOperator<T> inspector) {
        return of(value).inspector(inspector);
    }

    public static <T> Setting<T> ofLocked(T defaultValue, T value) {
        return of(defaultValue, value).lock(true);
    }

    public static <T> Setting<T> ofLocked(T value) {
        return of(value).lock(true);
    }

    public static <T> Setting<T> ofLocked(T defaultValue, T value, UnaryOperator<T> inspector) {
        return of(defaultValue, value, inspector).lock(true);
    }

    public static <T> Setting<T> ofLocked(T value, UnaryOperator<T> inspector) {
        return of(value, inspector).lock(true);
    }

    public T get() {
        return raw;
    }

    public <A> A get(Class<A> type) {
        return type.cast(type == String.class? String.valueOf(get()) : get());
    }

    public T getDefault() {
        return defaultValue;
    }

    public <A> A getDefault(Class<A> type) {
        return type.cast(type == String.class? String.valueOf(getDefault()) : getDefault());
    }

    public Setting<T> set(T value) {
        if (!locked()) {
            this.raw = inspector.apply(value);
            emit("on-set");
        }
        return this;
    }

    public Setting<T> edit(UnaryOperator<T> getter) {
        if (!locked()) {
            set(getter.apply(get()));
            emit("on-edit");
        }
        return this;
    }

    public Setting<T> reset() {
        if (!locked()) {
            set(defaultValue);
            emit("on-reset");
        }
        return this;
    }

    public Optional<T> optional() {
        return Optional.ofNullable(get());
    }

    public <A> Setting<A> mapTo(Function<T, A> mapper) {
        return of(mapper.apply(defaultValue), mapper.apply(raw)).lock(lock);
    }

    public <A> Setting<A> mapTo(Function<T, A> mapper, UnaryOperator<A> inspector) {
        return mapTo(mapper).inspector(inspector);
    }

    public boolean isOf(Class<?> type) {
        return type == Number.class? get() instanceof Number : get().getClass().isAssignableFrom(type);
    }

    public boolean isPrimitive() {
        return isOf(Number.class) || isOf(String.class) || isOf(Boolean.class) || isOf(Character.class) || get().getClass().isPrimitive();
    }

    public boolean isEnum() {
        return get().getClass().isEnum();
    }

    public boolean isArray() {
        return get().getClass().isArray();
    }

    public boolean isDefault() {
        return raw == defaultValue;
    }

    public Setting<T> inspector(UnaryOperator<T> inspector) {
        if (inspector != null) this.inspector = inspector;
        return this;
    }

    @Override
    public Setting<T> clone() {
        return new Setting<>(defaultValue, raw).lock(lock).inspector(inspector).on(new HashMap<>(events));
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        return buffer.toString();
    }
}