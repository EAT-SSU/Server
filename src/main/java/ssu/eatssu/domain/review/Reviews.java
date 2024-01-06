package ssu.eatssu.domain.review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Map<Integer, Long> countMap() {
        return this.reviews.stream()
            .collect(Collectors.groupingBy(Review::getMainRate, Collectors.counting()));
    }
}

