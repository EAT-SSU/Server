package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.admin.dto.RegisterCategoryRequest;
import ssu.eatssu.domain.admin.persistence.LoadCategoryRepository;
import ssu.eatssu.domain.admin.persistence.ManageCategoryRepository;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Transactional
@Service
@RequiredArgsConstructor
public class ManageCategoryService {

    private final LoadCategoryRepository loadCategoryRepository;
    private final ManageCategoryRepository manageCategoryRepository;

    public void register(Restaurant restaurant, RegisterCategoryRequest request) {
        // 해당 식당에 이미 존재하는 카테고리인지 확인
        if (loadCategoryRepository.existsCategory(restaurant, request.name())) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }

        MenuCategory category = MenuCategory.builder()
                                            .restaurant(restaurant)
                                            .name(request.name())
                                            .build();

        manageCategoryRepository.save(category);
    }
}
