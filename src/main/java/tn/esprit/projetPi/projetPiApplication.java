package tn.esprit.projetPi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class projetPiApplication {

	public static void main(String[] args) {
		SpringApplication.run(projetPiApplication.class, args);
	}

}
