package ssu.eatssu.domain.goodpricestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreDetailResponse;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreResponse;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;
import ssu.eatssu.domain.goodpricestore.persistence.GoodPriceStoreRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import java.util.stream.Collectors;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_GOOD_PRICE_STORE;

@Service
@RequiredArgsConstructor
public class GoodPriceStoreService {
    private final GoodPriceStoreRepository goodPriceStoreRepository;

    public List<GoodPriceStoreResponse> getStores(CategoryType category) {
        List<GoodPriceStore> stores = category == null
                ? goodPriceStoreRepository.findAll()
                : goodPriceStoreRepository.findAllByCategory(category);

        return stores.stream()
                     .map(GoodPriceStoreResponse::fromEntity)
                     .collect(Collectors.toList());
    }

    public GoodPriceStoreDetailResponse getStoreDetail(Long id) {
        GoodPriceStore store = goodPriceStoreRepository.findById(id)
                                                        .orElseThrow(() -> new BaseException(NOT_FOUND_GOOD_PRICE_STORE));
        return GoodPriceStoreDetailResponse.fromEntity(store);
    }
}
