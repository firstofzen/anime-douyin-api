package animedouyin.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateTymReq {
    private String asset_id;
    private String emailLiked;
}
