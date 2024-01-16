package ssu.eatssu.domain.report.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import ssu.eatssu.domain.report.entity.ReportType;

@Getter
public class ReportTypeResponse {

    private final List<Information> response = new ArrayList<>();

    public static ReportTypeResponse get() {
        Arrays.stream(ReportType.values())
            .forEach(reportType -> get().response.add(
	new Information(reportType.name(), reportType.getDescription())));
        return new ReportTypeResponse();
    }

    static class Information {

        private String type;
        private String description;

        public Information(String type, String description) {
            this.type = type;
            this.description = description;
        }
    }
}
