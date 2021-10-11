package com.taorusb.demoapp.service.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.taorusb.demoapp.service.OpenExchangeRatesService;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@ActiveProfiles("test")
class OpenExchangeRatesServiceImplTest {

    @Autowired
    OpenExchangeRatesService openExchangeRatesService;
    static WireMockServer wireMockServer;

    LocalDate localDate = LocalDate.now().minusDays(1);
    String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String currency = "USD";
    String historicalPath = String.format("/openexchangerates/historical/%s/%s", date, currency);
    String currentPath = String.format("/openexchangerates/%s", currency);

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(8081)
        );
        wireMockServer.start();
        configureFor("localhost", 8081);
    }

    @AfterAll
    static void stopServer() {
        wireMockServer.stop();
    }

    @Test
    void isTodayMoreThanYesterday_returns_false() {
        stubFor(get(historicalPath)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("yesterday.json")));
        stubFor(get(currentPath)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("today.json")));
        assertFalse(openExchangeRatesService.isTodayMoreThanYesterday("USD"));
    }

    @Test
    void isTodayMoreThanYesterday_throws_exception() {
        stubFor(get(historicalPath)
                .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));
        assertThrows(IllegalArgumentException.class, () -> openExchangeRatesService.isTodayMoreThanYesterday("USD"));
    }
}