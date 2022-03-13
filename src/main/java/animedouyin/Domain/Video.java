package animedouyin.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONArray;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@Document(collection = "Video")
public class Video {
    private @Id String id;
    private @Field("url") String url;
    private @Field("asset_id") String asset_id;
    private @Field("amountLike") Integer amountLike;
    private @Field("comments") JSONArray comments;
    private @Field("listUserLiked") ArrayList<String> listUserLiked;
}
