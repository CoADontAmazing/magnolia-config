package dev.coa.magnolia.config;

public interface ElementTag {

    static ElementTag of(String name) {
        return new ElementTag() {
            @Override
            public String toString() {
                return name;
            }
        };
    }
}