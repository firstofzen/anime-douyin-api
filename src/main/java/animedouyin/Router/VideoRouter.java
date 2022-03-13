package animedouyin.Router;

import animedouyin.Handler.VideoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class VideoRouter {
    @Bean
    public RouterFunction<ServerResponse> getAllVideo(VideoHandler videoHandler) {
        return route().GET("/getAllVideo", videoHandler::getAllVideo).build();
    }
    @Bean
    public RouterFunction<ServerResponse> updateTym(VideoHandler videohandler) {
        return route().PUT("/updateTym", videohandler::updateTym).build();
    }
    @Bean
    public RouterFunction<ServerResponse> updateComment(VideoHandler videoHandler) {
        return route().PUT("/updateComment", videoHandler::updateComment).build();
    }
    @Bean
    public RouterFunction<ServerResponse> addAllVideo(VideoHandler videoHandler) {
        return route().GET("/addAllVideo", videoHandler::addAllVideo).build();
    }
}
