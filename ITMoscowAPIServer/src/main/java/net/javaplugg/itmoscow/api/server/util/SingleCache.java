package net.javaplugg.itmoscow.api.server.util;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SingleCache<T> {

    public static final String KEY = "single_cache_key";

    @Delegate
    private final Cache<String, T> cache;

    public T get() {
        return cache.getIfPresent(KEY);
    }

    public void set(T t) {
        cache.put(KEY, t);
    }
}
