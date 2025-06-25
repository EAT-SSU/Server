package ssu.eatssu.domain.review.dto;

import ssu.eatssu.domain.rating.entity.Ratings;

public record RatingsDto(Ratings ratings) {

    public Integer getMainRating() {
        return ratings.getMainRating();
    }

    public Integer getTasteRating() {
        return ratings.getTasteRating();
    }

    public Integer getAmountRating() {
        return ratings.getAmountRating();
    }

}
