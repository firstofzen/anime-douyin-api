package animedouyin.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateComm {
    private String id_video;
    private String comment;
    private String email;
}
