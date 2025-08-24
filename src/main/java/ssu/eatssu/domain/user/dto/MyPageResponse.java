package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.entity.User;

@AllArgsConstructor
@Builder
@Schema(title = "마이페이지 정보")
@Getter
public class MyPageResponse {

    @Schema(description = "닉네임", example = "피치푸치")
    private String nickname;

    @Schema(description = "연결 계정 정보", example = "GOOGLE")
    private OAuthProvider provider;

    @Schema(description = "학과 id", example = "1")
    private Long departmentId;

    @Schema(description = "학과 이름", example = "컴퓨터학부")
    private String departmentName;

    @Schema(description = "단과대 id", example = "1")
    private Long collegeId;

    @Schema(description = "단과대 이름", example = "IT 대학")
    private String collegeName;

    public static MyPageResponse from(User user) {
        if (user == null) {
            return MyPageResponse.builder().build();
        }

        Department department = user.getDepartment();
        College college = department != null ? department.getCollege() : null;

        return MyPageResponse.builder()
                             .nickname(user.getNickname())
                             .provider(user.getProvider())
                             .departmentId(department != null ? department.getId() : null)
                             .departmentName(department != null ? department.getName() : null)
                             .collegeId(college != null ? college.getId() : null)
                             .collegeName(college != null ? college.getName() : null)
                             .build();
    }
}
