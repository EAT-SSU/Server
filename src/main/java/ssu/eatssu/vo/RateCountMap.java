package ssu.eatssu.vo;

import java.util.HashMap;
import java.util.Map;

public class RateCountMap {
    private Map<Integer, Integer> rateCountMap;

    public RateCountMap(Map<Integer, Integer> map) {
        this();
        map.forEach((key, value) -> rateCountMap.merge(key, value, Integer::sum));
    }

    public RateCountMap() {
        this.rateCountMap = new HashMap<>(Map.of(1, 0, 2, 0, 3, 0, 4, 0, 5, 0));
    }

    private Map<Integer, Integer> getMap() {
        return this.rateCountMap;
    }

    public void append(RateCountMap map) {
        map.getMap().forEach((key, value) -> rateCountMap.merge(key, value, Integer::sum));
    }

    public int get(int rate) {
        return rateCountMap.get(rate);
    }

}