package animedouyin.animedouyinapi;

import animedouyin.Domain.User.Info.UserAccount;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootTest
@EnableWebFlux
class AnimeDouyinApiApplicationTests {
    private final Logger logger = LoggerFactory.getLogger(AnimeDouyinApiApplicationTests.class);
    @Autowired
    ReactiveMongoTemplate mongoTemplate;
    @Test
    void contextLoads() {
        mongoTemplate.find(Query.query(Criteria.where("name").regex("^a")), UserAccount.class, "UserAccount").doOnEach(user -> logger.info(user.get().getName())).doOnError(Throwable::printStackTrace).subscribe();
    }

}
