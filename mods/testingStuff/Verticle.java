import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.logging.Logger;

public class Verticle extends org.vertx.java.deploy.Verticle {
    public void start() {
        // Verticle has access to the vertx and container object
        Logger logger = container.getLogger();
        EventBus bus = vertx.eventBus();
    }
}