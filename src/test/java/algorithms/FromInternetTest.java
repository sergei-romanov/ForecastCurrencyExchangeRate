package algorithms;

import model.Csv;
import model.Currency;
import model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class FromInternetTest {
    Storage storage;
    Algorithm algorithm;
    Currency currency = Currency.TRY;
    LocalDate date = LocalDate.of(2022, 3, 6);

    @BeforeEach
    void setup() {
        Map<LocalDate, Rate> rates = new HashMap<>();
        List<BigDecimal> courses = Stream.of(7.45, 7.90, 7.36, 6.61, 6.77, 5.94, 6.15, 5.84, 5.63, 5.56,
                        5.56, 5.55, 5.60, 5.65, 5.53, 5.51, 5.50, 5.51, 5.58, 5.60,
                        5.65, 5.69, 5.76, 5.81, 5.71, 5.80, 5.85, 5.81, 5.78, 5.71)
                .map(BigDecimal::valueOf)
                .toList();
        IntStream.range(1, 31)
                .mapToObj(i -> new Rate.Builder()
                        .withNominal(1)
                        .withCurrency(currency)
                        .withDate(date.minusDays(i))
                        .withCourse(courses.get(i - 1))
                        .build())
                .forEachOrdered(e -> rates.put(e.getDate(), e));
        Csv csv = new Csv(rates);
        Map<Currency, Csv> table = new HashMap<>();
        table.put(currency, csv);
        storage = new Storage(table);
    }

    @Test
    void someDayRate() {
        algorithm = new FromInternet(storage);
        var actual = algorithm.someDayRate(date, currency)
                .getCourse()
                .doubleValue();
        System.out.println(actual);
        assertThat(actual).isEqualTo(6.7, withPrecision(1.2));
    }

    @Test
    void weekRate() {
        algorithm = new FromInternet(storage);
        var actual = algorithm.weekRate(date, currency).stream()
                .map(e -> e.getCourse().doubleValue())
                .toList();
        actual.forEach(e -> assertThat(e).isEqualTo(6.7, withPrecision(1.2)));

    }

    @Test
    void monthRate() {
        algorithm = new FromInternet(storage);
        var actual = algorithm.monthRate(date, LocalDate.of(2022, 4, 1), currency).stream()
                .map(e -> e.getCourse().doubleValue())
                .toList();
        actual.forEach(e -> assertThat(e).isEqualTo(6.7, withPrecision(1.2)));
    }
}