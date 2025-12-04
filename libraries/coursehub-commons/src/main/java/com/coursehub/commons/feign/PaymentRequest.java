package com.coursehub.commons.feign;

import com.coursehub.commons.feign.enums.Currency;
import com.coursehub.commons.feign.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        String paymentId,
        String userId,
        String courseId,
        BigDecimal amount,
        Currency currency,
        PaymentMethod paymentMethod
) {

    public static class Builder {

        private String paymentId;
        private String userId;
        private String courseId;
        private BigDecimal amount;
        private Currency currency;
        private PaymentMethod paymentMethod;

        public Builder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(
                    paymentId,
                    userId,
                    courseId,
                    amount,
                    currency,
                    paymentMethod
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
