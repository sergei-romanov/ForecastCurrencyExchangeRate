package algorithms;

import model.Currency;
import model.Rate;
import storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;

public abstract class Algorithm {
    private final Storage storage;

    public Algorithm(Storage storage) {
        this.storage = storage;
    }

    public Rate someDayRate(LocalDate date, Currency currency) {
        BigDecimal course = doSomeDayRate(date, storage.getTable().get(currency).getTable());
        return new Rate.Builder()
                .withNominal(1)
                .withDate(date)
                .withCourse(course)
                .withCurrency(currency)
                .build();
    }

    protected abstract BigDecimal doSomeDayRate(LocalDate date, Map<LocalDate, Rate> table);

    public List<Rate> weekRate(LocalDate date, Currency currency) {
        List<Rate> weekRate = new ArrayList<>();
        IntStream.range(1, 8)
                .mapToObj(day -> someDayRate(date.plusDays(day), currency))
                .forEach(weekRate::add);
        return weekRate;
    }

    public List<Rate> monthRate(LocalDate start, LocalDate end, Currency currency) {
        List<Rate> monthRate = new ArrayList<>();
        var countDayForEndMonth = (int) DAYS.between(start, end);
        IntStream.range(1, countDayForEndMonth + 1)
                .mapToObj(day -> someDayRate(start.plusDays(day), currency))
                .forEach(monthRate::add);
        return monthRate;
    }
}
