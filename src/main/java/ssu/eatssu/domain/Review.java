package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Review extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private String content;

    private Integer mainGrade;

    private Integer amountGrade;

    private Integer tasteGrade;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImg> reviewImgs;

    public void update(String content, Integer mainGrade, Integer amountGrade, Integer tasteGrade){
        this.content = content;
        this.mainGrade = mainGrade;
        this.amountGrade = amountGrade;
        this.tasteGrade = tasteGrade;
    }

}
