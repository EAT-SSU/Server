package ssu.eatssu.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;
import ssu.eatssu.domain.enums.ReviewTag;

import java.util.ArrayList;
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

    @Enumerated(EnumType.STRING)
    private ReviewTag tag1;

    @Enumerated(EnumType.STRING)
    private ReviewTag tag2;

    @Enumerated(EnumType.STRING)
    private ReviewTag tag3;

    private Integer grade;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImg> reviewImgs;

    public void setTags(List<ReviewTag> tags){
        int tagsSize = tags.size();
        if(tagsSize==1){
            this.tag1 = tags.get(0);
        }else if(tagsSize==2){
            this.tag1 = tags.get(0);
            this.tag2 = tags.get(1);
        }else if(tagsSize>2){
            this.tag1 = tags.get(0);
            this.tag2 = tags.get(1);
            this.tag3 = tags.get(2);
        }
    }

    private void clearTags(){
        tag1=null;
        tag2=null;
        tag3=null;
    }

    public List<ReviewTag> tagsToList(){
        List<ReviewTag> tagList = new ArrayList<>();
        if(tag1==null){
            return tagList;
        }else{
            tagList.add(tag1);
        }
        if(tag2==null){
            return tagList;
        }else{
            tagList.add(tag2);
        }
        if(tag3==null){
            return tagList;
        }else{
            tagList.add(tag3);
        }
        return tagList;
    }

    public void update(String content, Integer grade, List<ReviewTag> tags){
        this.content = content;
        this.grade = grade;
        clearTags();
        setTags(tags);
    }

}
