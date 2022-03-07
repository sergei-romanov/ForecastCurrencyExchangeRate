package model;

import exception.CurrencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Currency {
    USD("Доллар США"),
    EUR("Евро"),
    TRY("Турецкая лира"),
    AMD("Армянский драм"),
    BGN("Болгарский лев");
    private static final Logger LOG = LoggerFactory.getLogger(Currency.class);
    private final String name;

    Currency(String title) {
        this.name = title;
    }

    public String getName() {
        return name;
    }

    public static Currency getCurrency(String title) {
        return switch (title) {
            case "Доллар США" -> Currency.USD;
            case "Евро" -> Currency.EUR;
            case "Турецкая лира" -> Currency.TRY;
            case "Армянский драм" -> Currency.AMD;
            case "Болгарский лев" -> Currency.BGN;
            default -> {
                LOG.error("Некорректный тип валюты: {}", title);
                throw new CurrencyException();
            }
        };
    }
}
