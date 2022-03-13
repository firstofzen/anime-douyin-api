package animedouyin.Router;

import animedouyin.Handler.SocialHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SocialRouter {
    @Bean
    public RouterFunction<ServerResponse> addFriend(SocialHandler socialHandler) {
        return route().POST("/addFriend", socialHandler::addFriend).build();
    }

    @Bean
    public RouterFunction<ServerResponse> unFriend(SocialHandler socialHandler) {
        return route().DELETE("/unFriend", socialHandler::unFriend).build();
    }

    @Bean
    public RouterFunction<ServerResponse> addFrQueue(SocialHandler socialHandler) {
        return route().POST("/queueAddFr", socialHandler::addFrToQueue).build();
    }

    @Bean
    public RouterFunction<ServerResponse> getFrQueue(SocialHandler socialHandler) {
        return route().GET("/getFrQueue", socialHandler::getFrQueue).build();
    }

}
