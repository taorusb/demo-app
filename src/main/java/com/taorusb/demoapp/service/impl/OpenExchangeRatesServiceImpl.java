package com.taorusb.demoapp.service.impl;

import com.jayway.jsonpath.JsonPath;
import com.taorusb.demoapp.api.client.OpenExchangeRatesClient;
import com.taorusb.demoapp.service.OpenExchangeRatesService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OpenExchangeRatesServiceImpl implements OpenExchangeRatesService {

    private final OpenExchangeRatesClient openExchangeRatesClient;

    @Autowired
    public OpenExchangeRatesServiceImpl(OpenExchangeRatesClient openExchangeRatesClient) {
        this.openExchangeRatesClient = openExchangeRatesClient;
    }

    @Override
    public boolean isTodayMoreThanYesterday(String currency) {
        try {
            Double yesterdayCourse = JsonPath.parse(getHistoricalCourse(currency)).read("$.rates.['RUB']");
            Double todayCourse = JsonPath.parse(getCurrentCourse(currency)).read("$.rates.['RUB']");
            return todayCourse > yesterdayCourse;
        } catch (FeignException e) {
            int status = e.status();
            if (status == HttpStatus.BAD_REQUEST.value()) {
                throw new IllegalArgumentException(e);
            }
            throw new RuntimeException(e);
        }
    }

    private String getCurrentCourse(String s) {
        return openExchangeRatesClient.getCurrentCourse(s);
    }

    private String getHistoricalCourse(String s) {
        LocalDate localDate = LocalDate.now().minusDays(1);
        return openExchangeRatesClient.getHistoricalCourse(localDate, s);
    }
}