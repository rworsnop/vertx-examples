package io.vertx.example;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
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
    private Router router;

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
    @Value("${busInstances:1}")
    public void setBusInstances(int instances) {
        this.busInstances = instances;
    }

    @Resource
    public void setRouter(Router router) {
        this.router = router;
    }

    @Resource
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @PostConstruct
    public void createServer(){
        for (int i =0; i < httpServerInstances; i++){
            vertx.createHttpServer().requestHandler(router::accept).listen(port);
        }
        for (int i = 0; i < busInstances; i++){
            vertx.eventBus().localConsumer("testaddress").handler(message ->
                    message.reply("Processed " + message.body() + " in " + Thread.currentThread().getName()));

        }
    }
}
