package com.man4fun.template.gateway.connect;

import io.netty.channel.Channel;

public class ChannelUtil {
	public static String getChannelKey(Channel channel) {
		return NettyConfig.INSTANCE.SERVER_ID + "-" + channel.id().asLongText();
	}
}
