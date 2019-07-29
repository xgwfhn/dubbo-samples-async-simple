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

package org.apache.dubbo.samples.async;

import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.samples.async.api.AsyncService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/async-consumer.xml"})
public class AsyncServiceIT {
    @Autowired
    private AsyncService asyncService;

    @Test
    public void testCompletableFuture() throws Exception {
        asyncService.sayHello("completable future");
        CompletableFuture<String> helloFuture = RpcContext.getContext().getCompletableFuture();
        CountDownLatch latch = new CountDownLatch(1);
        helloFuture.whenComplete((retValue, exception) -> {
        	System.out.println("retValue="+retValue);
            Assert.assertNull(exception);
            Assert.assertEquals("hello, completable future", retValue);
            latch.countDown();
        });
        System.out.println("111111111111");
        latch.await();
    }

    
    
    //这种写法是同步的  并且获取不到返回值
    @Test
    public void testAsyncCall_() throws Exception {
    	 RpcContext.getContext().asyncCall(() -> {
             String s=asyncService.sayHello("one way call request1");
             System.out.println("获取值="+s);
         });
    	 System.out.println("调用完成------------");
    }
    
    
    //这种写法是同步的  可以获取到返回值
    @Test
    public void testAsyncCall() throws Exception {
    	 CompletableFuture<String> f = RpcContext.getContext().asyncCall(() -> asyncService.sayHello("async call request"));
         System.out.println("async call returned: " + f.get());
    	 System.out.println("调用完成------------");
    }
    
    
    //程序先执行,服务接口进行异步调用,调用完后 获取返回值
    //需要在客户端配置<dubbo:method name="sayHello" async="true"  timeout="30000" />
    @Test
    public void testAsyncCallReturn() throws Exception {
    	 asyncService.sayHello("completable future");
         CompletableFuture<String> helloFuture = RpcContext.getContext().getCompletableFuture();
         CountDownLatch latch = new CountDownLatch(1);
         helloFuture.whenComplete((retValue, exception) -> {
             //Assert.assertNull(exception);
             //Assert.assertEquals("hello, completable future", retValue);
        	 System.out.println("获取异步返回值="+retValue);
             latch.countDown();
         });
         System.out.println("wancheng-------------:");
         latch.await();
    }
}
