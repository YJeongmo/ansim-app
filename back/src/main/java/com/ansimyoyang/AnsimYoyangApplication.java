package com.ansimyoyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class AnsimYoyangApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnsimYoyangApplication.class, args);
	}

}
