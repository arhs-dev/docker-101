package com.example.accessingdatamysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class AccessingDataMysqlApplication {
	private static final Logger log = LoggerFactory.getLogger(AccessingDataMysqlApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataMysqlApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {
			// save a few customers
			repository.save(new User("Jack Bauer", "jack@ctu.gov.us"));
			repository.save(new User("Chloe O'Brian", "cloe@ctu.gov.us"));
			repository.save(new User("Kim Bauer", "kim@ctu.gov.us"));
			repository.save(new User("David Palmer", "palmer@usa.gov"));
			repository.save(new User("Michelle Dessler", "michelle@ctu.gov.us"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (User user : repository.findAll()) {
				log.info(user.toString());
			}
			log.info("");

		};
	}


}
