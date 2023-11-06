package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ssu.eatssu.domain.enums.TimePart;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    //    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @DateTimeFormat(pattern = "yyyyMMdd")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private TimePart timePart;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Double mainGrade = 0.0;

    private Double amountGrade = 0.0;

    private Double tasteGrade = 0.0;


    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @Builder
    public Meal(Date date, TimePart timePart, Restaurant restaurant){
        this.date = date;
        this.timePart = timePart;
        this.restaurant = restaurant;
    }

    public void caculateGrade(){
        if(!mealMenus.isEmpty()){
            int totalReviewCnt = 0;
            Double mainGradeSum = 0.0;
            Double amountGradeSum = 0.0;
            Double tasteGradeSum = 0.0;
            for(MealMenu mealMenu : mealMenus){
                Menu menu = mealMenu.getMenu();
                totalReviewCnt += menu.getReviewCnt();
                mainGradeSum += menu.getTotalMainGrade();
                amountGradeSum += menu.getTotalAmountGrade();
                tasteGradeSum += menu.getTotalTasteGrade();
            }
            if(totalReviewCnt!=0){
                this.mainGrade = mainGradeSum/totalReviewCnt;
                this.amountGrade = amountGradeSum/totalReviewCnt;
                this.tasteGrade = tasteGradeSum/totalReviewCnt;
            }else{
                this.mainGrade = 0.0;
                this.amountGrade = 0.0;
                this.tasteGrade = 0.0;
            }

        }
    }

    public Map<String, Double> getGradeMap() {
        Map<String, Double> gradeMap = new HashMap<>();
        gradeMap.put("mainGrade", mainGrade);
        gradeMap.put("amountGrade", amountGrade);
        gradeMap.put("tasteGrade", tasteGrade);
        return gradeMap;
    }
}
