package musai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MusaiManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(MusaiManagementApplication.class, args);
	}
}