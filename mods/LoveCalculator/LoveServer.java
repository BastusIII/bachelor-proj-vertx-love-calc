package LoveCalculator;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Verticle;

import java.io.File;

public class LoveServer extends Verticle {

    public void start() {
        final Logger logger = container.getLogger();

        logger.info("LoveServer.java: initializing.");
        final EventBus bus = vertx.eventBus();
        final HttpServer server = vertx.createHttpServer();
        final RouteMatcher routeMatcher = new RouteMatcher();

        Handler<HttpServerRequest> indexHandler = new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest request) {
                request.response.sendFile("." + File.separator + "web" + File.separator + "LoveCalculator.html");
            }
        };
        Handler<HttpServerRequest> defaultHandler = new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest request) {
                request.response.statusCode = 404;
                request.response.end("URL not found");
            }
        };
        Handler<HttpServerRequest> loveHandler = new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest request) {
                if (!request.params().containsKey("name1") || !request.params().containsKey("name2")) {
                    logger.info("LoveServer.java: Bad request.");
                    request.response.statusCode = 400;
                    request.response.end("Bad Request, name1 and name2 needed.");
                } else if (request.params().get("name1").isEmpty() || request.params().get("name2").isEmpty()) {
                    logger.info("LoveServer.java: Bad request.");
                    request.response.statusCode = 400;
                    request.response.end("Bad Request, name1 or name2 empty.");
                } else {
                    logger.info("LoveServer.java: Names are: " + request.params());

                    JsonObject json = new JsonObject()
                            .putString("name1", request.params().get("name1"))
                            .putString("name2", request.params().get("name2"));
                    // define handler for response on bus
                    Handler<Message<JsonObject>> responseHandler = new Handler<Message<JsonObject>>() {
                        public void handle(Message<JsonObject> message) {
                            logger.info("LoveServer.java: received calculation " + message.body);
                            request.response.headers().put("Content-Type", "application/json");
                            request.response.end(message.body.toString());
                        }

                    };
                    // send generated json with names to the bus
                    bus.send("loveCalculation", json, responseHandler);
                }
            }
        };

        routeMatcher.allWithRegEx("/(index(\\.htm(l)*)*)*", indexHandler);
        routeMatcher.all("/getLove", loveHandler);
        routeMatcher.noMatch(defaultHandler);

        server.requestHandler(routeMatcher);
        server.listen(8080, "localhost");
    }
}
