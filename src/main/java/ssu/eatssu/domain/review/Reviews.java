package ssu.eatssu.domain.review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.rate.ReviewRate;

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

    public void calculateReviewRates() {
        this.reviews.forEach(review -> {
            int rateValue = review.getRate().getMainRate();
            ReviewRate.fromValue(rateValue).incrementCount();
        });
    }

    public int getTotalMainRate() {
        return this.reviews.stream().mapToInt(review -> review.getRate().getMainRate()).sum();
    }

    public int getTotalAmountRate() {
        return this.reviews.stream().mapToInt(review -> review.getRate().getAmountRate()).sum();
    }

    public int getTotalTasteRate() {
        return this.reviews.stream().mapToInt(review -> review.getRate().getTasteRate()).sum();
    }
}

