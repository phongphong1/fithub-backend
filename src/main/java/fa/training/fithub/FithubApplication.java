package fa.training.fithub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FithubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FithubApplication.class, args);
    }

}
