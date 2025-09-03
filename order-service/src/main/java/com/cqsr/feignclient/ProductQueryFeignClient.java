//package com.cqsr.feignclient;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@FeignClient(name = "store-query")
//public interface ProductQueryFeignClient {
//
//	@GetMapping("/api/v1/products/{id}")
//	ProductResponse getProduct(@PathVariable (name = "id") Long productId);
//}
