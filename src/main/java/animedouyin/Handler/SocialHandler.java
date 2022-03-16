package animedouyin.Handler;

import animedouyin.Domain.Dto.FriendReq;
import animedouyin.Domain.User.ExecuteInfo.UserCrud;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class SocialHandler {
    private final UserCrud userCrud;
    public Mono<ServerResponse> addFriend(ServerRequest request) {
        return request.bodyToMono(FriendReq.class).flatMap(req -> userCrud.addFriend(req.getEmail(), req.getEmailFriend())).flatMap(list -> {
            var listFr = this.convertRespFr(list.get(0));
            var queueFr = this.convertRespFr(list.get(1));
            var reslt = new JSONObject();
            reslt.appendField("listFriend", listFr);
            reslt.appendField("queueFriend", queueFr);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt);
        });
    }

    public Mono<ServerResponse> unFriend(ServerRequest request) {
        return request.bodyToMono(FriendReq.class).flatMap(req -> userCrud.unFriend(req.getEmail(), req.getEmailFriend())).flatMap(list -> {
            var reslt = this.convertRespFr(list);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt);
        });
    }

    public Mono<ServerResponse> addFrToQueue(ServerRequest request) {
        return request.bodyToMono(FriendReq.class).flatMap(req -> userCrud.addFriendToQueue(req.getEmail(), req.getEmailFriend()).flatMap(list -> {
            var reslt = this.convertRespFr(list);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt);
        }));
    }

    public Mono<ServerResponse> getQueueFr(ServerRequest request) {
        return userCrud.getFrQueue(request.queryParam("email").get()).flatMap(list -> {
            var reslt = this.convertRespFr(list);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt);
        });
    }
    private JSONArray convertRespFr(ArrayList<String> list) {
        var jsonArr = new JSONArray();
        list.forEach(ele -> {
            userCrud.getUser(ele).doOnSuccess(usr -> {
                var jsonObj = new JSONObject();
                jsonObj.appendField("name" , usr.getName());
                jsonObj.appendField("image", usr.getAttributes().get("image").toString());
                jsonObj.appendField("email", usr.getEmail());
                jsonArr.appendElement(jsonObj);
            }).subscribe();
        });
        return jsonArr;
    }
    public Mono<ServerResponse> getAllFriend(ServerRequest serverRequest) {
        return userCrud.getUser(serverRequest.queryParam("email").get()).flatMap(user -> {
            var reslt = this.convertRespFr(user.getListFriend());
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt);
        });
    }
    public Mono<ServerResponse> unAddFrReq(ServerRequest serverRequest) {
        return userCrud.unAddFrReq(serverRequest.queryParam("email").get(), serverRequest.queryParam("emailFr").get()).flatMap(list -> ServerResponse.ok().bodyValue(list));
    }
}
