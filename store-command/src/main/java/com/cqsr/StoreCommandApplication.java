package com.cqsr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StoreCommandApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreCommandApplication.class, args);
	}

}
