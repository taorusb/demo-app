package com.taorusb.demoapp.controller;

import com.taorusb.demoapp.service.GifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@RestController
@Validated
public class GifControllerV1 {

    private final GifService gifService;
    private final String defaultCurrencyValue;

    @Autowired
    public GifControllerV1(GifService gifService, @Value("${openexchangerates.default.currency.value}") String defaultCurrencyValue) {
        this.gifService = gifService;
        this.defaultCurrencyValue = defaultCurrencyValue;
    }

    @GetMapping(value = "/api/v1/result", produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity<byte[]> getResult(@RequestParam(name = "currency_code", required = false)
                                                        @Pattern(regexp = "[a-zA-Z]{3}$") String currencyCode) {
        if (Objects.isNull(currencyCode)) {
            currencyCode = defaultCurrencyValue;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        byte[] arr = gifService.getResult(currencyCode);
        return new ResponseEntity<>(arr, headers, HttpStatus.OK);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handleException() {
        return ResponseEntity.badRequest().build();
    }
}