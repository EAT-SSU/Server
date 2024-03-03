package ssu.eatssu.domain.menu.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuRatingService {
    private final QuerydslMenuRatingCalculator menuRatingCalculator;


    public Double getMainRatingAverage(Long menuId) {
        return menuRatingCalculator.getMainRatingAverage(menuId);
    }
}
