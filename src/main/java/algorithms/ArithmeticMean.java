package algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Csv;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Алгоритм прогнозирования среднего арифметического значение на основании 7 последних курсов
 */
public class ArithmeticMean implements Algorithms {
    private static final Logger logger = LoggerFactory.getLogger(ArithmeticMean.class);

    /**
     * @param csv    таблица Csv
     * @param period в виде числа enum Period на которое нужно вычислить прогноз курса валют
     * @return list из одного или семи элементов типа double, спрогнозированных значений курса валют на основании
     * aлгоритма прогнозирования среднего арифметического значение на основании 7 последних курсов
     */
    @Override
    public List<Double> calculating(Csv csv, Period period) {
        return switch (period) {
            case DAY -> calculatingDay(csv);
            case WEEK -> calculatingWeek(csv);
        };
    }

    /**
     * @param csv таблицы значений Csv
     * @return list из одного элемента со средним значением курса валют за последние 7 дней из таблицы значений Csv
     */
    private List<Double> calculatingDay(Csv csv) {
        var size = csv.getTable().size();
        return Stream.of(getAverage(csv.getPartTable(csv.getTable(), size - 7, size)))
                .collect(Collectors.toList());
    }

    /**
     * @param csv таблица значений Csv
     * @return list из 7 элементов:
     * 1ый вычисляется как среднее значение курса валют за последние 7 дней из таблицы значений Csv
     * 2ой как среднее значение курса валют за последние 6 дней из таблицы значений Csv, плюс 1ый вычисленный элемент
     * 7ой вычисляется как среднее значение курса валют за последний день из таблицы значений Csv,
     * плюс 6 вычисленных значений
     */
    private List<Double> calculatingWeek(Csv csv) {
        List<Double> result = new ArrayList<>();
        var size = csv.getTable().size();
        for (int i = 0; i < 7; i++) {
            var tableReal = csv.getPartTable(csv.getTable(), size - 7 + i, size);
            var tableVirtual = result.stream()
                    .map(e -> List.of("", e.toString(), ""))
                    .collect(Collectors.toCollection(ArrayList::new));
            result.add(getAverage(csv.joinTable(tableReal, tableVirtual)));
            tableVirtual.clear();
        }
        return result;
    }

    /**
     * @param list таблица значений Csv
     * @return число типа double, среднее значение курса валют, из переданной таблицы значений Csv
     */
    private double getAverage(List<List<String>> list) {
        return list.stream()
                .mapToDouble(e -> Double.parseDouble(e.get(1).replace(",", ".")))
                .average()
                .orElse(0.0);
    }
}