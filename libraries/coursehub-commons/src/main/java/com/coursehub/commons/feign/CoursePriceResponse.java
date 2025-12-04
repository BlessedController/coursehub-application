package com.coursehub.commons.feign;

import com.coursehub.commons.feign.enums.Currency;

import java.math.BigDecimal;

public record CoursePriceResponse(
        BigDecimal amount,
        Currency currency
) {

    public static class Builder {
        private BigDecimal amount;
        private Currency currency;

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public CoursePriceResponse build() {
            return new CoursePriceResponse(amount, currency);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
