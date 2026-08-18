/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.man4fun.template.business.dubbo;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.PathResolver;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.ServerService;
import org.apache.dubbo.rpc.TriRpcStatus;
import org.apache.dubbo.rpc.model.MethodDescriptor;
import org.apache.dubbo.rpc.model.ServiceDescriptor;
import org.apache.dubbo.rpc.model.StubMethodDescriptor;
import org.apache.dubbo.rpc.model.StubServiceDescriptor;
import org.apache.dubbo.rpc.service.Destroyable;
import org.apache.dubbo.rpc.stub.BiStreamMethodHandler;
import org.apache.dubbo.rpc.stub.ServerStreamMethodHandler;
import org.apache.dubbo.rpc.stub.StubInvocationUtil;
import org.apache.dubbo.rpc.stub.StubInvoker;
import org.apache.dubbo.rpc.stub.StubMethodHandler;
import org.apache.dubbo.rpc.stub.StubSuppliers;
import org.apache.dubbo.rpc.stub.UnaryStubMethodHandler;

import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.concurrent.CompletableFuture;

public final class DubboPlayerServiceTriple {

    public static final String SERVICE_NAME = PlayerService.SERVICE_NAME;

    private static final StubServiceDescriptor serviceDescriptor = new StubServiceDescriptor(SERVICE_NAME,PlayerService.class);

    static {
        org.apache.dubbo.rpc.protocol.tri.service.SchemaDescriptorRegistry.addSchemaDescriptor(SERVICE_NAME,ActorServiceProto.getDescriptor());
        StubSuppliers.addSupplier(SERVICE_NAME, DubboPlayerServiceTriple::newStub);
        StubSuppliers.addSupplier(PlayerService.JAVA_SERVICE_NAME,  DubboPlayerServiceTriple::newStub);
        StubSuppliers.addDescriptor(SERVICE_NAME, serviceDescriptor);
        StubSuppliers.addDescriptor(PlayerService.JAVA_SERVICE_NAME, serviceDescriptor);
    }

    @SuppressWarnings("all")
    public static PlayerService newStub(Invoker<?> invoker) {
        return new PlayerServiceStub((Invoker<PlayerService>)invoker);
    }

    private static final StubMethodDescriptor handleActorMsgMethod = new StubMethodDescriptor("HandleActorMsg",
    com.man4fun.template.business.dubbo.ActorMsg.class, com.man4fun.template.business.dubbo.ActorResponse.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.man4fun.template.business.dubbo.ActorMsg::parseFrom,
    com.man4fun.template.business.dubbo.ActorResponse::parseFrom);

    private static final StubMethodDescriptor handleActorMsgAsyncMethod = new StubMethodDescriptor("HandleActorMsg",
    com.man4fun.template.business.dubbo.ActorMsg.class, java.util.concurrent.CompletableFuture.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.man4fun.template.business.dubbo.ActorMsg::parseFrom,
    com.man4fun.template.business.dubbo.ActorResponse::parseFrom);

    private static final StubMethodDescriptor handleActorMsgProxyAsyncMethod = new StubMethodDescriptor("HandleActorMsgAsync",
    com.man4fun.template.business.dubbo.ActorMsg.class, com.man4fun.template.business.dubbo.ActorResponse.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.man4fun.template.business.dubbo.ActorMsg::parseFrom,
    com.man4fun.template.business.dubbo.ActorResponse::parseFrom);




    static{
        serviceDescriptor.addMethod(handleActorMsgMethod);
        serviceDescriptor.addMethod(handleActorMsgProxyAsyncMethod);
    }

    public static class PlayerServiceStub implements PlayerService, Destroyable {
        private final Invoker<PlayerService> invoker;

        public PlayerServiceStub(Invoker<PlayerService> invoker) {
            this.invoker = invoker;
        }

        @Override
        public void $destroy() {
              invoker.destroy();
         }

