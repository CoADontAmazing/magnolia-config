package dev.coa.magnolia.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class Element<T extends Element, E extends Consumer<T>> implements EventListener<E> {
    protected Section parent;

    protected String path;
    public String name;
    public String comment;

    protected boolean lock;
    protected Set<ElementTag> tags;
    protected final Map<String, E> events = new HashMap<>();

    public static <E extends Element> E edit(E element, Consumer<E> editor) {
        editor.accept(element);
        return element;
    }

    protected Setting<?> asSetting() {
        return (Setting) this;
    }

    protected Section asSection() {
        return (Section) this;
    }

    public T name(String name) {
        if (!locked()) {
            this.name = name;
            emit("on-name");
        }
        return (T) this;
    }

    public T comment(String comment) {
        if (!locked()) {
            this.comment = comment;
            emit("on-comment");
        }
        return (T) this;
    }

    public T lock(boolean should) {
        this.lock = should;
        emit("on-lock");
        return (T) this;
    }

    public boolean locked() {
        return lock || (parent != null && parent.lock);
    }

    public Section parent() {
        return parent;
    }

    @Override
    public T on(String event, E callback) {
        return (T) EventListener.super.on(event, callback);
    }

    @Override
    public T on(Map<String, E> map) {
        return (T) EventListener.super.on(map);
    }

    @Override
    public T off(String... events) {
        return (T) EventListener.super.off(events);
    }

    @Override
    public void emit(String event) {
        if (events.containsKey(event) && events.get(event) != null) events.get(event).accept((T) this);
    }

    @Override
    public Map<String, E> events() {
        return events;
    }

    public T tags(ElementTag... tags) {
        return tags(tags != null? Set.of(tags) : null);
    }

    public T tags(Collection<ElementTag> tags) {
        if (!locked() && tags != null) {
            this.tags = Set.copyOf(tags);
            emit("on-tags");
        }
        return (T) this;
    }

    public Set<ElementTag> tags() {
        return hasTags()? tags : Set.of();
    }

    public boolean hasTag(ElementTag tag) {
        return hasTags() && tags.contains(tag);
    }

    public boolean hasTags() {
        return tags != null;
    }

    public abstract T clone();
}