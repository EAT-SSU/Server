package ssu.eatssu.domain.admin.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BriefMenuTest {

    @Test
    void storesMenuSummaryFields() {
        BriefMenu menu = new BriefMenu(1L, "돈가스", 6000);

        assertThat(menu.id()).isEqualTo(1L);
        assertThat(menu.name()).isEqualTo("돈가스");
        assertThat(menu.price()).isEqualTo(6000);
    }
}
