package animedouyin.Domain;

import animedouyin.Domain.User.Info.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class VideoCrud {
    private final ReactiveMongoTemplate mongoTemplate;
    private final Logger logger = LoggerFactory.getLogger(VideoCrud.class);
    public Mono<Video> addVideo(@NonNull Video vid) {
        Assert.notNull(vid, "vid not null");
        return mongoTemplate.save(vid);
    }

    public Mono<Video> getVideoByAsset_id(@NonNull String asset_id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("asset_id").is(asset_id)), Video.class, "Video");
    }

    public Mono<JSONArray> updateComment(@NonNull String id_video,@NonNull String email,@NonNull String comment) {
        var comments = new AtomicReference<JSONArray>();
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), UserAccount.class, "UserAccount").flatMap(acc -> mongoTemplate.findOne(Query.query(Criteria.where("asset_id").is(id_video)), Video.class, "Video").flatMap(vid -> {
            var com = vid.getComments();
            var jsonCm = new JSONObject();
            jsonCm.appendField("name", acc.getName());
            jsonCm.appendField("text", comment);
            jsonCm.appendField("image",acc.getAttributes().get("image").toString());
            com.appendElement(jsonCm);
            comments.set(com);
            var update = new Update();
            update.set("comments",com);
            return mongoTemplate.updateFirst(Query.query(Criteria.where("asset_id").is(id_video)), update, Video.class, "Video").map(resl -> comments.get());
        })).doOnError(Throwable::printStackTrace);
    }
    @SneakyThrows
    public Boolean checkVideoExist(String asset_id) {
        return mongoTemplate.exists(Query.query(Criteria.where("asset_id").is(asset_id)), Video.class, "Video").toFuture().get();
    }
    public Mono<JSONObject> updateTym(@NonNull String asset_id, @NonNull String email) {
        return this.getVideoByAsset_id(asset_id).flatMap(vid -> {
            var updateDef = new Update();
            if(!vid.getListUserLiked().contains(email)) {
                updateDef.set("amountLike", (vid.getAmountLike() + 1));
                var listLiked = vid.getListUserLiked();
                listLiked.add(email);
                updateDef.set("listUserLiked", listLiked);
            } else {
                updateDef.set("amountLike", (vid.getAmountLike() - 1));
                var listULiked = vid.getListUserLiked();
                listULiked.remove(email);
                updateDef.set("listUserLiked", listULiked);
            }
            return mongoTemplate.updateFirst(Query.query(Criteria.where("asset_id").is(asset_id)),updateDef, Video.class, "Video").flatMap(resl -> mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), UserAccount.class, "UserInfo")).flatMap(userInfo -> {
                var upd = new Update();
                var videosLiked = userInfo.getVideosLiked();
                videosLiked.add(asset_id);
                upd.set("videosLiked", videosLiked);
                return mongoTemplate.updateFirst(Query.query(Criteria.where("email").is(email)), upd, UserAccount.class, "UserInfo");
            }).flatMap(upresl -> this.getVideoByAsset_id(asset_id)).map(reslt -> {
                var json = new JSONObject();
                json.appendField("amountLike", reslt.getAmountLike());
                json.appendField("listUserLiked", reslt.getListUserLiked());
                return json;
            });
        });
    }

    public Mono<List<Video>> getAllVideo() {
        return mongoTemplate.findAll(Video.class, "Video").collectList();
    }
}

