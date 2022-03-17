package algorithms;

import model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Алгоритм “Актуальный”.
 * Рассчитывается, как сумма курса за (текущий год - 2 + текущий год - 3),
 * то есть прогноз на 25.12.2022 будет считаться как прогноз 25.12.2020 + 25.12.2019.
 * Если число сильно впереди и нет данных за год - кидать ошибку.
 */
public class Actual extends Algorithm {
    private static final Logger LOG = LoggerFactory.getLogger(Actual.class);

    public Actual(Storage storage) {
        super(storage);
    }

    @Override
    protected BigDecimal doSomeDayRate(LocalDate date, Map<LocalDate, Rate> csv) {
        if (LocalDate.now().plusYears(1).isBefore(date)) {
            LOG.error("Введена не корректная дата при использовании алгоритма  Actual: {}", date);
            throw new IllegalArgumentException("Дата сильно впереди и нет данных за год");
        }
        BigDecimal course2YearAgo = csv.get(date.minusYears(2)).getCourse();
        BigDecimal course3YearAgo = csv.get(date.minusYears(3)).getCourse();
        return course2YearAgo.add(course3YearAgo);
    }
}
