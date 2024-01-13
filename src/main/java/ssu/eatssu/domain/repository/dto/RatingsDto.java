package ssu.eatssu.domain.repository.dto;


import ssu.eatssu.domain.rating.Ratings;
public record RatingsDto (Ratings ratings){

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