        @Override
        public com.man4fun.template.business.dubbo.ActorResponse handleActorMsg(com.man4fun.template.business.dubbo.ActorMsg request){
            return StubInvocationUtil.unaryCall(invoker, handleActorMsgMethod, request);
        }

        public CompletableFuture<com.man4fun.template.business.dubbo.ActorResponse> handleActorMsgAsync(com.man4fun.template.business.dubbo.ActorMsg request){
            return StubInvocationUtil.unaryCall(invoker, handleActorMsgAsyncMethod, request);
        }

        public void handleActorMsg(com.man4fun.template.business.dubbo.ActorMsg request, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse> responseObserver){
            StubInvocationUtil.unaryCall(invoker, handleActorMsgMethod , request, responseObserver);
        }



    }

    public static abstract class PlayerServiceImplBase implements PlayerService, ServerService<PlayerService> {

        private <T, R> BiConsumer<T, StreamObserver<R>> syncToAsync(java.util.function.Function<T, R> syncFun) {
            return new BiConsumer<T, StreamObserver<R>>() {
                @Override
                public void accept(T t, StreamObserver<R> observer) {
                    try {
                        R ret = syncFun.apply(t);
                        observer.onNext(ret);
                        observer.onCompleted();
                    } catch (Throwable e) {
                        observer.onError(e);
                    }
                }
            };
        }

        @Override
        public CompletableFuture<com.man4fun.template.business.dubbo.ActorResponse> handleActorMsgAsync(com.man4fun.template.business.dubbo.ActorMsg request){
                return CompletableFuture.completedFuture(handleActorMsg(request));
        }

        /**
        * This server stream type unary method is <b>only</b> used for generated stub to support async unary method.
        * It will not be called if you are NOT using Dubbo3 generated triple stub and <b>DO NOT</b> implement this method.
        */
        public void handleActorMsg(com.man4fun.template.business.dubbo.ActorMsg request, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse> responseObserver){
            handleActorMsgAsync(request).whenComplete((r, t) -> {
                if (t != null) {
                    responseObserver.onError(t);
                } else {
                    responseObserver.onNext(r);
                    responseObserver.onCompleted();
                }
            });
        }

        @Override
        public final Invoker<PlayerService> getInvoker(URL url) {
            PathResolver pathResolver = url.getOrDefaultFrameworkModel()
            .getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
            Map<String,StubMethodHandler<?, ?>> handlers = new HashMap<>();

            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/HandleActorMsg");
            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/HandleActorMsgAsync");
            // for compatibility
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/HandleActorMsg");
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/HandleActorMsgAsync");


            BiConsumer<com.man4fun.template.business.dubbo.ActorMsg, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse>> handleActorMsgFunc = this::handleActorMsg;
            handlers.put(handleActorMsgMethod.getMethodName(), new UnaryStubMethodHandler<>(handleActorMsgFunc));
            BiConsumer<com.man4fun.template.business.dubbo.ActorMsg, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse>> handleActorMsgAsyncFunc = syncToAsync(this::handleActorMsg);
            handlers.put(handleActorMsgProxyAsyncMethod.getMethodName(), new UnaryStubMethodHandler<>(handleActorMsgAsyncFunc));




            return new StubInvoker<>(this, url, PlayerService.class, handlers);
        }


        @Override
        public com.man4fun.template.business.dubbo.ActorResponse handleActorMsg(com.man4fun.template.business.dubbo.ActorMsg request){
            throw unimplementedMethodException(handleActorMsgMethod);
        }





        @Override
        public final ServiceDescriptor getServiceDescriptor() {
            return serviceDescriptor;
        }
        private RpcException unimplementedMethodException(StubMethodDescriptor methodDescriptor) {
            return TriRpcStatus.UNIMPLEMENTED.withDescription(String.format("Method %s is unimplemented",
                "/" + serviceDescriptor.getInterfaceName() + "/" + methodDescriptor.getMethodName())).asException();
        }
    }

}
