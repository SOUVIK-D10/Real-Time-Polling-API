package com.sll.rtpollingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RealTimePollingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimePollingApiApplication.class, args);
	}

}
