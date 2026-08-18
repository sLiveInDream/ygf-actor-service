package com.man4fun.template.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.router.ServerRouteManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Activate(group = { CommonConstants.CONSUMER })
public class ClearRouterCacheFilter implements Filter {
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation)
			throws RpcException {
		try {
			return invoker.invoke(invocation);
		} catch (RpcException e) {
			// 是网络错误 清掉原本的路由，触发重试
			if (e.isTimeout() || e.isNetwork()) {
				Object[] args = invocation.getArguments();
				if (args != null && args.length == 1 && invocation
						.getParameterTypes()[0] == ActorMsg.class) {
					String serviceName = invocation.getServiceName();
					ActorMsg msg = (ActorMsg) args[0];
					String actorId = msg.getActorId();
					ServerRouteManager.removeRoute(serviceName, actorId);
					log.error(
							"ClearRouterCacheFilter occur! serviceName:{},actorId:{}",
							serviceName, actorId, e);
				}
			}

			throw e;
		}
	}
}
