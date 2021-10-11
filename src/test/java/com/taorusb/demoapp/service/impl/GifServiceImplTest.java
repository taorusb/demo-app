package com.taorusb.demoapp.service.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.taorusb.demoapp.service.GifService;
import com.taorusb.demoapp.service.OpenExchangeRatesService;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@ActiveProfiles("test")
class GifServiceImplTest {

    @Autowired
    GifService gifService;
    @MockBean
    OpenExchangeRatesService openExchangeRatesService;
    static WireMockServer wireMockServer;

    byte[] richBody = {1, 2, 3};
    byte[] brokeBody = {3, 2, 1};

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
    void getResult_returns_rich() {
        Mockito.when(openExchangeRatesService.isTodayMoreThanYesterday("USD")).thenReturn(true);
        String path = "/giphy/rich";
        String downloadPath = "/media/1/giphy.gif";
        stubFor(get(path).willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("giphy-rich.json")));
        stubFor(get(downloadPath).willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader("Content-Type", "image/gif")
                .withBody(richBody)));
        assertArrayEquals(richBody, gifService.getResult("USD"));
    }

    @Test
    void getResult_returns_broke() {
        Mockito.when(openExchangeRatesService.isTodayMoreThanYesterday("USD")).thenReturn(false);
        String path = "/giphy/broke";
        String downloadPath = "/media/2/giphy.gif";
        stubFor(get(path).willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("giphy-broke.json")));
        stubFor(get(downloadPath).willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader("Content-Type", "image/gif")
                .withBody(brokeBody)));
        assertArrayEquals(brokeBody, gifService.getResult("USD"));
    }

    @Test
    void getResult_throws_exception() {
        Mockito.when(openExchangeRatesService.isTodayMoreThanYesterday("USD")).thenReturn(false);
        String path = "/giphy/broke";
        String downloadPath = "/media/2/giphy.gif";
        stubFor(get(path).willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("giphy-broke.json")));
        stubFor(get(downloadPath).willReturn(aResponse()
                .withStatus(HttpStatus.SC_NOT_FOUND)
                .withHeader("Content-Type", "image/gif")
                .withBody(brokeBody)));
        assertThrows(RuntimeException.class, () -> gifService.getResult("USD"));
    }
}