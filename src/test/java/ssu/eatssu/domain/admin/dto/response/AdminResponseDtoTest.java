package ssu.eatssu.domain.admin.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AdminResponseDtoTest {

    @Test
    void menuBoardsAndSectionsCollectMenuLines() {
        MenuCategory category = mock(MenuCategory.class);
        given(category.getId()).willReturn(1L);
        given(category.getName()).willReturn("한식");
        MenuLine line = new MenuLine(1L, "돈가스", 6000, 4.5);
        MenuSection menuSection = new MenuSection(category);
        MealSection mealSection = new MealSection(2L, "중식");
        menuSection.addMenuLine(line);
        mealSection.addMenuLine(line);
        MenuBoard board = new MenuBoard("학식");
        board.addSection(menuSection);
        board.addSection(mealSection);
        MenuBoards boards = new MenuBoards();
        boards.add(board);

        assertThat(boards.menuBoards()).containsExactly(board);
        assertThat(menuSection.menuLines()).containsExactly(line);
        assertThat(mealSection.menuLines()).containsExactly(line);
    }

    @Test
    void pageWrapperCopiesPageMetadata() {
        PageWrapper<String> wrapper = new PageWrapper<>(new PageImpl<>(List.of("one"), PageRequest.of(1, 2), 5));

        assertThat(wrapper.content()).containsExactly("one");
        assertThat(wrapper.totalPages()).isEqualTo(3);
        assertThat(wrapper.number()).isEqualTo(1);
        assertThat(wrapper.isFirst()).isFalse();
    }

    @Test
    void inquiryAndReportLinesMapSourceEntities() {
        LocalDateTime now = LocalDateTime.now();
        Inquiry inquiry = mock(Inquiry.class);
        given(inquiry.getId()).willReturn(1L);
        given(inquiry.getCreatedDate()).willReturn(now);
        given(inquiry.getEmail()).willReturn("user@eatssu.com");
        given(inquiry.getContent()).willReturn("문의");
        given(inquiry.getStatus()).willReturn(InquiryStatus.WAITING);
        Report report = mock(Report.class);
        Review review = mock(Review.class);
        given(report.getId()).willReturn(2L);
        given(report.getCreatedDate()).willReturn(now);
        given(report.getReportType()).willReturn(ReportType.IMPROPER_CONTENT);
        given(review.getId()).willReturn(3L);
        given(review.getContent()).willReturn("리뷰");

        assertThat(new InquiryLine(inquiry).email()).isEqualTo("user@eatssu.com");
        assertThat(new ReportLine(report, review).type()).isEqualTo("음란성, 욕설 등 부적절한 내용");
    }
}
