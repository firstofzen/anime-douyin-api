package animedouyin.Handler;

import animedouyin.Domain.Dto.FeedBack;
import animedouyin.Domain.SendMail;
import lombok.RequiredArgsConstructor;
import animedouyin.Domain.User.ExecuteInfo.UserCrud;
import animedouyin.Domain.User.Info.UserAccount;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class HomeHandler {
    private final UserCrud userCrud;
    private final SendMail sendMail;

    public Mono<ServerResponse> redirect(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext().flatMap(this::saveUser).flatMap(userInfo ->
                ServerResponse.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create("https://anime-douyin.vercel.app" + "?email=" + userInfo.getEmail() + "&image=" + userInfo.getAttributes().get("image").toString())).build());
    }

    private Mono<UserAccount> saveUser(SecurityContext securityContext) {
        var oidcUser = (DefaultOidcUser) securityContext.getAuthentication().getPrincipal();
        var map = new HashMap<String, Object>();
        map.put("image", oidcUser.getUserInfo().getPicture());
        map.put("name", oidcUser.getUserInfo().getFullName());
        var nottifi = new ArrayList<JSONObject>();
        var jsonObj = new JSONObject();
        jsonObj.appendField("isRead", "false");
        nottifi.add(0, jsonObj);
        return userCrud.saveUser(UserAccount.builder()
                .roles(oidcUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .refreshToken(UUID.randomUUID().toString())
                .attributes(map)
                .email(oidcUser.getUserInfo().getEmail())
                .listFriend(new JSONArray())
                .notification(nottifi)
                .queueAddFr(new JSONArray())
                .videosLiked(new ArrayList<>())
                .build());
    }

    public Mono<ServerResponse> getUserInfo(ServerRequest request) {
        return userCrud.getUser(request.queryParam("email").get()).doOnError(Throwable::printStackTrace).flatMap(usr -> {
            var json = new JSONObject();
            json.appendField("name", usr.getName());
            json.appendField("refreshToken", usr.getRefreshToken());
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(json);
        }).doOnError(Throwable::printStackTrace).switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> feedBack(ServerRequest request) {
        return request.bodyToMono(FeedBack.class).flatMap(fb -> sendMail.sendMessage(fb.getEmail(), fb.getText())).flatMap(resl -> ServerResponse.ok().build());
    }


    public Mono<ServerResponse> getAllVideoLiked(ServerRequest request) {
        return userCrud.getAllVideoLiked(request.queryParam("email").get()).flatMap(reslt -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        return userCrud.deleteUser(String.valueOf(serverRequest.queryParam("email"))).flatMap(reslt -> ServerResponse.ok().bodyValue(reslt)).switchIfEmpty(ServerResponse.badRequest().build());
    }
}
