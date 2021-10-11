package com.taorusb.demoapp.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@FeignClient(name = "${openexchangerates.name}", url = "${openexchangerates.url}")
public interface OpenExchangeRatesClient {

    @GetMapping("${openexchangerates.current.course}")
    String getCurrentCourse(@PathVariable(name = "currency") String currencyCode);

    @GetMapping("${openexchangerates.yesterday.course}")
    String getHistoricalCourse(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                               @PathVariable(name = "currency") String currencyCode);
}