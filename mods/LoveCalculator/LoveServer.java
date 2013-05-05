package LoveCalculator;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
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

        Handler<HttpServerRequest> httpHandler = new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest request) {
                switch (request.path) {
                    case "/":
                        request.response.sendFile(File.separator + "web" + File.separator + "loveCalculator.html");
                        break;
                    case "/getLove":
                        if (!request.params().containsKey("name1") || !request.params().containsKey("name2")) {
                            request.response.statusCode = 400;
                            request.response.statusMessage = "Bad Request, name1 and name2 needed.";
                            request.response.end();
                        } else if (request.params().get("name1").isEmpty() || request.params().get("name2").isEmpty()) {
                            request.response.statusCode = 400;
                            request.response.statusMessage = "Bad Request, name1 or name2 empty.";
                            request.response.end();
                        } else {
                            logger.info("LoveServer.java: Names are: " + request.params());

                            JsonObject json = new JsonObject()
                                    .putString("name1", request.params().get("name1"))
                                    .putString("name2", request.params().get("name2"));

                            Handler<Message<JsonObject>> responseHandler = new Handler<Message<JsonObject>>() {
                                public void handle(Message<JsonObject> message) {
                                    logger.info("LoveServer.java: received calculation " + message.body);
                                    request.response.headers().put("Content-Type", "application/json");
                                    request.response.end(message.body.toString());
                                }

                            };

                            bus.send("loveCalculation", json, responseHandler);
                        }
                        break;
                    default:
                        request.response.statusCode = 404;
                        request.response.statusMessage = "URL not found";
                        request.response.end();
                }
            }
        };

        server.requestHandler(httpHandler);
        server.listen(8080, "localhost");
    }
}
