package ssu.eatssu.vo;

import java.util.HashMap;
import java.util.Map;

public class RateCountMap {
    private Map<Integer, Integer> rateCountMap = new HashMap<>();

    public RateCountMap(Map<Integer, Integer> rateCountMap) {
        // TODO: key 값이 1~5가 아닌 경우 예외처리
        this.rateCountMap = rateCountMap;
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