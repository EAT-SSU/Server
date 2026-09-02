package ssu.eatssu.domain.goodpricestore.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreDetailResponse;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreResponse;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.service.GoodPriceStoreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GoodPriceStoreControllerTest {

    @Mock
    private GoodPriceStoreService goodPriceStoreService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new GoodPriceStoreController(goodPriceStoreService)).build();
    }

    @Test
    void getStoresReturnsStoresFilteredByCategory() throws Exception {
        when(goodPriceStoreService.getStores(CategoryType.KOREAN))
                .thenReturn(List.of(GoodPriceStoreResponse.builder().id(1L).storeName("식당").build()));

        mockMvc.perform(get("/good-price-stores").param("category", CategoryType.KOREAN.name()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result[0].storeName").value("식당"));

        verify(goodPriceStoreService).getStores(eq(CategoryType.KOREAN));
    }

    @Test
    void getStoresReturnsAllStoresWhenCategoryIsOmitted() throws Exception {
        when(goodPriceStoreService.getStores(isNull())).thenReturn(List.of());

        mockMvc.perform(get("/good-price-stores"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void getStoreDetailReturnsStoreById() throws Exception {
        when(goodPriceStoreService.getStoreDetail(1L))
                .thenReturn(GoodPriceStoreDetailResponse.builder().id(1L).storeName("식당").build());

        mockMvc.perform(get("/good-price-stores/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.storeName").value("식당"));
    }
}
