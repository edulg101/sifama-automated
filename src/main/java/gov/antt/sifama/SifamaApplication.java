package gov.antt.sifama;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SifamaApplication implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(SifamaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
