package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LocalDateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Csv {
    private static final Logger LOG = LoggerFactory.getLogger(Csv.class);
    private final Map<LocalDate, Rate> table;
    private final static int MAX_DAY = 1000;

    public Csv(Map<LocalDate, Rate> table) {
        this.table = table;
    }

    /**
     * @param path to Csv
     *             create table - map key- date value - Rate,
     *             example: 01.02.2022 - {"1", "01.02.2022", "86.5032", "Евро"}
     */
    public Csv(Path path) {
        Map<LocalDate, Rate> table = new HashMap<>();
        try {
            List<String> allLines = new ArrayList<>();
            File file = new File(path.toString());
            BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            String line;
            int count = 0;
            while (count < MAX_DAY && (line = br.readLine()) != null) {
                count++;
                allLines.add(line);
            }
            LOG.debug("List<String> первые две строки парсинга из файла:\n{}"
                    , allLines.stream().limit(2).collect(Collectors.toList()));
            table = validationTable(createTable(allLines)).stream()
                    .collect(Collectors.toMap(Rate::getDate, value -> value));
            LOG.debug("csv создан:\n{}", path);
        } catch (IOException e) {
            LOG.error("Некорректный источник данных, по указанному пути: {}", path);
        }
        this.table = table;

    }

    public Map<LocalDate, Rate> getTable() {
        return table;
    }

    private List<Rate> createTable(List<String> lines) {
        return IntStream.range(1, lines.size())
                .mapToObj(i -> Arrays.stream(lines.get(i).split(";")).toList())
                .map(Csv::createRate)
                .map(Csv::validationNominal)
                .collect(Collectors.toList());
    }

    /**
     * Исходя из того если есть пропуски, то считается что курс не изменился -> добавляются данные для каждого дня.
     */
    private List<Rate> validationTable(List<Rate> rates) {
        List<Rate> result = new ArrayList<>();
        for (int i = 0; i < rates.size() - 1; i++) {
            var rate1st = rates.get(i).cloneRate();
            var rate2nd = rates.get(i + 1);
            result.add(rate1st);
            var delta = (int) ChronoUnit.DAYS.between(rate2nd.getDate(), rate1st.getDate());
            if (delta > 1) {
                for (int j = 1; j < delta; j++) {
                    var rate = rate2nd.cloneRate();
                    rate.setDate(rate1st.getDate().minusDays(j));
                    result.add(rate);
                }
            }
        }
        result.add(rates.get(rates.size() - 1));
        StringBuilder sb = new StringBuilder();
        result.stream().limit(10).forEach(e -> sb.append(e).append("\n"));
        LOG.debug("List<Rate> первые 10 элементов при валидации:\n{} ", sb);
        return result;
    }

    /**
     * @param lineTable строка таблицы прогнозов
     * @return эта же строка, если наминал отличен от единицы -> пересчитывается курс на 1 единицу
     */
    private static Rate validationNominal(Rate lineTable) {
        var nominal = lineTable.getNominal();
        if (nominal != 1) {
            var rate = lineTable.getCourse().divide(BigDecimal.valueOf(nominal), RoundingMode.FLOOR);
            lineTable.setCourse(rate);
            lineTable.setNominal(1);
        }
        return lineTable;
    }

    private static Rate createRate(List<String> line) {
        var rate = line.get(2).substring(1, line.get(2).length() - 1).replace(",", ".");
        return new Rate.Builder()
                .withNominal(Integer.parseInt(line.get(0)))
                .withDate(LocalDate.parse(line.get(1), LocalDateUtils.FORMATTER))
                .withCourse(BigDecimal.valueOf(Double.parseDouble(rate)))
                .withCurrency(Currency.getCurrency(line.get(3).trim()))
                .build();
    }


}

