package animedouyin.Router;

import animedouyin.Handler.HomeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
@RequiredArgsConstructor
public class HomeRouter {
    @Bean
    public RouterFunction<ServerResponse> redirect(HomeHandler homeHandleFunction) {
        return route().GET("/redirect",homeHandleFunction::redirect).build();
    }
    @Bean
    public RouterFunction<ServerResponse> getUserInfo(HomeHandler homeHandleFunction) {
        return route().GET("/getUserInfo", homeHandleFunction::getUserInfo).build();
    }
    @Bean
    public RouterFunction<ServerResponse> feedBack(HomeHandler homeHandler) {
        return route().POST("/feedBack", homeHandler::feedBack).build();
    }
    @Bean
    public RouterFunction<ServerResponse> getAllVideoLiked(HomeHandler homeHandler) {
        return route().GET("/getAllVideoLiked", homeHandler::getAllVideoLiked).build();
    }
    @Bean
    public RouterFunction<ServerResponse> deleteUser(HomeHandler homeHandler) {
        return route().DELETE("/deleteUser", homeHandler::deleteUser).build();
    }
    @Bean
    public RouterFunction<ServerResponse> searchFrByName(HomeHandler homeHandler) {
        return route().GET("/searchUsrByPrefixName", homeHandler::searchFrByPrefixName).build();
    }
}
