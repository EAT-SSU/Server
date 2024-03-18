package ssu.eatssu.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;

import java.util.ArrayList;
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

    @Embedded
    private Ratings ratings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    public void update(String content, Integer mainRate, Integer amountRate, Integer tasteRate) {
        this.content = content;
        this.ratings = Ratings.of(mainRate, amountRate, tasteRate);
    }

    public boolean isNotWrittenBy(User user) {
        return !this.user.equals(user);
    }

    public void clearUser() {
        this.user = null;
    }

}
