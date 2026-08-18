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

public final class DubboGatewayServiceTriple {

    public static final String SERVICE_NAME = GatewayService.SERVICE_NAME;

    private static final StubServiceDescriptor serviceDescriptor = new StubServiceDescriptor(SERVICE_NAME,GatewayService.class);

    static {
        org.apache.dubbo.rpc.protocol.tri.service.SchemaDescriptorRegistry.addSchemaDescriptor(SERVICE_NAME,ActorServiceProto.getDescriptor());
        StubSuppliers.addSupplier(SERVICE_NAME, DubboGatewayServiceTriple::newStub);
        StubSuppliers.addSupplier(GatewayService.JAVA_SERVICE_NAME,  DubboGatewayServiceTriple::newStub);
        StubSuppliers.addDescriptor(SERVICE_NAME, serviceDescriptor);
        StubSuppliers.addDescriptor(GatewayService.JAVA_SERVICE_NAME, serviceDescriptor);
    }

    @SuppressWarnings("all")
    public static GatewayService newStub(Invoker<?> invoker) {
        return new GatewayServiceStub((Invoker<GatewayService>)invoker);
    }

    private static final StubMethodDescriptor notifyMethod = new StubMethodDescriptor("Notify",
    com.man4fun.template.business.dubbo.ServiceNotify.class, com.man4fun.template.business.dubbo.ActorResponse.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.man4fun.template.business.dubbo.ServiceNotify::parseFrom,
    com.man4fun.template.business.dubbo.ActorResponse::parseFrom);

    private static final StubMethodDescriptor notifyAsyncMethod = new StubMethodDescriptor("Notify",
    com.man4fun.template.business.dubbo.ServiceNotify.class, java.util.concurrent.CompletableFuture.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.man4fun.template.business.dubbo.ServiceNotify::parseFrom,
    com.man4fun.template.business.dubbo.ActorResponse::parseFrom);

    private static final StubMethodDescriptor notifyProxyAsyncMethod = new StubMethodDescriptor("NotifyAsync",
    com.man4fun.template.business.dubbo.ServiceNotify.class, com.man4fun.template.business.dubbo.ActorResponse.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.man4fun.template.business.dubbo.ServiceNotify::parseFrom,
    com.man4fun.template.business.dubbo.ActorResponse::parseFrom);




    static{
        serviceDescriptor.addMethod(notifyMethod);
        serviceDescriptor.addMethod(notifyProxyAsyncMethod);
    }

    public static class GatewayServiceStub implements GatewayService, Destroyable {
        private final Invoker<GatewayService> invoker;

        public GatewayServiceStub(Invoker<GatewayService> invoker) {
            this.invoker = invoker;
        }

        @Override
        public void $destroy() {
              invoker.destroy();
         }

        @Override
        public com.man4fun.template.business.dubbo.ActorResponse notify(com.man4fun.template.business.dubbo.ServiceNotify request){
            return StubInvocationUtil.unaryCall(invoker, notifyMethod, request);
        }

        public CompletableFuture<com.man4fun.template.business.dubbo.ActorResponse> notifyAsync(com.man4fun.template.business.dubbo.ServiceNotify request){
            return StubInvocationUtil.unaryCall(invoker, notifyAsyncMethod, request);
        }

        public void notify(com.man4fun.template.business.dubbo.ServiceNotify request, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse> responseObserver){
            StubInvocationUtil.unaryCall(invoker, notifyMethod , request, responseObserver);
        }



    }

    public static abstract class GatewayServiceImplBase implements GatewayService, ServerService<GatewayService> {

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
        public CompletableFuture<com.man4fun.template.business.dubbo.ActorResponse> notifyAsync(com.man4fun.template.business.dubbo.ServiceNotify request){
                return CompletableFuture.completedFuture(notify(request));
        }

        /**
        * This server stream type unary method is <b>only</b> used for generated stub to support async unary method.
        * It will not be called if you are NOT using Dubbo3 generated triple stub and <b>DO NOT</b> implement this method.
        */
        public void notify(com.man4fun.template.business.dubbo.ServiceNotify request, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse> responseObserver){
            notifyAsync(request).whenComplete((r, t) -> {
                if (t != null) {
                    responseObserver.onError(t);
                } else {
                    responseObserver.onNext(r);
                    responseObserver.onCompleted();
                }
            });
        }

        @Override
        public final Invoker<GatewayService> getInvoker(URL url) {
            PathResolver pathResolver = url.getOrDefaultFrameworkModel()
            .getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
            Map<String,StubMethodHandler<?, ?>> handlers = new HashMap<>();

            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/Notify");
            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/NotifyAsync");
            // for compatibility
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/Notify");
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/NotifyAsync");


            BiConsumer<com.man4fun.template.business.dubbo.ServiceNotify, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse>> notifyFunc = this::notify;
            handlers.put(notifyMethod.getMethodName(), new UnaryStubMethodHandler<>(notifyFunc));
            BiConsumer<com.man4fun.template.business.dubbo.ServiceNotify, StreamObserver<com.man4fun.template.business.dubbo.ActorResponse>> notifyAsyncFunc = syncToAsync(this::notify);
            handlers.put(notifyProxyAsyncMethod.getMethodName(), new UnaryStubMethodHandler<>(notifyAsyncFunc));




            return new StubInvoker<>(this, url, GatewayService.class, handlers);
        }


        @Override
        public com.man4fun.template.business.dubbo.ActorResponse notify(com.man4fun.template.business.dubbo.ServiceNotify request){
            throw unimplementedMethodException(notifyMethod);
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
