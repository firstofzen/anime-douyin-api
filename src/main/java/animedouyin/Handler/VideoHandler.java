package animedouyin.Handler;

import animedouyin.Domain.Dto.UpdateComm;
import animedouyin.Domain.Dto.UpdateTymReq;
import lombok.RequiredArgsConstructor;
import animedouyin.Domain.ManageVideo;
import animedouyin.Domain.Video;
import animedouyin.Domain.VideoCrud;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class VideoHandler {
    private final VideoCrud videoCrud;
    private final ManageVideo manageVideo;
    private final Logger logger = LoggerFactory.getLogger(VideoHandler.class);
    public Mono<ServerResponse> updateTym(ServerRequest request) {
        return request.bodyToMono(UpdateTymReq.class).flatMap(req -> videoCrud.updateTym(req.getAsset_id(), req.getEmailLiked())).flatMap(reslt -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt)).doOnError(Throwable::printStackTrace);
    }

    public Mono<ServerResponse> updateComment(ServerRequest request) {
        return request.bodyToMono(UpdateComm.class).flatMap(comm -> videoCrud.updateComment(comm.getId_video(), comm.getEmail(), comm.getComment())).flatMap(reslt -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(reslt)).doOnError(Throwable::printStackTrace).switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> addAllVideo(ServerRequest request) {
        AtomicInteger count = new AtomicInteger();
        return Mono.just(manageVideo.getAllVideo()).flatMap(arr -> {
            arr.forEach(ele -> {
                if (!videoCrud.checkVideoExist(ele.get("asset_id").toString())) {
                    count.getAndIncrement();
                    videoCrud.addVideo(Video.builder().asset_id(ele.get("asset_id").toString()).url(ele.get("secure_url").toString()).comments(new JSONArray()).amountLike(0).listUserLiked(new ArrayList<>()).build()).subscribe();
                }
            });
            return ServerResponse.ok().bodyValue(count);
        }).switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> getAllVideo(ServerRequest request) {
        return videoCrud.getAllVideo().map(list -> {
            var json = new JSONArray();
            list.forEach(ele -> {
                var jsonObj = new JSONObject();
                jsonObj.appendField("listUserLiked", ele.getListUserLiked());
                jsonObj.appendField("amountLike", ele.getAmountLike());
                jsonObj.appendField("url", ele.getUrl());
                jsonObj.appendField("asset_id", ele.getAsset_id());
                jsonObj.appendField("comments", ele.getComments());
                json.appendElement(jsonObj);
            });
            return json;
        }).flatMap(json -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(json));
    }
}