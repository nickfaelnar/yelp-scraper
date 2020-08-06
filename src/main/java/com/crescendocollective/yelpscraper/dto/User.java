package com.crescendocollective.yelpscraper.dto;

import com.google.cloud.vision.v1.Likelihood;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {

    private String name;

    private String img;

    private Likelihood angerLikelihood = Likelihood.UNKNOWN;

    private Likelihood joyLikelihood = Likelihood.UNKNOWN;

    private Likelihood surpriseLikelihood = Likelihood.UNKNOWN;

    public User(String name, String img) {
        this.name = name;
        this.img = img;
    }

}
