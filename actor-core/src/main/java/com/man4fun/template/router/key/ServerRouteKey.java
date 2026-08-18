package com.man4fun.template.router.key;

public class ServerRouteKey {
	public static final String SERVER_ROUTE_KEY_FORMAT = "server:route:%s:%s";

	public static String genServerRouteKey(String serverName, String actorId) {
		return String.format(SERVER_ROUTE_KEY_FORMAT, serverName, actorId);
	}
}
