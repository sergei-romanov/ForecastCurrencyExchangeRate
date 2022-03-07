package model;

import utils.LocalDateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class Rate implements Comparable<Rate> {
    private int nominal;
    private LocalDate date;
    private BigDecimal course;
    private Currency currency;

    public Rate() {
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getCourse() {
        return course;
    }

    public void setCourse(BigDecimal course) {
        this.course = course;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Rate cloneRate() {
        return new Builder()
                .withNominal(this.getNominal())
                .withDate(this.getDate())
                .withCourse(this.getCourse())
                .withCurrency(this.currency)
                .build();
    }

    @Override
    public int compareTo(Rate o) {
        if (this.getDate().isBefore(o.getDate())) {
            return 1;
        }
        if (this.getDate().equals(o.getDate())) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "" + nominal + " "
                + date.format(LocalDateUtils.FORMATTER) + " "
                + course.setScale(2, RoundingMode.FLOOR) + " "
                + currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rate rate1 = (Rate) o;
        return nominal == rate1.nominal
                && date.equals(rate1.date)
                && course.equals(rate1.course)
                && currency.equals(rate1.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, course, currency);
    }

    public static final class Builder {
        private int nominal;
        private LocalDate date;
        private BigDecimal course;
        private Currency currency;

        public Builder() {
        }

        public Builder withNominal(int nominal) {
            this.nominal = nominal;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder withCourse(BigDecimal rate) {
            this.course = rate;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Rate build() {
            Rate result = new Rate();
            result.setNominal(nominal);
            result.setDate(date);
            result.setCourse(course);
            result.setCurrency(currency);
            return result;
        }
    }
}

