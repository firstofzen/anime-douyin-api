package animedouyin.Domain.User.Info;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "UserAccount")
@Builder
@Getter
@Setter
public class UserAccount implements OAuth2User{
    private @MongoId String id;
    private @Field("roles") List<String> roles;
    private @Field("refreshToken") String refreshToken;
    private @Field("email") String email;
    private @Field("listFriend") JSONArray listFriend;
    private @Field("queueAddFr") JSONArray queueAddFr;
    private @Field("notification") ArrayList<JSONObject> notification;
    private @Field("videosLiked") ArrayList<String> videosLiked;
    private @Field("attributes") Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
