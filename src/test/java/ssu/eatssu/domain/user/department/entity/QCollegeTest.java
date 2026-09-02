package ssu.eatssu.domain.user.department.entity;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QCollegeTest {

    @Test
    void exposesQuerydslPathsForCollegeFields() {
        QCollege query = QCollege.college;

        assertThat(query.getType()).isEqualTo(College.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.nameKo.getMetadata().getName()).isEqualTo("nameKo");
        assertThat(query.nameEn.getMetadata().getName()).isEqualTo("nameEn");
        assertThat(query.nameJa.getMetadata().getName()).isEqualTo("nameJa");
        assertThat(query.nameVi.getMetadata().getName()).isEqualTo("nameVi");
        assertThat(query.departments.getMetadata().getName()).isEqualTo("departments");
        assertThat(query.partnerships.getMetadata().getName()).isEqualTo("partnerships");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QCollege.college.getMetadata();

        assertThat(new QCollege("college").getType()).isEqualTo(College.class);
        assertThat(new QCollege(QCollege.college).getType()).isEqualTo(College.class);
        assertThat(new QCollege(metadata).getType()).isEqualTo(College.class);
    }
}
