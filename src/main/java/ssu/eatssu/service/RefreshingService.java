package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.repository.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshingService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;

    public void refreshAllReviews(){
        List<Menu> menuList = menuRepository.findAll();
        for(Menu menu : menuList){
            menu.refreshReview();
        }
        List<Meal> mealList = mealRepository.findAll();
        for(Meal meal : mealList){
            meal.caculateGrade();
        }
    }
}
