create table college
(
    college_id bigint auto_increment
        primary key,
    name       varchar(255) not null,
    constraint UK_nyc2rxbj71rdhcw055436agb5
        unique (name)
);

create table department
(
    department_id bigint auto_increment
        primary key,
    name          varchar(255) not null,
    college_id    bigint       null,
    constraint FK6p7527hkl1k5a92k34d8yye97
        foreign key (college_id) references college (college_id)
);

create table meal
(
    meal_id    bigint auto_increment
        primary key,
    date       date                                                                           null,
    price      int                                                                            null,
    restaurant enum ('DODAM', 'DORMITORY', 'FOOD_COURT', 'SNACK_CORNER', 'HAKSIK', 'FACULTY') null,
    time_part  enum ('MORNING', 'LUNCH', 'DINNER')                                            null
);

create table menu_category
(
    menu_category_id bigint auto_increment
        primary key,
    name             varchar(255)                                                                   null,
    restaurant       enum ('DODAM', 'DORMITORY', 'FOOD_COURT', 'SNACK_CORNER', 'HAKSIK', 'FACULTY') null
);

create table menu
(
    menu_id          bigint auto_increment
        primary key,
    is_discontinued  bit                                                                            null,
    like_count       int                                                                            null,
    name             varchar(255)                                                                   null,
    price            int                                                                            null,
    restaurant       enum ('DODAM', 'DORMITORY', 'FOOD_COURT', 'SNACK_CORNER', 'HAKSIK', 'FACULTY') null,
    unlike_count     int                                                                            null,
    menu_category_id bigint                                                                         null,
    constraint FKpyeafaiqn171tt9u4atxl9de7
        foreign key (menu_category_id) references menu_category (menu_category_id)
);

create table meal_menu
(
    meal_menu_id bigint auto_increment
        primary key,
    meal_id      bigint null,
    menu_id      bigint null,
    constraint FK4csi2rxcc80koaiybdjkpfntx
        foreign key (meal_id) references meal (meal_id),
    constraint FK5ywj42p0hun7ifyfd3p3pxxnt
        foreign key (menu_id) references menu (menu_id)
);

create table partnership_restaurant
(
    partnership_restaurant_id bigint auto_increment
        primary key,
    latitude                  double                             not null,
    longitude                 double                             not null,
    restaurant_type           enum ('RESTAURANT', 'CAFE', 'PUB') not null,
    store_name                varchar(255)                       not null
);

create table partnership
(
    partnership_id            bigint auto_increment
        primary key,
    description               varchar(255) not null,
    end_date                  date         not null,
    start_date                date         not null,
    partnership_restaurant_id bigint       null,
    partnership_college_id    bigint       null,
    partnership_department_id bigint       null,
    constraint FK3we90emq62kx2w4qvcje88soq
        foreign key (partnership_restaurant_id) references partnership_restaurant (partnership_restaurant_id),
    constraint FKf71ovavgpo8nn0eusnwfxi58b
        foreign key (partnership_department_id) references department (department_id),
    constraint FKp5qgfntyy03gl0n2t4t9bar7
        foreign key (partnership_college_id) references college (college_id)
);

create table user
(
    user_id       bigint auto_increment
        primary key,
    created_date  datetime(6)                       not null,
    modified_date datetime(6)                       not null,
    credentials   varchar(255)                      null,
    email         varchar(255)                      null,
    nickname      varchar(255)                      null,
    provider      enum ('EATSSU', 'KAKAO', 'APPLE') null,
    provider_id   varchar(255)                      null,
    role          enum ('USER', 'ADMIN')            null,
    status        enum ('ACTIVE', 'INACTIVE')       null,
    department_id bigint                            null,
    device_type   enum ('IOS', 'ANDROID')           null,
    constraint UK_ob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint FKgkh2fko1e4ydv1y6vtrwdc6my
        foreign key (department_id) references department (department_id)
);

create table inquiry
(
    user_inquiry_id bigint auto_increment
        primary key,
    created_date    datetime(6)                          not null,
    modified_date   datetime(6)                          not null,
    content         varchar(255)                         null,
    email           varchar(255)                         null,
    status          enum ('WAITING', 'ANSWERED', 'HOLD') null,
    user_id         bigint                               null,
    constraint FKff1ylwlwujmed7diqs8ykf6pv
        foreign key (user_id) references user (user_id)
);

create table partnership_like
(
    partnership_like_id       bigint auto_increment
        primary key,
    partnership_restaurant_id bigint null,
    user_id                   bigint null,
    constraint FKcwju0bkiwim9w4lc9v27pkdit
        foreign key (user_id) references user (user_id),
    constraint FKhb06n2mees4p3gxqqrj414q3u
        foreign key (partnership_restaurant_id) references partnership_restaurant (partnership_restaurant_id)
);

create table review
(
    review_id     bigint auto_increment
        primary key,
    created_date  datetime(6)  not null,
    modified_date datetime(6)  not null,
    content       varchar(300) null,
    rating        int          null,
    amount_rating int          null,
    main_rating   int          null,
    taste_rating  int          null,
    meal_id       bigint       null,
    menu_id       bigint       null,
    user_id       bigint       null,
    constraint FKbsl9qcmq2sb7otlbp6ibmmwer
        foreign key (meal_id) references meal (meal_id),
    constraint FKiyf57dy48lyiftdrf7y87rnxi
        foreign key (user_id) references user (user_id),
    constraint FKkythy7xd59wvq6hwhv23xh7gw
        foreign key (menu_id) references menu (menu_id)
);

create table report
(
    review_report_id bigint auto_increment
        primary key,
    created_date     datetime(6)                                                                                               not null,
    modified_date    datetime(6)                                                                                               not null,
    content          varchar(255)                                                                                              null,
    report_type      enum ('NO_ASSOCIATE_CONTENT', 'IMPROPER_CONTENT', 'IMPROPER_ADVERTISEMENT', 'COPY', 'COPYRIGHT', 'EXTRA') null,
    status           enum ('PENDING', 'IN_PROGRESS', 'RESOLVED', 'REJECTED', 'FALSE_REPORT')                                   null,
    review_id        bigint                                                                                                    null,
    reporter_id      bigint                                                                                                    null,
    constraint FKmcui10qh03nnf6h3glch6pvmy
        foreign key (review_id) references review (review_id),
    constraint FKndpjl61ubcm2tkf7ml1ynq13t
        foreign key (reporter_id) references user (user_id)
);

create table review_image
(
    review_image_id bigint auto_increment
        primary key,
    image_url       varchar(255) null,
    review_id       bigint       null,
    constraint FK16wp089tx9nm0obc217gvdd6l
        foreign key (review_id) references review (review_id)
);

create table review_like
(
    review_like_id bigint auto_increment
        primary key,
    review_id      bigint null,
    user_id        bigint null,
    constraint FK68am9vk1s1e8n1v873meqkk0k
        foreign key (review_id) references review (review_id),
    constraint FKq4l36vpqal6v4ehikh67g8e49
        foreign key (user_id) references user (user_id)
);

create table review_menu_like
(
    review_menu_like_id bigint auto_increment
        primary key,
    is_like             bit    not null,
    menu_id             bigint null,
    review_id           bigint null,
    constraint FK6ey698wnlhnv3xtn2r60kt3jw
        foreign key (review_id) references review (review_id),
    constraint FKo33fcdj516ypc6vjy5kamu2ck
        foreign key (menu_id) references menu (menu_id)
);