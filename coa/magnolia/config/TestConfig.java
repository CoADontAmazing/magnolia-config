package dev.coa.magnolia.config;

import java.security.SecureRandom;

import static java.lang.System.*;

public class TestConfig {
    public static void main(String[] args) {
        Section world = Section.empty();

        ElementTag BASIC = ElementTag.of("basic");

        var level = world.addSetting("level", 0, value -> Math.clamp(value, 0, 200))
                .tags(BASIC)
                .on("on-set", it -> out.printf("%s%s: %s%n", it.name, it.tags, it.get(String.class)))
                .set(5);

        var bag = world.createSection("bag");

        var named = bag.addSetting("acceptItems", false)
                .name("Can accept items");

        bag.addSetting("size", 38.2f, 39.2f, 100.2f)
                .on("on-set", it -> out.println("size: " + it.get()))
                .set(1.0f);

        bag.addSetting("tier", 0L, 0L, 10L)
                .on("on-set", it -> out.println("tier: " + it.get()))
                .set(1L);

        bag.lock(false);

        out.println(world.keys());
        out.println(bag.keys());
        out.println(named);
        //out.println(world.hasPath("bag/tier"));

        Setting<Boolean> sz = bag.get("acceptItems");
        sz.set(new SecureRandom().nextBoolean());

        out.println(sz.name);
        out.println(sz.comment);
        out.println(sz.get());

        level.set(321)
                .edit(old -> old + 34);
    }
}