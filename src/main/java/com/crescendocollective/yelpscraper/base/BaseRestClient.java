package com.crescendocollective.yelpscraper.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

@Getter @Setter
public class BaseRestClient {

    private RestTemplate restTemplate;

    public BaseRestClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

}
