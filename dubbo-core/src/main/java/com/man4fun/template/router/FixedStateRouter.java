package com.man4fun.template.router;

import java.util.Random;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.Holder;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.RouterSnapshotNode;
import org.apache.dubbo.rpc.cluster.router.state.AbstractStateRouter;
import org.apache.dubbo.rpc.cluster.router.state.BitList;

import com.man4fun.template.business.dubbo.ActorMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FixedStateRouter<T> extends AbstractStateRouter<T> {
	private final Random random = new Random();

	public FixedStateRouter(URL url) {
		super(url);
	}

	@Override
	protected BitList<Invoker<T>> doRoute(BitList<Invoker<T>> bitList, URL url,
			Invocation invocation, boolean needToPrintMessage,
			Holder<RouterSnapshotNode<T>> holder, Holder<String> messageHolder)
			throws RpcException {
		if (bitList == null || bitList.isEmpty()) {
			return bitList;
		}

		Object[] args = invocation.getArguments();
		if (args == null || args.length != 1) {
			return bitList;
		}

		if (invocation.getParameterTypes()[0] != ActorMsg.class) {
			return bitList;
		}

		String serviceName = invocation.getServiceName();
		ActorMsg msg = (ActorMsg) args[0];
		String actorId = msg.getActorId();
		String targetAddress = ServerRouteManager.getRouteAddress(serviceName,
				actorId, bitList);
		log.info(
				"FixedStateRouter  route serviceName:{} actorId:{} targetAddress:{}",
				serviceName, actorId, targetAddress);
		BitList<Invoker<T>> targetList = new BitList<>(BitList.emptyList());
		for (Invoker<T> invoker : bitList) {
			if (invoker.getUrl().getAddress().equals(targetAddress)) {
				targetList.add(invoker);
			}
		}

		return targetList;
	}
}
