package ssu.eatssu.global.runner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.goodpricestore.persistence.GoodPriceStoreRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoodPriceStoreSeedRunnerTest {

    @Test
    void 시딩_옵션이_없으면_저장하지_않는다() throws Exception {
        GoodPriceStoreRepository repository = mock(GoodPriceStoreRepository.class);
        ApplicationArguments arguments = mock(ApplicationArguments.class);
        when(arguments.containsOption("seed-good-price-store")).thenReturn(false);

        new GoodPriceStoreSeedRunner(repository).run(arguments);

        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void 데이터가_이미_있으면_시딩하지_않는다() throws Exception {
        GoodPriceStoreRepository repository = mock(GoodPriceStoreRepository.class);
        ApplicationArguments arguments = mock(ApplicationArguments.class);
        when(arguments.containsOption("seed-good-price-store")).thenReturn(true);
        when(repository.count()).thenReturn(1L);

        new GoodPriceStoreSeedRunner(repository).run(arguments);

        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void 비어있는_저장소에는_CSV_데이터를_시딩한다() throws Exception {
        GoodPriceStoreRepository repository = mock(GoodPriceStoreRepository.class);
        ApplicationArguments arguments = mock(ApplicationArguments.class);
        when(arguments.containsOption("seed-good-price-store")).thenReturn(true);
        when(repository.count()).thenReturn(0L);

        new GoodPriceStoreSeedRunner(repository).run(arguments);

        verify(repository).saveAll(any());
    }

    @Test
    void 값이_널이면_빈값으로_취급한다() {
        GoodPriceStoreSeedRunner runner = new GoodPriceStoreSeedRunner(mock(GoodPriceStoreRepository.class));

        String blanked = ReflectionTestUtils.invokeMethod(runner, "blankToNull", (String) null);
        Integer parsed = ReflectionTestUtils.invokeMethod(runner, "parseNullableInt", (String) null);

        assertThat(blanked).isNull();
        assertThat(parsed).isNull();
    }
}
