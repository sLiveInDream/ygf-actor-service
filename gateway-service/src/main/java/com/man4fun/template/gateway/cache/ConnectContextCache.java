package com.man4fun.template.gateway.cache;

import java.util.Map;

import com.google.common.collect.Maps;

import io.netty.channel.ChannelHandlerContext;

public class ConnectContextCache {
	private static Map<String, ConnectContext> connectContextMap = Maps
			.newConcurrentMap();
	private static Map<Long, String> user2channelKeyMap = Maps
			.newConcurrentMap();

	public static void put(String channelKey, ConnectContext connectContext) {
		connectContextMap.put(channelKey, connectContext);
		if (connectContext.getUserId() != 0) {
			user2channelKeyMap.put(connectContext.getUserId(), channelKey);
		}
	}

	public static ConnectContext get(String channelKey) {
		return connectContextMap.get(channelKey);
	}

	public static void remove(String channelKey) {
		ConnectContext removedContext = connectContextMap.remove(channelKey);
		if (removedContext != null && removedContext.getUserId() != 0) {
			user2channelKeyMap.remove(removedContext.getUserId());
		}
	}

	public static ChannelHandlerContext getChannelHandlerContext(long userId) {
		String channelKey = user2channelKeyMap.get(userId);
		if (channelKey == null || channelKey.isEmpty()) {
			return null;
		}
		ConnectContext connectContext = connectContextMap.get(channelKey);
		if (connectContext == null) {
			return null;
		}
		return connectContext.getChannelHandlerContext();
	}
}
