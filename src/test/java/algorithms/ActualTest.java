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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ActualTest {
    Algorithm algorithm;
    LocalDate date = LocalDate.of(2022, 3, 24);
    Storage storage;
    Currency currency = Currency.TRY;

    @BeforeEach
    void setup() {
        Rate rate1 = Mockito.mock(Rate.class);
        Rate rate2 = Mockito.mock(Rate.class);
        Rate rate3 = Mockito.mock(Rate.class);
        Rate rate4 = Mockito.mock(Rate.class);
        Rate rate5 = Mockito.mock(Rate.class);
        Rate rate6 = Mockito.mock(Rate.class);
        Rate rate7 = Mockito.mock(Rate.class);
        Rate rate8 = Mockito.mock(Rate.class);

        Mockito.when(rate1.getCourse()).thenReturn(BigDecimal.valueOf(70));
        Mockito.when(rate2.getCourse()).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(rate3.getCourse()).thenReturn(BigDecimal.valueOf(20));
        Mockito.when(rate4.getCourse()).thenReturn(BigDecimal.valueOf(30));
        Mockito.when(rate5.getCourse()).thenReturn(BigDecimal.valueOf(40));
        Mockito.when(rate6.getCourse()).thenReturn(BigDecimal.valueOf(50));
        Mockito.when(rate7.getCourse()).thenReturn(BigDecimal.valueOf(60));
        Mockito.when(rate8.getCourse()).thenReturn(BigDecimal.valueOf(160));

        Map<LocalDate, Rate> rateMap = new HashMap<>();
        rateMap.put(date.minusYears(2).plusDays(1), rate1);
        rateMap.put(date.minusYears(2).plusDays(2), rate2);
        rateMap.put(date.minusYears(2).plusDays(3), rate3);
        rateMap.put(date.minusYears(2).plusDays(4), rate4);
        rateMap.put(date.minusYears(2).plusDays(5), rate5);
        rateMap.put(date.minusYears(2).plusDays(6), rate6);
        rateMap.put(date.minusYears(2).plusDays(7), rate7);
        rateMap.put(date.minusYears(2).plusDays(8), rate8);

        rateMap.put(date.minusYears(3).plusDays(1), rate7);
        rateMap.put(date.minusYears(3).plusDays(2), rate6);
        rateMap.put(date.minusYears(3).plusDays(3), rate5);
        rateMap.put(date.minusYears(3).plusDays(4), rate4);
        rateMap.put(date.minusYears(3).plusDays(5), rate3);
        rateMap.put(date.minusYears(3).plusDays(6), rate2);
        rateMap.put(date.minusYears(3).plusDays(7), rate1);
        rateMap.put(date.minusYears(3).plusDays(8), rate8);
        Csv csv = new Csv(rateMap);

        Map<Currency, Csv> tableStorage = new HashMap<>();
        tableStorage.put(currency, csv);
        storage = new Storage(tableStorage);
    }

    @Test
    void someDayRate() {
        algorithm = new Actual(storage);
        var actual = algorithm.someDayRate(date.plusDays(1), currency).getCourse();
        assertThat(actual).isEqualTo(BigDecimal.valueOf(130));
    }

    @Test
    void weekRate() {
        algorithm = new Actual(storage);
        List<BigDecimal> expect = Stream.of(130, 60, 60, 60, 60, 60, 130)
                .map(BigDecimal::valueOf)
                .toList();
        var actual = algorithm.weekRate(date, currency).stream()
                .map(Rate::getCourse)
                .collect(Collectors.toList());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void monthRate() {
        algorithm = new Actual(storage);
        List<BigDecimal> expect = Stream.of(130, 60, 60, 60, 60, 60, 130, 320)
                .map(BigDecimal::valueOf)
                .toList();
        var actual = algorithm.monthRate(date, LocalDate.of(2022, 4, 1), currency)
                .stream()
                .map(Rate::getCourse)
                .collect(Collectors.toList());
        assertThat(actual).isEqualTo(expect);
    }
}