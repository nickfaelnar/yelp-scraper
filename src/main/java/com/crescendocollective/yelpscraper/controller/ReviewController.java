package com.crescendocollective.yelpscraper.controller;

import com.crescendocollective.yelpscraper.constants.ReviewSite;
import com.crescendocollective.yelpscraper.dto.RestaurantReview;
import com.crescendocollective.yelpscraper.dto.YelpRequestV1;
import com.crescendocollective.yelpscraper.dto.YelpRequestV2;
import com.crescendocollective.yelpscraper.factory.ReviewServiceFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewServiceFactory reviewServiceFactory;

    @GetMapping("/v1/yelp/{restaurantAlias}")
    @ApiOperation(value = "Get Restaurant Reviews", notes = "Scrapes Yelp Page")
    public RestaurantReview getYelpReview(@PathVariable String restaurantAlias) {
        return reviewServiceFactory.create(ReviewSite.YELPV1)
                .getReview(new YelpRequestV1(restaurantAlias));
    }

    @GetMapping("/v2/yelp/{restaurantId}")
    @ApiOperation(value = "Get Restaurant Reviews", notes = "Uses Yelp Exposed API")
    public RestaurantReview getYelpReviewV2(@PathVariable String restaurantId) {
        return reviewServiceFactory.create(ReviewSite.YELPV2)
                .getReview(new YelpRequestV2(restaurantId));
    }

}
