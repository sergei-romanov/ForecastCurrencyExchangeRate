package algorithms;

import model.Csv;
import model.Currency;
import model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class MysticalTest {
    Algorithm algorithm;
    Storage storage;
    LocalDate date = LocalDate.now();
    Currency currency = Currency.TRY;

    @BeforeEach
    void setup() {
        Rate rate1 = Mockito.mock(Rate.class);
        Rate rate2 = Mockito.mock(Rate.class);
        Rate rate3 = Mockito.mock(Rate.class);
        Mockito.when(rate1.getCourse()).thenReturn(BigDecimal.valueOf(70));
        Mockito.when(rate2.getCourse()).thenReturn(BigDecimal.valueOf(30));
        Mockito.when(rate3.getCourse()).thenReturn(BigDecimal.valueOf(20));
        Map<LocalDate, Rate> rateMap = new HashMap<>();
        // last three full moon [2021-12-19, 2022-01-18, 2022-02-16]
        rateMap.put(LocalDate.of(2021, 12, 19), rate1);
        rateMap.put(LocalDate.of(2022, 1, 18), rate2);
        rateMap.put(LocalDate.of(2022, 2, 16), rate3);
        Csv csv = new Csv(rateMap);
        Map<Currency, Csv> tableStorage = new HashMap<>();
        tableStorage.put(currency, csv);
        storage = new Storage(tableStorage);
    }

    @Test
    void someDayRate() {
        algorithm = new Mystical(storage);
        var actual = algorithm.someDayRate(date, currency).getCourse();
        assertThat(actual).isEqualTo(BigDecimal.valueOf(40));
    }

    @Test
    void weekRate() {
        algorithm = new Mystical(storage);
        algorithm.weekRate(date, currency).stream()
                .map(e -> e.getCourse().doubleValue())
                .forEach(e -> assertThat(e).isEqualTo(40.0, withPrecision(8.0)));
    }

    @Test
    void monthRate() {
        algorithm = new Mystical(storage);
        algorithm.monthRate(date, LocalDate.of(2022, 4, 1), currency).stream()
                .map(e -> e.getCourse().doubleValue())
                .forEach(e -> assertThat(e).isEqualTo(40.0, withPrecision(8.0)));
    }
}