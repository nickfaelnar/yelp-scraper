package com.crescendocollective.yelpscraper.service;

import com.crescendocollective.yelpscraper.base.BaseReviewRequest;
import com.crescendocollective.yelpscraper.dto.RestaurantReview;
import com.crescendocollective.yelpscraper.dto.YelpRequestV1;

public interface ReviewService<T extends BaseReviewRequest> {

    RestaurantReview getReview(T requestV1);

}
