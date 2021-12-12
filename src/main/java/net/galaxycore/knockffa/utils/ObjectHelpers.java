package net.galaxycore.knockffa.utils;

import java.util.Objects;

public class ObjectHelpers {

    public static <T> T objectOrDefault(T obj, T def) {
        if (Objects.isNull(obj))
            return def;
        return obj;
    }

}
