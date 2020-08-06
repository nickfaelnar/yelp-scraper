package com.crescendocollective.yelpscraper.service.impl;

import com.crescendocollective.yelpscraper.constants.GlobalConstants;
import com.crescendocollective.yelpscraper.dto.Restaurant;
import com.crescendocollective.yelpscraper.dto.RestaurantReview;
import com.crescendocollective.yelpscraper.dto.RestaurantReviewItem;
import com.crescendocollective.yelpscraper.dto.User;
import com.crescendocollective.yelpscraper.dto.YelpRequestV1;
import com.crescendocollective.yelpscraper.service.GoogleVisionService;
import com.crescendocollective.yelpscraper.service.YelpReviewV1Service;
import com.crescendocollective.yelpscraper.util.WebScraperUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YelpReviewV1ServiceImpl implements YelpReviewV1Service {

    private static final Logger logger = LoggerFactory.getLogger(YelpReviewV1ServiceImpl.class);

    @Autowired
    private GoogleVisionService googleVisionService;

    @Autowired
    private Environment env;

    @Value("${yelp.restaurant.url}")
    private String pageUrl;

    @Override
    public RestaurantReview getReview(YelpRequestV1 request) {
        WebScraperUtil scraperUtil = null;
        int start = 0;

        scraperUtil = getScraper(pageUrl, request.getRestaurantAlias(), start);

        // Get Restaurant Detail
        Restaurant restaurant = new Restaurant(scraperUtil.getTextBySelector(env.getProperty("yelp.restaurant.name.selector")));

        // Get Reviews
        List<RestaurantReviewItem> reviewsItems = getReviews(scraperUtil);
        while(toFetchMore(scraperUtil)) {
            start += GlobalConstants.MAX_REVIEWS_PER_CALL;
            scraperUtil = getScraper(pageUrl, request.getRestaurantAlias(), start);
            reviewsItems.addAll(getReviews(scraperUtil));
        }

        return new RestaurantReview(restaurant, reviewsItems);
    }

    private WebScraperUtil getScraper(String url, String alias, int start) {
        try {
            return new WebScraperUtil(String.format(pageUrl, alias, start));
        } catch (IOException e) {
            logger.error("Issue with calling URL", e);
            throw new RuntimeException(e);
        }
    }

    private int parseReviewRating(String reviewRatingAlias) {
        return Integer.parseInt(reviewRatingAlias.split(" ")[0]);
    }

    private boolean toFetchMore(WebScraperUtil scraperUtil) {
        return (scraperUtil.getFirstElementBySelector(env.getProperty("yelp.restaurant.review-list.next.selector")) != null);
    }

    private List<RestaurantReviewItem> getReviews(WebScraperUtil scraperUtil) {
        Elements reviewList = scraperUtil.getElementsBySelector(env.getProperty("yelp.restaurant.review-list.selector"));
        return reviewList.stream()
                    .map((review) -> {
                        User user = new User(
                                review.selectFirst(env.getProperty("yelp.restaurant.review-list.review.reviewer-name.selector")).text(),
                                review.selectFirst(env.getProperty("yelp.restaurant.review-list.review.reviewer-img.selector")).attr("src")
                                    .replace("60s.jpg", "300s.jpg") // Retrieve bigger image

                        );
                        googleVisionService.getUserImgAnnotation(user);

                        Element commentElem = review.selectFirst(env.getProperty("yelp.restaurant.review-list.review.comment.selector"));
                        return new RestaurantReviewItem(
                                user,
                                parseReviewRating(review.selectFirst(env.getProperty("yelp.restaurant.review-list.review.rating.selector")).attr("aria-label")),
                                (commentElem != null) ? commentElem.text() : null
                        );
                    })
                    .collect(Collectors.toList());
    }

}
