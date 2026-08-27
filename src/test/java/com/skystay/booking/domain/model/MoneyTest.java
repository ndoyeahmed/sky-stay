package com.skystay.booking.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void computesPercentage() {
        Money price = new Money(BigDecimal.valueOf(100_000), "XOF");
        Money penalty = price.percentage(30);
        assertThat(penalty.amount()).isEqualByComparingTo("30000.00");
    }
}
