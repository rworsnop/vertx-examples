package io.vertx.example;

import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rworsnop on 9/2/15.
 */
@Configuration
public class VertxBeans {
    @Bean
    protected VertxOptions vertxOptions(@Value("${eventLoopSize:}") Integer eventLoopSize){
        VertxOptions options = new VertxOptions();
        if (eventLoopSize != null){
            options.setEventLoopPoolSize(eventLoopSize);
        }
        return options;
    }

    @Bean
    public Vertx vertx(VertxOptions options) {
        return Vertx.vertx(options);
    }

    @Bean
    public EventBus eventBus(Vertx vertx){
        return vertx.eventBus();
    }
}