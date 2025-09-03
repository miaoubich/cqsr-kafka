package com.cqsr.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "store-command")
public interface ProductCommandFeignClient {

	@PatchMapping("/api/v1/products/{id}/reduce")
    void reduceQuantity(@PathVariable Long id, @RequestParam int amount);

}
