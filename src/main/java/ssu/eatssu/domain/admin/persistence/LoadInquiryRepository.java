package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.domain.inquiry.entity.QInquiry;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class LoadInquiryRepository {
    private final JPAQueryFactory queryFactory;

    private final QInquiry inquiry = QInquiry.inquiry;

    public Page<Inquiry> findAllInquiries(Pageable pageable) {
        return PageableExecutionUtils.getPage(fetchInquiries(pageable), pageable, () -> countInquiries().fetchOne());
    }

    private JPAQuery<Long> countInquiries() {
        return queryFactory
                .select(inquiry.count())
                .from(inquiry);
    }
    private List<Inquiry> fetchInquiries(Pageable pageable) {
        return queryFactory
                .selectFrom(inquiry)
                .orderBy(statusPath().asc(), inquiry.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private NumberExpression<Integer> statusPath() {

        /**
         * Inquiry Status 에 따라 정렬
         * WAITING -> HOLD -> ANSWERED
         */
        return new CaseBuilder()
                .when(inquiry.status.eq(InquiryStatus.WAITING)).then(1)
                .when(inquiry.status.eq(InquiryStatus.HOLD)).then(2)
                .when(inquiry.status.eq(InquiryStatus.ANSWERED)).then(3)
                .otherwise(4);
    }
}
