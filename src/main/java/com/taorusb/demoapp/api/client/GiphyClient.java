package com.taorusb.demoapp.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${giphy.name}", url = "${giphy.url}")
public interface GiphyClient {

    @GetMapping("${giphy.get.result.with.rich.tag}")
    String getRichResult();

    @GetMapping("${giphy.get.result.with.broke.tag}")
    String getBrokeResult();
}