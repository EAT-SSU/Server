package ssu.eatssu.domain.user.dto;

import lombok.Builder;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.entity.Language;

@Builder
public record DepartmentResponse(
        Long departmentId, String departmentName, Long collegeId, String collegeName
) {
    public static DepartmentResponse from(Department department) {
        return from(department, Language.KO);
    }

    public static DepartmentResponse from(Department department, Language language) {
        if (department == null) {
            return new DepartmentResponse(null, null, null, null);
        }
        final College college = department.getCollege();
        return DepartmentResponse.builder()
                                 .departmentId(department.getId())
                                 .departmentName(department.getNameByLanguage(language))
                                 .collegeId(college != null ? college.getId() : null)
                                 .collegeName(college != null ? college.getNameByLanguage(language) : null)
                                 .build();
    }
}
