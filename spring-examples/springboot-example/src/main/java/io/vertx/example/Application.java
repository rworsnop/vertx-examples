package io.vertx.example;


import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
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
        router.get("/bus").handler(context ->
                vertx.eventBus().sendObservable("testaddress", context.request().params().get("message"))
                    .subscribe(
                            msg -> context.response().end(msg.body().toString() + "\n"),
                            throwable -> context.fail(500)));
        return router;
    }

}
