package com.man4fun.template.router;

import java.util.Random;

import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.router.state.BitList;
import org.springframework.beans.factory.InitializingBean;

import com.man4fun.template.router.cache.ServerRouteCache;
import com.man4fun.template.router.key.ServerRouteKey;
import com.man4fun.template.router.redis.ServerRouteDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerRouteManager implements InitializingBean {
	private static ServerRouteManager instance;
	private final Random random = new Random();
	private final ServerRouteDao serverRouteDao;

	public ServerRouteManager(ServerRouteDao serverRouteDao) {
		this.serverRouteDao = serverRouteDao;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

	public static void removeRoute(String serviceName, String actorId) {
		String key = ServerRouteKey.genServerRouteKey(serviceName, actorId);
		ServerRouteCache.remove(key);
		if (instance.serverRouteDao != null) {
			instance.serverRouteDao.deleteRoute(key);
		}
	}

	public static <T> String getRouteAddress(String serviceName, String actorId,
			BitList<Invoker<T>> bitList) {
		// 先从本地缓存查, 有就返回
		String key = ServerRouteKey.genServerRouteKey(serviceName, actorId);
		String cache = ServerRouteCache.get(key);
		if (!StringUtils.isEmpty(cache)) {
			return cache;
		}

		// 再从redis查, 有就返回 且刷新cache
		if (instance.serverRouteDao != null) {
			String redisCache = instance.serverRouteDao.getRouteAddress(key);
			if (!StringUtils.isEmpty(redisCache)) {
				ServerRouteCache.putIfAbsent(key, redisCache);
				return redisCache;
			}
		}

		// 查不到=还未分配
		// 根据invokeList 及 redis中服务状态，选择一个
		String targetAddress = loadBalanceInvoker(bitList);
		if (StringUtils.isEmpty(targetAddress)) {
			log.error("cant find valid server from {}", bitList.size());
			return null;
		}

		if (instance.serverRouteDao != null) {
			return instance.serverRouteDao.setRouteAddressIfAbsent(key,
					targetAddress);
		} else {
			return ServerRouteCache.putIfAbsent(key, targetAddress);
		}

	}

	private static <T> String loadBalanceInvoker(BitList<Invoker<T>> bitList) {
		if (bitList == null || bitList.isEmpty()) {
			return "";
		}

		// 拉取bitList所有服务的状态数据
		// 筛选出指定版本的服务
		// 从中随机一个
		int index = instance.random.nextInt(bitList.size());
		return bitList.get(index).getUrl().getAddress();
	}
}
