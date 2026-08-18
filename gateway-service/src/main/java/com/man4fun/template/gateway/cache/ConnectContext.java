package com.man4fun.template.gateway.cache;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectContext {
	private String channelKey;
	private ChannelHandlerContext channelHandlerContext;
	private String openId;
	private long userId;
	private Map<String, String> routerMap;

	public ConnectContext(String channelKey, ChannelHandlerContext context) {
		this.channelKey = channelKey;
		this.channelHandlerContext = context;
	}
}
