package ssu.eatssu.domain.user.department.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentEntityTest {

    @Test
    void collegeExposesKoreanNameAndCollections() {
        College college = new College("IT대학");
        ReflectionTestUtils.setField(college, "nameJa", "IT大学");
        ReflectionTestUtils.setField(college, "nameVi", "Đại học CNTT");

        assertThat(college.getName()).isEqualTo("IT대학");
        assertThat(college.getNameKo()).isEqualTo("IT대학");
        assertThat(college.getNameJa()).isEqualTo("IT大学");
        assertThat(college.getNameVi()).isEqualTo("Đại học CNTT");
        assertThat(college.getDepartments()).isEmpty();
        assertThat(college.getPartnerships()).isEmpty();
        assertThat(college.getId()).isNull();
    }

    @Test
    void departmentExposesKoreanNameAndCollections() {
        Department department = new Department("컴퓨터학부");
        ReflectionTestUtils.setField(department, "nameJa", "コンピュータ学部");
        ReflectionTestUtils.setField(department, "nameVi", "Khoa Khoa học Máy tính");

        assertThat(department.getName()).isEqualTo("컴퓨터학부");
        assertThat(department.getNameKo()).isEqualTo("컴퓨터학부");
        assertThat(department.getNameJa()).isEqualTo("コンピュータ学部");
        assertThat(department.getNameVi()).isEqualTo("Khoa Khoa học Máy tính");
        assertThat(department.getPartnerships()).isEmpty();
        assertThat(department.getId()).isNull();
        assertThat(department.getCollege()).isNull();
    }
}
