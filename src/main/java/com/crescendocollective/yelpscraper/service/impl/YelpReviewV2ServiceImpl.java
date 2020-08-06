package com.crescendocollective.yelpscraper.service.impl;

import com.crescendocollective.yelpscraper.base.BaseRestClient;
import com.crescendocollective.yelpscraper.constants.GlobalConstants;
import com.crescendocollective.yelpscraper.dto.Restaurant;
import com.crescendocollective.yelpscraper.dto.RestaurantReview;
import com.crescendocollective.yelpscraper.dto.RestaurantReviewItem;
import com.crescendocollective.yelpscraper.dto.User;
import com.crescendocollective.yelpscraper.dto.YelpRequestV2;
import com.crescendocollective.yelpscraper.service.GoogleVisionService;
import com.crescendocollective.yelpscraper.service.YelpReviewV2Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class YelpReviewV2ServiceImpl extends BaseRestClient implements YelpReviewV2Service {

    private static final Logger logger = LoggerFactory.getLogger(YelpReviewV2ServiceImpl.class);



    @Autowired
    private GoogleVisionService googleVisionService;

    @Value("${yelp.review.api}")
    private String apiEndpoint;

    public YelpReviewV2ServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public RestaurantReview getReview(YelpRequestV2 request) {
        int start = 0;

        JSONObject jsonResponse = getResponse(String.format(apiEndpoint, request.getRestaurantId(), start));
        JSONArray reviews = jsonResponse.getJSONArray("reviews");

        // Get Restaurant Detail
        JSONObject restaurantJson = reviews.getJSONObject(0).getJSONObject("business");
        Restaurant restaurant = new Restaurant(restaurantJson.getString("name"));

        // Get Reviews
        List<RestaurantReviewItem> restaurantReviewItems = new ArrayList<>();
        restaurantReviewItems.addAll(getReviews(reviews));
        while(toFetchMore(jsonResponse.getJSONObject("pagination"))) {
            start += GlobalConstants.MAX_REVIEWS_PER_CALL;
            jsonResponse = getResponse(String.format(apiEndpoint, request.getRestaurantId(), start));
            restaurantReviewItems.addAll(getReviews(jsonResponse.getJSONArray("reviews")));
        }

        return new RestaurantReview(restaurant, restaurantReviewItems);
    }

    private JSONObject getResponse(String url) {
        String apiResponse = getRestTemplate().getForObject(url, String.class);
        return new JSONObject(apiResponse);
    }

    private List<RestaurantReviewItem> getReviews(JSONArray reviews) {
        List<RestaurantReviewItem> restaurantReviewItems = new ArrayList<>();
        reviews.iterator().forEachRemaining((r) -> {
            JSONObject review = (JSONObject) r;

            //Get Reviewer
            JSONObject reviewerJson = review.getJSONObject("user");
            User user = new User(reviewerJson.getString("markupDisplayName"),
                    reviewerJson.getString("src").replace("60s.jpg", "300s.jpg"));

             googleVisionService.getUserImgAnnotation(user);

            //Add review
            restaurantReviewItems.add(new RestaurantReviewItem(
                    user,
                    review.getInt("rating"),
                    review.getJSONObject("comment").getString("text")
            ));
        });
        return restaurantReviewItems;
    }

    private boolean toFetchMore(JSONObject pagination) {
        return ((pagination.getInt("startResult") + pagination.getInt("resultsPerPage")) < pagination.getInt("totalResults"));
    }

}
