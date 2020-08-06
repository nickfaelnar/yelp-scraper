package com.crescendocollective.yelpscraper.dto;

import com.crescendocollective.yelpscraper.base.BaseReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter @AllArgsConstructor
public class YelpRequestV2 extends BaseReviewRequest {

    private @NonNull String restaurantId;

}
