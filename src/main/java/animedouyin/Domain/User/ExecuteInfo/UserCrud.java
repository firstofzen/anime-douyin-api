package animedouyin.Domain.User.ExecuteInfo;

import animedouyin.Domain.User.Info.UserAccount;
import animedouyin.Domain.Video;
import animedouyin.Domain.VideoCrud;
import com.mongodb.client.result.DeleteResult;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCrud {
    private final ReactiveMongoTemplate mongoTemplate;
    private final VideoCrud videoCrud;
    private final Logger logger = LoggerFactory.getLogger(UserCrud.class);

    public Mono<UserAccount> getUser(String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), UserAccount.class, "UserAccount").doOnError(Throwable::printStackTrace).switchIfEmpty(Mono.empty());
    }

    @SneakyThrows
    public Mono<Boolean> checkUserExist(String email) {
        return mongoTemplate.exists(Query.query(Criteria.where("email").is(email)), UserAccount.class, "UserAccount");
    }

    public Mono<UserAccount> saveUser(UserAccount userAccount) {
        return this.checkUserExist(userAccount.getEmail()).flatMap(res -> {
            if (res) {
                return this.getUser(userAccount.getEmail());
            }
            return mongoTemplate.save(Mono.just(userAccount), "UserAccount");
        });
    }

    public Mono<DeleteResult> deleteUser(String email) {
        return mongoTemplate.remove(Query.query(Criteria.where("email").is(email)), UserAccount.class, "UserAccount");
    }

    public Mono<List<ArrayList<String>>> addFriend(@NonNull String email, @NonNull String emailFriend) {
        return this.getUser(email).flatMap(userInfo -> {
            var upda = new Update();
            var listFr = userInfo.getListFriend();
            var queueFr = userInfo.getQueueAddFr();
            listFr.add(emailFriend);
            queueFr.removeIf(ele -> ele.equals(emailFriend));
            upda.set("listFriend", listFr);
            upda.set("queueAddFr", queueFr);
            return mongoTemplate.updateFirst(Query.query(Criteria.where("email").is(email)), upda, UserAccount.class, "UserAccount").map(reslt -> List.of(listFr, queueFr));
        });
    }

    public Mono<ArrayList<String>> unFriend(@NonNull String email, @NonNull String emailFriend) {
        return this.getUser(emailFriend).flatMap(userInfo -> {
            var listFr = userInfo.getListFriend();
            listFr.remove(email);
            var upd = new Update();
            upd.set("listFriend", listFr);
            return mongoTemplate.updateFirst(Query.query(Criteria.where("email").is(emailFriend)),upd,UserAccount.class, "UserAccount");
        }).flatMap(rsud ->
                this.getUser(email).flatMap(userInfo -> {
                    var upd = new Update();
                    var listFr = userInfo.getListFriend();
                    listFr.remove(emailFriend);
                    upd.set("listFriend", listFr);
                    return mongoTemplate.updateFirst(Query.query(Criteria.where("email").is(email)), upd, UserAccount.class, "UserAccount").map(reslt -> listFr);
                }));
    }

    public Mono<ArrayList<String>> addFriendToQueue(@NonNull String email, @NonNull String emailFriend) {
        return this.getUser(emailFriend).flatMap(userInfo -> {
            var upda = new Update();
            var queueFr = userInfo.getQueueAddFr();
            var notifi = userInfo.getNotification();
            if(!queueFr.contains(email)){queueFr.add(email);}
            var isRead = notifi.get(0);
            if (isRead.getAsString("isRead").equals("false")) {
                var jsonObj1__ = new JSONObject();
                jsonObj1__.appendField("isRead", "true");
                notifi.set(0, jsonObj1__);
            }
            var jsonObj1 = new JSONObject();
            var jsonObj1_ = new JSONObject();
            jsonObj1_.appendField("name", userInfo.getName());
            jsonObj1_.appendField("email", userInfo.getEmail());
            jsonObj1.appendField("addFrReq", jsonObj1_);
            notifi.add(jsonObj1);
            upda.set("queueAddFr", queueFr);
            upda.set("notification", notifi);
            return mongoTemplate.updateFirst(Query.query(Criteria.where("email").is(email)), upda, UserAccount.class, "UserAccount").map(reslt -> queueFr);
        });
    }

    public Mono<ArrayList<String>> getFrQueue(String email) {
        return this.getUser(email).map(UserAccount::getQueueAddFr);
    }

    public Mono<JSONArray> getAllVideoLiked(String email) {
        return this.getUser(email).map(UserAccount::getVideosLiked).map(arr -> {
            var listVidLiked = new ArrayList<Video>();
            arr.forEach(ele -> {
                try {
                    listVidLiked.add(videoCrud.getVideoByAsset_id(ele).toFuture().get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            var jsonArr = new JSONArray();
            listVidLiked.forEach(ele -> {
                var jsonObj = new JSONObject();
                jsonObj.appendField("amountLike", ele.getAmountLike());
                jsonObj.appendField("comments", ele.getComments());
                jsonObj.appendField("url", ele.getUrl());
                jsonObj.appendField("listUserLiked", ele.getListUserLiked());
                jsonObj.appendField("asset_id", ele.getAsset_id());
                jsonArr.appendElement(jsonObj);
            });
            return jsonArr;
        });
    }

    public Mono<ArrayList<String>> unAddToQueue(@NonNull String email, @NonNull String emailFr) {
        AtomicReference<ArrayList<String>> list = new AtomicReference<>();
        return this.getUser(emailFr).flatMap(usrFr -> {
            var queueFr = usrFr.getQueueAddFr();
            var upd = new Update();
            queueFr.removeIf(ele -> ele.equals(email));
            upd.set("queueAddFr", queueFr);
            list.set(queueFr);
            return mongoTemplate.updateFirst(Query.query(Criteria.where("email").is(emailFr)), upd, UserAccount.class);
        }).map(updR -> list.get());
    }

    public Flux<JSONObject> getUserByPrefixName(@NonNull String prefix, @NonNull String email) {
        return mongoTemplate.find(Query.query(Criteria.where("name").regex("^" + prefix)), UserAccount.class, "UserAccount").filter(ele -> !ele.getEmail().contains(email)).map(ele -> {
            var jsonObj = new JSONObject();
            jsonObj.appendField("image", ele.getAttributes().get("image").toString());
            jsonObj.appendField("name", ele.getName());
            jsonObj.appendField("email", ele.getEmail());
            jsonObj.appendField("listFriend", ele.getListFriend());
            jsonObj.appendField("queueFriend", ele.getQueueAddFr());
            return jsonObj;
        });
    }
}
