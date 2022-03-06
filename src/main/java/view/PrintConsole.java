package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PrintConsole implements Print {
    /**
     * @param list значений курса валюты соответсвующее дню или дням, следующим за переданной датой
     * @param date начальная дата со следующего дня которой начинается вывод
     */
    @Override
    public void print(List<Double> list, LocalDate date) {
        list.stream()
                .map(avg -> createLine(date.plusDays(list.indexOf(avg) + 1), avg))
                .forEach(System.out::print);
    }

    /**
     * @param date  дата
     * @param value значение курса на текущую дату
     * @return строка в формате: Вт 22.02.2022 - 6,11
     */
    private String createLine(LocalDate date, Double value) {
        var formatter = DateTimeFormatter
                .ofPattern("E dd.MM.yyyy", new Locale("RU"));
        var result = String.format("%s - %.2f\n", date.format(formatter), value);
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }
}
