package com.crescendocollective.yelpscraper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
public class RestaurantReview {

    private Restaurant restaurant;

    private List<RestaurantReviewItem> reviews;

}
