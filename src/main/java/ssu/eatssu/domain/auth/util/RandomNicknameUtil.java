package ssu.eatssu.domain.auth.util;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomNicknameUtil {

    private static final String[] PREFIX = {
            "덜익은","잘익은","타버린","밍밍한","짭짤한","달달한","씁쓸한","매콤한","얼큰한","구수한",
            "새콤한","상큼한","고소한","담백한","느끼한","촉촉한","바삭한","쫄깃한","꾸덕한","진한",
            "깔끔한","시원한","은은한","중독적인","화끈한","감칠한","개운한","살짝탄","부드러운","향긋한",
            "자극적인","산뜻한","짙은","진득한","기름진","촉촉한","살짝매운","은근한","쌉쌀한","따뜻한",
            "따끈한","시큼한","고급진","정겨운","친숙한","투박한","소박한","짠내나는","심심한","묵직한"
    };

    private static final String[] MENU_NAME = {
            "등심돈가스","콥샐러드","팟타이","김치찌개","단호박죽","김치볶음밥","미소장국","총각김치","제육덮밥","단무지",
            "볶음김치","야채피클","나주곰탕","갈비탕","동태전","떡갈비조림","칠리탕수육","소고기국밥","콩나물밥","고구마맛탕",
            "계란찜","한식잡채","치킨너겟","타코야끼","비엔나볶음","떡볶이","두부장국","동지죽","감귤","야채튀김",
            "미니우동","챠슈덮밥","가츠동","새우튀김","탕수육강정","두부조림","부추전","검정콩밥","망고샐러드","비빔만두",
            "비빔밥","닭볶음탕","수수밥","쟁반짜장","깍두기","볶음김치","연근조림","멸치볶음","오이생채","감자조림"
    };

    public String generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String prefix = PREFIX[random.nextInt(PREFIX.length)];
        String category = MENU_NAME[random.nextInt(MENU_NAME.length)];

        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 4);

        return prefix + "-" + category + "-" + uuidPart;
    }
}
