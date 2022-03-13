package animedouyin.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FriendReq {
    private String email;
    private String image;
    private String emailFriend;
}
