package com.kalew515.utils;

import java.util.Collection;
import java.util.List;

public class CollectionUtil {

    public static boolean isEmpty (Collection<?> c) {
        return c == null || c.isEmpty();
    }


    public static <T> T first (Collection<T> values) {
        if (isEmpty(values)) {
            return null;
        }
        if (values instanceof List) {
            List<T> list = (List<T>) values;
            return list.get(0);
        } else {
            return values.iterator().next();
        }
    }
}
