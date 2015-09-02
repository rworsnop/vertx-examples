package io.vertx.example;

import io.vertx.core.AsyncResult;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rworsnop on 9/2/15.
 */
@Configuration
public class VertxBeans {

    @Bean
    protected VertxOptions vertxOptions(
            @Value("${eventLoopSize:}") Integer eventLoopSize,
            @Value("${clustered:false}") boolean clustered){
        VertxOptions options = new VertxOptions();
        options.setClustered(clustered);
        if (eventLoopSize != null){
            options.setEventLoopPoolSize(eventLoopSize);
        }
        return options;
    }

    @Bean
    public Vertx vertx(VertxOptions options) throws Throwable {
        if (options.isClustered()){
            return clusteredVertx(options);
        } else{
            return Vertx.vertx(options);
        }
    }

    private Vertx clusteredVertx(VertxOptions options) throws Throwable {
        AtomicReference<AsyncResult<Vertx>> result = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Vertx.clusteredVertx(options, ar -> {
            result.set(ar);
            countDownLatch.countDown();

        });
        countDownLatch.await();
        AsyncResult<Vertx> ar = result.get();
        if (ar.succeeded()){
            return ar.result();
        } else{
            throw ar.cause();
        }
    }

    @Bean
    public EventBus eventBus(Vertx vertx){
        return vertx.eventBus();
    }
}
