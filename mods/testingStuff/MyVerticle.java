package testingStuff;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Verticle;

public class MyVerticle extends Verticle {
    public void start() {
        // MyVerticle has access to the vertx and container object
        Logger logger = container.getLogger();
        EventBus bus = vertx.eventBus();
    }
}