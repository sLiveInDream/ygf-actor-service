package com.man4fun.template.router.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRouteCache {
	private static final Map<String, String> cache = new ConcurrentHashMap<>();

	public static String get(String key) {
		return cache.get(key);
	}

	public static String putIfAbsent(String key, String value) {
		return cache.putIfAbsent(key, value);
	}

	public static void remove(String key) {
		cache.remove(key);
	}
}
