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

    private Integer mainRate;

    private Integer amountRate;

    private Integer tasteRate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImgs;

    public void update(String content, Integer mainRate, Integer amountRate, Integer tasteRate){
        this.content = content;
        this.mainRate = mainRate;
        this.amountRate = amountRate;
        this.tasteRate = tasteRate;
    }

    public void signoutUser(){
        this.user = null;
    }

}
