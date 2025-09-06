package ssu.eatssu.domain.review.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.rating.entity.ReviewRating;

import java.util.ArrayList;
import java.util.List;

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
            if (review.getRatings() != null) {
                int rateValue = review.getRatings().getMainRating();
                ReviewRating.fromValue(rateValue).incrementCount();
            }
        });
    }

    public int getTotalMainRating() {
        return this.reviews.stream()
                           .filter(review -> review.getRatings() != null)
                           .mapToInt(review -> review.getRatings().getMainRating())
                           .sum();
    }
}

