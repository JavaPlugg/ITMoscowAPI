package net.javaplugg.itmoscow.api.server.util;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SetCache<T> {

    public static final String VALUE = "set_cache_value";

    @Delegate
    private final Cache<T, String> cache;

    public void add(T t) {
        cache.put(t, VALUE);
    }

    public void remove(T t) {
        cache.invalidate(t);
    }

    public boolean contains(T t) {
        return Objects.equals(cache.getIfPresent(t), VALUE);
    }
}
