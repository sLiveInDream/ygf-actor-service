package com.man4fun.template.router.redis;

/**
 * Server 级路由 DAO。
 *
 * 路由只保存地址：route:{serviceType}:{routeKey} -> address。 例如
 * route:service-player:player_10001 -> 10.0.0.11:20880。
 */
public interface ServerRouteDao {
	/**
	 * 查询 routeKey 当前绑定的服务实例地址。
	 */
	String getRouteAddress(String key);

	/**
	 * 首次创建路由。只有 key 不存在时才写入，避免并发登录/创建时覆盖已有归属。如果key已存在就返回现有的value，否则返回新写入的value
	 */
	String setRouteAddressIfAbsent(String key, String value);

	/**
	 * 删除指定key
	 *
	 */
	void deleteRoute(String key);
}
