package dev.coa.magnolia.config;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Section extends Element<Section, Consumer<Section>> {
    public static final String PATH_SEPARATOR = "/";

    private final Map<String, Element> raw;

    protected Section(Map<String, Element> map) {
        this.raw = map;
    }

    public static Section empty() {
        return of(new HashMap<>());
    }

    public static Section make(Consumer<Section> maker) {
        return edit(empty(), maker);
    }

    public static Section of(int capacity) {
        return new Section(new HashMap<>(capacity));
    }

    public static Section of(Map<String, Element> map) {
        return new Section(map);
    }

    public static Section copyOf(Section section) {
        return empty().name(section.name).comment(section.comment).addAll(section);
    }

    public static Section ofLocked(Map<String, Element> map) {
        return of(map).lock(true);
    }

    public static Section lockedCopyOf(Section section) {
        return copyOf(section).lock(true);
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> T get(String key) {
        return (T) raw.get(key);
    }

    public <T extends Element> T get(String key, T defaultValue) {
        T t = get(key);
        return t == null? defaultValue : t;
    }

    public <T extends Element> Optional<T> find(String key) {
        return Optional.ofNullable(get(key));
    }

    public <T extends Element> T add(String key, T element) {
        if (!locked()) {
            set(key, element);
            emit("on-add");
        }
        return element;
    }

    public <T> Setting<T> addSetting(String key, T value) {
        return add(key, Setting.of(value).name(key));
    }

    public <T> Setting<T> addSetting(String key, T value, UnaryOperator<T> inspector) {
        return addSetting(key, value).inspector(inspector);
    }

    public <T extends Number> Setting<T> addSetting(String key, T value, T min, T max) {
        return addSetting(key, value, val -> switch (val) {
            case Long l -> (T) (Object) Math.clamp(l, min.longValue(), max.longValue());
            case Double d -> (T) (Object) Math.clamp(d, min.doubleValue(), max.doubleValue());
            case Float f -> (T) (Object) Math.clamp(f, min.floatValue(), max.floatValue());
            case null, default -> (T) (Object) Math.clamp(val.intValue(), min.intValue(), max.intValue());
        });
    }

    public Section createSection(String key) {
        return createSection(key, new HashMap<>());
    }

    public Section createSection(String key, Map<String, Element> params) {
        return add(key, of(params).name(key));
    }

    public Section set(String key, Element element) {
        if (!locked()) {
            element.path = (path != null? path + PATH_SEPARATOR : "") + key;
            element.parent = this;
            raw.put(key, element);
            emit("on-set");
        }
        return this;
    }

    public Section edit(String key, UnaryOperator<Element> getter) {
        if (!locked()) {
            set(key, getter.apply(get(key)));
            emit("on-edit");
        }
        return this;
    }

    public Section addAll(Map<String, Element> map) {
        map.forEach(this::add);
        return this;
    }

    public Section addAll(Section section) {
        return addAll(section.asMap());
    }

    public boolean has(String key) {
        return raw.containsKey(key);
    }

    public boolean hasPath(String path) {
        if (path == null) return false;

        if (!path.contains(PATH_SEPARATOR)) return has(path);

        var points = path.split(PATH_SEPARATOR);
        var element = get(points[0]);

        if (element == null) return false;
        return element.asSection().hasPath(String.join(PATH_SEPARATOR, Arrays.copyOfRange(points, 1, points.length)));
    }

    public Section remove(String key) {
        if (!locked()) {
            var element = raw.remove(key);
            if (element != null) {
                element.parent = null;
                element.path = null;
            }
            emit("on-remove");
        }
        return this;
    }

    public Section clear() {
        if (!locked()) {
            raw.clear();
            emit("on-clear");
        }
        return this;
    }

    public int size() {
        return raw.size();
    }

    public Set<String> keys() {
        return raw.keySet();
    }

    public Set<Map.Entry<String, Element>> entries() {
        return raw.entrySet();
    }

    public Stream<Map.Entry<String, Element>> stream() {
        return entries().stream();
    }

    public Map<String, Element> asMap() {
        return Collections.unmodifiableMap(raw);
    }

    @Override
    public Section clone() {
        return new Section(raw).lock(lock).on(new HashMap<>(events));
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        return buffer.toString();
    }
}