package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "유저 학과 등록")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDepartmentRequest {
	@Schema(description = "학과 이름", example = "소프트")
	private String departmentName;
}
