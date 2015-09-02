package io.vertx.example;


import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.Router;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by rworsnop on 8/31/15.
 */
@Component
public class Server {

    private Vertx vertx;
    private EventBus eventBus;

    private int port;
    private int httpServerInstances;
    private int busInstances;

    @Value("${port:8080}")
    public void setPort(int port) {
        this.port = port;
    }
    @Value("${httpServers:1}")
    public void setHttpServerInstances(int instances) {
        this.httpServerInstances = instances;
    }
    @Value("${busConsumers:1}")
    public void setBusInstances(int instances) {
        this.busInstances = instances;
    }

    @Resource
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }
    @Resource
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void createServer(){
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
                eventBus.sendObservable("testaddress", context.request().params().get("message"))
                        .subscribe(
                                msg -> context.response().end(msg.body().toString() + "\n"),
                                throwable -> context.fail(500)));

        for (int i =0; i < httpServerInstances; i++){
            vertx.createHttpServer().requestHandler(router::accept).listen(port);
        }
        for (int i = 0; i < busInstances; i++){
            eventBus.localConsumer("testaddress").handler(message ->
                    message.reply("Processed " + message.body() + " in " + Thread.currentThread().getName()));

        }
    }
}
