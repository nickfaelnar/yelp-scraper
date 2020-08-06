package com.crescendocollective.yelpscraper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class RestaurantReviewItem {

    private User user;

    private int rating;

    private String review;

}
