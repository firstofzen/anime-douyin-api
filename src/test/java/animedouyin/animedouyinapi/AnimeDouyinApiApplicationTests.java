package animedouyin.animedouyinapi;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
class AnimeDouyinApiApplicationTests {
    private final Logger log = LoggerFactory.getLogger(AnimeDouyinApiApplicationTests.class);
    @Test
    void contextLoads() {
        Mono.just("hihi").filter(ele -> ele.equals("hi")).doOnNext(log::warn).subscribe();
    }
}
