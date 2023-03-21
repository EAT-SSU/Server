package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_hour_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    private DayType dayType;

    @Enumerated(EnumType.STRING)
    private TimePart timePart;

    private String time;

}
