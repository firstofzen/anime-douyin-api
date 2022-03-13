package animedouyin.Handler;

import animedouyin.Domain.Dto.FriendReq;
import animedouyin.Domain.User.ExecuteInfo.UserCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SocialHandler {
    private final UserCrud userCrud;
    public Mono<ServerResponse> addFriend(ServerRequest request) {
        return request.bodyToMono(FriendReq.class).flatMap(req -> userCrud.addFriend(req.getEmail(), req.getEmailFriend(), req.getImage())).flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list));
    }

    public Mono<ServerResponse> unFriend(ServerRequest request) {
        return request.bodyToMono(FriendReq.class).flatMap(req -> userCrud.unFriend(req.getEmail(), req.getEmailFriend(), req.getImage())).flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list));
    }

    public Mono<ServerResponse> addFrToQueue(ServerRequest request) {
        return request.bodyToMono(FriendReq.class).flatMap(req -> userCrud.addFriendToQueue(req.getEmail(), req.getEmailFriend(), req.getImage()).flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list)));
    }

    public Mono<ServerResponse> getFrQueue(ServerRequest request) {
        return userCrud.getFrQueue(request.queryParam("email").get()).flatMap(reslt -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt));
    }
}
