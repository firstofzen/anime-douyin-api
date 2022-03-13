package animedouyin.Domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SendMail {
    @Autowired JavaMailSender javaMailSender;
    private final Logger log = LoggerFactory.getLogger(SendMail.class);
    public Mono<Void> sendMessage(@NonNull String email, String text) {
        log.info("feed back user : {}", text);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("firstofzen@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Hi Guy Bro " + email);
        mailMessage.setText("Thanks you for feed back , from anime-douyin with love ");
        javaMailSender.send(mailMessage);
        return Mono.empty();
    }
}
