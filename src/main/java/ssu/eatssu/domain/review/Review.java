package ssu.eatssu.domain.review;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.BaseTimeEntity;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.ReviewImg;
import ssu.eatssu.domain.User;

import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Embedded
    private Rates rates;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImg> reviewImgs;

    public void update(String content, Integer mainRate, Integer amountRate, Integer tasteRate) {
        this.content = content;
        this.rates = new Rates(mainRate, amountRate, tasteRate);
    }

    public void signoutUser() {
        this.user = null;
    }

}
