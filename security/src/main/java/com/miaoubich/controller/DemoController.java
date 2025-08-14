package com.miaoubich.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/v1/auth/test-endpoint")
public class DemoController {

	@GetMapping
	public ResponseEntity<String> testMethod(){
		return ResponseEntity.ok("This is a test method from the secured endpoint!");
	}
}
