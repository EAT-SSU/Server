package ssu.eatssu.domain.user.dto;

import lombok.Builder;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;

@Builder
public record DepartmentResponse(
        Long departmentId, String departmentName, Long collegeId, String collegeName
) {
    public static DepartmentResponse from(Department department) {
        if (department == null) {
            return new DepartmentResponse(null, null, null, null);
        }
        final College college = department.getCollege();
        return DepartmentResponse.builder()
                                 .departmentId(department.getId())
                                 .departmentName(department.getName())
                                 .collegeId(college != null ? college.getId() : null)
                                 .collegeName(college != null ? college.getName() : null)
                                 .build();
    }
}

