package telegram;

import algorithms.*;
import controller.Controller;
import model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Storage;
import utils.LocalDateUtils;
import view.Output;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseMessage {
    private static final Logger LOG = LoggerFactory.getLogger(ParseMessage.class);

    public static Controller parse(String line) {
        var commands = Arrays.stream(line.split(" ")).toList();
        try {
            validate(commands);
            Algorithm algorithm = getAlgorithm(commands);
            Output output = new Output();
            Period period = getPeriod(commands);
            LocalDate date = null;
            if (period == Period.DATA) {
                date = getDate(commands);
            }
            List<Currency> currencies = getCurrencies(commands);
            LOG.debug("create Controller with currencies: {}, period: {}, date: {}", currencies, period, date);
            return new Controller(currencies, period, date, algorithm, output);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            LOG.debug("create Controller with help menu");
            return new Controller(null, null, null, null, new Output());
        }
    }

    private static void validate(List<String> commands) throws IllegalArgumentException {
        if (commands.size() < 4) {
            throw new IllegalArgumentException();
        }
        if (!commands.contains("rate")) {
            throw new IllegalArgumentException();
        }
    }

    private static LocalDate getDate(List<String> commands) throws DateTimeParseException {
        int index = commands.indexOf("-date") + 1;
        if (index == 0) {
            throw new IllegalArgumentException();
        }
        return LocalDate.parse(commands.get(index), LocalDateUtils.FORMATTER);
    }

    private static Period getPeriod(List<String> commands) {
        int index = commands.indexOf("-period") + 1;
        return switch (commands.get(index).toLowerCase()) {
            case "tomorrow" -> Period.TOMORROW;
            case "week" -> Period.WEEK;
            case "month" -> Period.MONTH;
            default -> Period.DATA;
        };
    }

    private static Algorithm getAlgorithm(List<String> commands) throws IllegalArgumentException {
        int index = commands.indexOf("-alg") + 1;
        if (index == 0) {
            throw new IllegalArgumentException();
        }
        Storage storage = new Storage();
        return switch (commands.get(index).toLowerCase()) {
            case "actual" -> new Actual(storage);
            case "internet" -> new FromInternet(storage);
            case "moon" -> new Mystical(storage);
            default -> null;
        };
    }

    private static List<Currency> getCurrencies(List<String> commands) throws IllegalArgumentException {
        List<Currency> currencies = new ArrayList<>();
        String[] currency = commands.get(1).split(",");
        if (currency.length > 5) {
            throw new IllegalArgumentException();
        }
        Arrays.stream(currency)
                .map(Currency::parseCurrency)
                .forEach(currencies::add);
        return currencies.size() == 0 ? null : currencies;
    }
}
