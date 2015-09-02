package io.vertx.example;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        // This is basically the same example as the web-examples staticsite example but it's booted using
        // SpringBoot, not Vert.x
        SpringApplication.run(Application.class, args);
    }

    @Bean
    protected VertxOptions vertxOptions(@Value("${workerPoolSize:10}") int workerPoolSize){
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(workerPoolSize);
        return options;
    }

    @Bean
    public Vertx vertx(VertxOptions options) {
        return Vertx.vertx(options);
    }

    @Bean
    public Router router(Vertx vertx){
        Router router = Router.router(vertx);

        // /hello
        router.get("/hello").handler(context -> context.response()
                .end("Hello World from thread " + Thread.currentThread().getName() + "\n"));

        // /timer?delay=1500
        router.get("/timer").handler(context ->
                vertx.setTimer(Integer.valueOf(context.request().params().get("delay")),
                        id -> context.response().end(Thread.currentThread().getName() + "\n")));
        // /bus?message=foo
        router.get("/bus").handler(context ->{
            vertx.eventBus().send("testaddress", context.request().params().get("message"), result -> {
                if (result.succeeded()){
                    context.response().end(result.result().body().toString()+"\n");
                } else{
                    context.fail(result.cause());
                }
            });
        });
        return router;
    }

}
