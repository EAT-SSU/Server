package ssu.eatssu.domain.rating.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RatingCountMapTest {

    @Test
    void initializesFiveRatingsAndMergesCounts() {
        RatingCountMap first = new RatingCountMap();
        RatingCountMap second = new RatingCountMap();
        first.setRatingCount(5, 2);
        second.setRatingCount(5, 3);
        second.setRatingCount(6, 1);

        first.merge(second);

        Map<Integer, Integer> counts = field(first);
        assertThat(counts).containsEntry(1, 0).containsEntry(5, 5).containsEntry(6, 1);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> field(RatingCountMap map) {
        return (Map<Integer, Integer>) ReflectionTestUtils.getField(map, "ratingCountMap");
    }
}
