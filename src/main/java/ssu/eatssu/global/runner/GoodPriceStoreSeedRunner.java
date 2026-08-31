package ssu.eatssu.global.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;
import ssu.eatssu.domain.goodpricestore.persistence.GoodPriceStoreRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoodPriceStoreSeedRunner implements ApplicationRunner {

    private static final String SEED_CSV_PATH = "seed/good_price_store_seed.csv";

    private final GoodPriceStoreRepository goodPriceStoreRepository;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        if (!args.containsOption("seed-good-price-store")) {
            return;
        }
        if (goodPriceStoreRepository.count() > 0) {
            log.info("GoodPriceStore가 이미 존재해 시딩을 건너뜁니다.");
            return;
        }

        List<GoodPriceStore> stores = readSeedCsv();
        goodPriceStoreRepository.saveAll(stores);
        log.info("GoodPriceStore {}건 시딩 완료", stores.size());
    }

    private List<GoodPriceStore> readSeedCsv() throws IOException {
        ClassPathResource resource = new ClassPathResource(SEED_CSV_PATH);

        List<GoodPriceStore> stores = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                                                  .setHeader()
                                                  .setSkipHeaderRecord(true)
                                                  .build()
                                                  .parse(reader)) {
            for (CSVRecord record : parser) {
                stores.add(toEntity(record));
            }
        }
        return stores;
    }

    private GoodPriceStore toEntity(CSVRecord record) {
        return GoodPriceStore.builder()
                             .sourceId(Integer.parseInt(record.get("source_id")))
                             .category(CategoryType.valueOf(record.get("category")))
                             .storeName(record.get("store_name"))
                             .mainMenu(blankToNull(record.get("main_menu")))
                             .price(parseNullableInt(record.get("price")))
                             .roadAddress(record.get("road_address"))
                             .district(record.get("district"))
                             .latitude(Double.parseDouble(record.get("latitude")))
                             .longitude(Double.parseDouble(record.get("longitude")))
                             .imageUrl1(blankToNull(record.get("image_url_1")))
                             .imageUrl2(blankToNull(record.get("image_url_2")))
                             .imageUrl3(blankToNull(record.get("image_url_3")))
                             .naverMapUrl(blankToNull(record.get("naver_map_url")))
                             .kakaoMapUrl(blankToNull(record.get("kakao_map_url")))
                             .build();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return (int) Double.parseDouble(value);
    }
}
