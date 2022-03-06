package storage;

import exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * объект хранения, представляющий Csv
 */
public class Csv {
    private static final Logger LOG = LoggerFactory.getLogger(Csv.class);
    private final String[] header;
    private final List<List<String>> table;

    private Csv(String[] header, List<List<String>> table) {
        this.header = header;
        this.table = table;
    }

    /**
     * @param path путь к файлу Csv
     * @return Csv файл типа: массив header[]{"data", "curs", "cdx"} - заголовок таблицы,
     * table - list массивов строк {"01.02.2022", "86.5032", "Евро"} с соответствующими ключу валютой
     */
    public static Csv createCsv(Path path) {
        String[] header = new String[0];
        List<List<String>> table = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(path);
            header = allLines.get(0).split(";");
            for (int i = 1; i < allLines.size(); i++) {
                table.add(Arrays.stream(allLines.get(i).split(";")).toList());
            }
        } catch (IOException e) {
            LOG.error("Некорректный источник данных, по указанному пути: {}", path);
            System.exit(1);
        }
        LOG.info("csv создан, header: {}", Arrays.toString(header));
        return new Csv(header, table);
    }

    public String[] getHeader() {
        return header;
    }

    public List<List<String>> getTable() {
        return table;
    }

    /**
     * @param start начальный индекс диапазона
     * @param end   конечный индекс диапазона
     * @return new list из таблицы значений Csv, со start включительно по end не включительно
     */
    public List<List<String>> getPartTable(List<List<String>> csvTable, int start, int end) {
        checkIndex(csvTable, start, end);
        return IntStream.range(start, end)
                .mapToObj(csvTable::get)
                .collect(Collectors.toList());
    }

    /**
     * @param csv1Table list таблицы значений Csv
     * @param csv2Table list таблицы значений Csv
     * @return new list объединяющий таблицы значений Csv
     */
    public List<List<String>> joinTable(List<List<String>> csv1Table, List<List<String>> csv2Table) {
        ArrayList<List<String>> result = new ArrayList<>(csv1Table);
        result.addAll(csv2Table);
        return result;
    }

    /**
     * @param start начальный индекс диапазона
     * @param end   конечный индекс диапазона
     *              Если индексы некорректны throw new StorageException()
     */
    private void checkIndex(List<List<String>> csvTable, int start, int end) {
        if (end > csvTable.size() || start < 0 || start > end) {
            LOG.error("Некорректные индексы, из таблицы : {}", csvTable);
            throw new StorageException();
        }
    }
}

