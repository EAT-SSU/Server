package ssu.eatssu.domain.review;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.review.Review;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String imageUrl;
}
