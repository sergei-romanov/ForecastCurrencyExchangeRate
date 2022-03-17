package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Currency {
    USD("Доллар США"),
    EUR("Евро"),
    TRY("Турецкая лира"),
    AMD("Армянский драм"),
    BGN("Болгарский лев");
    private static final Logger LOG = LoggerFactory.getLogger(Currency.class);

    Currency(String title) {
    }

    public static Currency getCurrency(String title) {
        return switch (title) {
            case "Доллар США" -> Currency.USD;
            case "Евро" -> Currency.EUR;
            case "Турецкая лира" -> Currency.TRY;
            case "Армянский драм" -> Currency.AMD;
            case "Болгарский лев" -> Currency.BGN;
            default -> {
                LOG.error("Некорректная стока для извлечения типа валюты : {}", title);
                yield null;
            }
        };
    }
    public static Currency parseCurrency(String title) {
        return switch (title.toUpperCase()) {
            case "USD" -> Currency.USD;
            case "EUR" -> Currency.EUR;
            case "TRY" -> Currency.TRY;
            case "AMD" -> Currency.AMD;
            case "BGN" -> Currency.BGN;
            default -> {
                LOG.error("Некорректная аббревиатура для извлечения типа валюты : {}", title);
                yield null;
            }
        };
    }
}
