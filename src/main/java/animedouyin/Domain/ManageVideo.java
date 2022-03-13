package animedouyin.Domain;

import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ManageVideo {
    private final Cloudinary cloudinary;
    @SneakyThrows
    public ArrayList<Map<String, Object>> getAllVideo() {
        return (ArrayList<Map<String, Object>>) cloudinary.search().expression("folder:videos/*").execute().get("resources");
    }
}
