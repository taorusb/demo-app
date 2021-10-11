package com.taorusb.demoapp.service.impl;

import com.jayway.jsonpath.JsonPath;
import com.taorusb.demoapp.api.client.GiphyClient;
import com.taorusb.demoapp.service.GifService;
import com.taorusb.demoapp.service.OpenExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
public class GifServiceImpl implements GifService {

    private final GiphyClient giphyClient;
    private final OpenExchangeRatesService openExchangeRatesService;
    private final String downloadLinkTemplate;

    @Autowired
    public GifServiceImpl(GiphyClient giphyClient, OpenExchangeRatesService openExchangeRatesService,
                          @Value("${giphy.download.link}") String downloadLinkTemplate) {
        this.giphyClient = giphyClient;
        this.openExchangeRatesService = openExchangeRatesService;
        this.downloadLinkTemplate = downloadLinkTemplate;
    }

    @Override
    public byte[] getResult(String currency) {
        if (openExchangeRatesService.isTodayMoreThanYesterday(currency)) {
            return getResultFromString(giphyClient.getRichResult());
        }
        return getResultFromString(giphyClient.getBrokeResult());
    }

    private byte[] getResultFromString(String s) {
        String id = JsonPath.parse(s).read("$.data.id");
        String url = String.format(downloadLinkTemplate, id);
        try {
            byte[] result = new URL(url).openStream().readAllBytes();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}