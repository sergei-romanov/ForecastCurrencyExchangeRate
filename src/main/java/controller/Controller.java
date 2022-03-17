package controller;

import algorithms.Algorithm;
import algorithms.Period;
import model.Currency;
import model.Rate;
import view.Output;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Controller(List<Currency> currencies, Period period, LocalDate date,
                         Algorithm algorithm, Output output) {

    public String operate() {
        if (currencies == null) {
            return getHelp();
        }
        if (currencies.size() == 1) {
            return getRate();
        }
        getChart();
        return "chart";
    }

    private void getChart() {
        Map<Currency, List<Rate>> map;
        LocalDate date = LocalDate.now();
        switch (period) {
            case WEEK -> {
                map = currencies.stream()
                        .collect(Collectors.toMap(e -> e, e -> algorithm.weekRate(date.plusDays(1), e)));
                output.printChart(map);
            }
            case MONTH -> {
                map = currencies.stream()
                        .collect(Collectors.toMap(e -> e, e -> algorithm.monthRate(date.plusDays(1),
                                LocalDate.of(date.getYear(), date.getMonth().plus(1), 1), e)));
                output.printChart(map);
            }
        }
    }

    private String getRate() {
        List<Rate> rates = new ArrayList<>();
        return switch (period) {
            case DATA -> {
                rates.add(algorithm.someDayRate(date, currencies.get(0)));
                yield output.printData(rates);
            }
            case TOMORROW -> {
                rates.add(algorithm.someDayRate(LocalDate.now().plusDays(1), currencies.get(0)));
                yield output.printData(rates);
            }
            case WEEK -> output.printData(algorithm.weekRate(LocalDate.now().plusDays(1), currencies.get(0)));
            case MONTH -> {
                LocalDate date = LocalDate.now();
                yield output.printData(algorithm.monthRate(date.plusDays(1)
                        , LocalDate.of(date.getYear(), date.getMonth().plus(1), 1), currencies.get(0)));
            }
        };
    }

    public String getHelp() {
        return """
                Параметры программы задаются при запуске, по порядку:
                1. Вид запроса(курс валюты):  rate
                2. Tип валюты:                AMD BGN TRY USD EUR
                3. Период:                    -period(обязательный ключ)
                        - определенная дата:  date
                        - завтра:             tomorrow
                        - на неделю вперед:   week
                        - до конца месяца:    month
                4. Алгоритм прогноза:         -alg(обязательный ключ)
                        - актуальный:         actual
                        - мистический:        moon
                        - из интернета:       internet
                5. Вывод в виде:              -graph, -list
                Пример команд:
                rate TRY -date 22.02.2030 -alg moon
                rate USD,TRY -period month -alg moon -output graph
                Вывести help menu: help
                """;
    }
}
