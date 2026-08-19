package ssu.eatssu.domain.goodpricestore.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreDetailResponse;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreResponse;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.presentation.docs.GoodPriceStoreControllerDocs;
import ssu.eatssu.domain.goodpricestore.service.GoodPriceStoreService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/good-price-stores")
@RequiredArgsConstructor
public class GoodPriceStoreController implements GoodPriceStoreControllerDocs {
    private final GoodPriceStoreService goodPriceStoreService;

    @Override
    @GetMapping
    public BaseResponse<List<GoodPriceStoreResponse>> getStores(
            @RequestParam(required = false) CategoryType category) {
        return BaseResponse.success(goodPriceStoreService.getStores(category));
    }

    @Override
    @GetMapping("/{id}")
    public BaseResponse<GoodPriceStoreDetailResponse> getStoreDetail(@PathVariable Long id) {
        return BaseResponse.success(goodPriceStoreService.getStoreDetail(id));
    }
}
