package ssu.eatssu.domain.inquiry.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QInquiryTest {

    @Test
    void exposesQuerydslPathsForInquiryFields() {
        QInquiry query = QInquiry.inquiry;

        assertThat(query.getType()).isEqualTo(Inquiry.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.content.getMetadata().getName()).isEqualTo("content");
        assertThat(query.email.getMetadata().getName()).isEqualTo("email");
        assertThat(query.status.getMetadata().getName()).isEqualTo("status");
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
        assertThat(query.user).isNotNull();
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QInquiry.inquiry.getMetadata();

        assertThat(new QInquiry("inquiry").getType()).isEqualTo(Inquiry.class);
        assertThat(new QInquiry(QInquiry.inquiry).getType()).isEqualTo(Inquiry.class);
        assertThat(new QInquiry(metadata).getType()).isEqualTo(Inquiry.class);
        assertThat(new QInquiry(metadata, PathInits.DIRECT2).getType()).isEqualTo(Inquiry.class);
        assertThat(new QInquiry(Inquiry.class, metadata, PathInits.DIRECT2).getType()).isEqualTo(Inquiry.class);
    }
}
