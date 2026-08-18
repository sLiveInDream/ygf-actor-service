package com.man4fun.template.router.redis.impl;

import org.springframework.data.redis.core.RedisTemplate;

import com.man4fun.template.router.redis.ServerRouteDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerRouteDaoImpl implements ServerRouteDao {

	private final RedisTemplate<String, String> redisTemplate;

	public ServerRouteDaoImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String getRouteAddress(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	// todo 用lua脚本合并成单次请求
	@Override
	public String setRouteAddressIfAbsent(String key, String value) {
		if (Boolean.TRUE
				.equals(redisTemplate.opsForValue().setIfAbsent(key, value))) {
			return value;
		} else {
			return redisTemplate.opsForValue().get(key);
		}
	}

	@Override
	public void deleteRoute(String key) {
		redisTemplate.delete(key);
	}
}
