package ssu.eatssu.web.review.dto;

//COMMENT: 더 좋은 매개변수 명이 있을 것 같은데 제 머리로는 이게 한계입니다... 멋진 변수명으로 바꿔주세요
public record ReviewRatingCount(int oneStarCount, int twoStarCount, int threeStarCount, int fourStarCount, int fiveStarCount) {

    public ReviewRatingCount {
    }
}
