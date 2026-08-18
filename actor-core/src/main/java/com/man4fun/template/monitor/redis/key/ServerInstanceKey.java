package com.man4fun.template.monitor.redis.key;

public class ServerInstanceKey {
	public static final String SERVER_INSTANCE_KEY_FORMAT = "server:instance:%s:%s";

	public static String genServerInstanceKey(String serviceType,
			String address) {
		return String.format(SERVER_INSTANCE_KEY_FORMAT, serviceType, address);
	}
}
