package storage;

import model.Csv;
import model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Storage {
    private static final Logger LOG = LoggerFactory.getLogger(Storage.class);
    private final Map<Currency, Csv> table;


    public Storage(Map<Currency, Csv> table) {
        this.table = table;
    }

    public Storage() {
        List<Path> list = getPaths();
        this.table = list.stream()
                .filter(x -> getCurrency(x) != null)
                .peek(x -> LOG.debug("создание key - currency: {} from path: {}", getCurrency(x), x))
                .collect(Collectors.toMap(this::getCurrency, Csv::new));
        LOG.debug("storage создан, размер: {}", getInfo());
    }

    public Map<Currency, Csv> getTable() {
        return table;
    }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Currency, Csv> pair : table.entrySet()) {
            String message = String.format("\nКлюч currency: %s - количество элементов rate: %d"
                    , pair.getKey().toString(), pair.getValue().getTable().keySet().size());
            sb.append(message);
        }
        return sb.toString();
    }

    /**
     * @param line path
     * @return element Enum Currency from path
     */
    private Currency getCurrency(Path line) {
        String currency = line.toString().substring(19, 22);
        return Currency.parseCurrency(currency);
    }


    /**
     * @return list all path from catalog: src/main/resources
     */
    private List<Path> getPaths() {
        try {
            return Files.walk(Paths.get("src/main/resources"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
