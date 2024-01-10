package ssu.eatssu.domain.review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.rating.ReviewRating;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class Reviews {

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    public void add(Review review) {
        reviews.add(review);
    }

    public int size() {
        return reviews.size();
    }

    public void calculateReviewRatings() {
        this.reviews.forEach(review -> {
            int rateValue = review.getRatings().getMainRating();
            ReviewRating.fromValue(rateValue).incrementCount();
        });
    }


    public int getTotalMainRating() {
        return this.reviews.stream().mapToInt(review -> review.getRatings().getMainRating()).sum();
    }

    public int getTotalAmountRating() {
        return this.reviews.stream().mapToInt(review -> review.getRatings().getAmountRating()).sum();
    }

    public int getTotalTasteRating() {
        return this.reviews.stream().mapToInt(review -> review.getRatings().getTasteRating()).sum();
    }
}

