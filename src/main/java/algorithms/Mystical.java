package algorithms;

import model.Currency;
import model.Rate;
import org.shredzone.commons.suncalc.MoonPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Storage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
/**
 * Алгоритм “Мистический”
 * - Для расчета на дату используем среднее арифметическое из трех последних от этой даты полнолуний.
 * - Для расчета на неделю и месяц. Первый курс рассчитывается аналогично предыдущему пункту.
 * Последующие даты рассчитываются рекуррентно по формуле - значение предыдущей даты +
 * случайное число от -10% до +10% от значения предыдущей даты.
 */
public class Mystical extends Algorithm {
    private final static LocalDate LAST_DATE_WITH_CURRENT_DATA = LocalDate.of(2022,3,5);
    private static final Logger LOG = LoggerFactory.getLogger(Mystical.class);

    public Mystical(Storage storage) {
        super(storage);
    }

    @Override
    protected BigDecimal doSomeDayRate(LocalDate date, Map<LocalDate, Rate> table) {
        List<LocalDate> moonPhase = getLastThreeMoonPhase();
        LOG.info("Даты последних 3х полнолуний: {}", moonPhase);
        BigDecimal sum = moonPhase.stream()
                .map(table::get)
                .map(Rate::getCourse)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        return sum.divide(BigDecimal.valueOf(3), RoundingMode.FLOOR);
    }

    @Override
    public List<Rate> weekRate(LocalDate date, Currency currency) {
        return getRates(date, currency, 7);
    }

    @Override
    public List<Rate> monthRate(LocalDate start, LocalDate end, Currency currency) {
        var countDayForEndMonth = (int) DAYS.between(start, end);
        return getRates(start, currency, countDayForEndMonth);
    }

    private List<Rate> getRates(LocalDate date, Currency currency, int amountDay) {
        List<Rate> rates = new ArrayList<>();
        Rate rootRate = someDayRate(date.plusDays(1), currency);
        rates.add(rootRate);
        for (int i = 1; i < amountDay; i++) {
            Rate previous = rates.get(i - 1);
            rates.add(new Rate.Builder()
                    .withNominal(1)
                    .withCurrency(currency)
                    .withDate(previous.getDate().plusDays(1))
                    .withCourse(generateRandomCourse(previous.getCourse()))
                    .build());
        }
        return rates;
    }

    /**
     * @return случайное число от -10% до +10% от входящего значения
     */
    private BigDecimal generateRandomCourse(BigDecimal course) {
        BigDecimal max = new BigDecimal(String.valueOf(course.multiply(BigDecimal.valueOf(1.1))));
        BigDecimal min = new BigDecimal(String.valueOf(course.multiply(BigDecimal.valueOf(0.9))));
        BigDecimal range = max.subtract(min);
        return min.add(range.multiply(BigDecimal.valueOf(Math.random())));
    }

    private static List<LocalDate> getLastThreeMoonPhase() {
        LocalDate date = LAST_DATE_WITH_CURRENT_DATA.minusYears(2);
        List<LocalDate> moon = new ArrayList<>();
        MoonPhase.Parameters parameters = MoonPhase.compute()
                .phase(MoonPhase.Phase.FULL_MOON);
        while (true) {
            MoonPhase moonPhase = parameters
                    .on(date)
                    .execute();
            LocalDate nextFullMoon = moonPhase
                    .getTime()
                    .toLocalDate();
            if (nextFullMoon.isAfter(LAST_DATE_WITH_CURRENT_DATA)) {
                break;
            }
            moon.add(nextFullMoon);
            date = nextFullMoon.plusDays(1);
        }
        return moon.stream()
                .skip(moon.size() - 3)
                .collect(Collectors.toList());
    }
}
