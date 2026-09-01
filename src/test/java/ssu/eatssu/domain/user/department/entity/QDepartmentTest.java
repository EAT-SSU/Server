package ssu.eatssu.domain.user.department.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QDepartmentTest {

    @Test
    void exposesQuerydslPathsForDepartmentFields() {
        QDepartment query = QDepartment.department;

        assertThat(query.getType()).isEqualTo(Department.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.nameKo.getMetadata().getName()).isEqualTo("nameKo");
        assertThat(query.nameEn.getMetadata().getName()).isEqualTo("nameEn");
        assertThat(query.nameJa.getMetadata().getName()).isEqualTo("nameJa");
        assertThat(query.nameVi.getMetadata().getName()).isEqualTo("nameVi");
        assertThat(query.partnerships.getMetadata().getName()).isEqualTo("partnerships");
        assertThat(query.college).isNotNull();
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QDepartment.department.getMetadata();

        assertThat(new QDepartment("department").getType()).isEqualTo(Department.class);
        assertThat(new QDepartment(QDepartment.department).getType()).isEqualTo(Department.class);
        assertThat(new QDepartment(metadata).getType()).isEqualTo(Department.class);
        assertThat(new QDepartment(metadata, PathInits.DIRECT2).getType()).isEqualTo(Department.class);
        assertThat(new QDepartment(Department.class, metadata, PathInits.DIRECT2).getType()).isEqualTo(Department.class);
    }
}
