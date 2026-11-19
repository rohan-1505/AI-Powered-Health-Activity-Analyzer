package com.fitness.ai;

import java.util.Map;

import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication

public class AiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiServiceApplication.class, args);
	}
	@Bean
    CommandLineRunner init(MongoTemplate mongoTemplate) {
        return args -> {
            Document doc = new Document("test", "hello Atlas")
                    .append("metadata", Map.of("step", 1, "status", "active"));
            mongoTemplate.save(doc, "test_collection");
             System.out.println(">>> SAVED TO ATLAS SUCCESSFULLY! <<<");
        };
	}

}

