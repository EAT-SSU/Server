package ssu.eatssu.domain.review.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.review.dto.response.MenuReviewResponse;
import ssu.eatssu.domain.review.service.ReviewService;
import ssu.eatssu.domain.slice.service.SliceService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private SliceService sliceService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService, sliceService)).build();
    }

    @Test
    void 메뉴_리뷰_통계를_조회한다() throws Exception {
        when(reviewService.findMenuReviews(1L)).thenReturn(MenuReviewResponse.builder().menuName("라면").build());

        mockMvc.perform(get("/reviews/menus/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.menuName").value("라면"));

        verify(reviewService).findMenuReviews(1L);
    }
}
