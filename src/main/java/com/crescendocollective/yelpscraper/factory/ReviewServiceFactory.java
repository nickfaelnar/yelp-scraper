package com.crescendocollective.yelpscraper.factory;

import com.crescendocollective.yelpscraper.constants.ReviewSite;
import com.crescendocollective.yelpscraper.service.ReviewService;
import com.crescendocollective.yelpscraper.service.YelpReviewV1Service;
import com.crescendocollective.yelpscraper.service.YelpReviewV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Review Service Factory.
 * @author nfaelnar
 */
@Component
public class ReviewServiceFactory {

    @Autowired
    private YelpReviewV1Service yelpReviewV1Service;

    @Autowired
    private YelpReviewV2Service yelpReviewV2Service;

    public ReviewService create(ReviewSite site) {
        switch (site) {
            case YELPV1:
                return yelpReviewV1Service;
            case YELPV2:
                return yelpReviewV2Service;
            default:
                // TODO: Can implement Builder Pattern here
                throw new UnsupportedOperationException();
        }
    }
}
