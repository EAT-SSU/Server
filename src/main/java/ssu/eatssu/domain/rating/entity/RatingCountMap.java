package ssu.eatssu.domain.rating.entity;

import java.util.HashMap;
import java.util.Map;

public class RatingCountMap {

    private final Map<Integer, Integer> ratingCountMap;

    public RatingCountMap() {
        this.ratingCountMap = new HashMap<>();
        this.init();

    }

    private void init() {
        this.ratingCountMap.put(1, 0);
        this.ratingCountMap.put(2, 0);
        this.ratingCountMap.put(3, 0);
        this.ratingCountMap.put(4, 0);
        this.ratingCountMap.put(5, 0);
    }

    public void setRatingCount(Integer rating, Integer count) {
        this.ratingCountMap.put(rating, count);
    }

    public void merge(RatingCountMap map) {
        for (Map.Entry<Integer, Integer> entry : map.ratingCountMap.entrySet()) {
            Integer currentCount = this.ratingCountMap.getOrDefault(entry.getKey(), 0);
            Integer newCount = entry.getValue();
            this.ratingCountMap.put(entry.getKey(), currentCount + newCount);
        }
    }
}
