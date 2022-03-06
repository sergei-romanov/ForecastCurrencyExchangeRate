package utils;

import algorithms.Period;

public class MainUtils {
    /**
     * @param lineArguments массив значений командной строки
     * @return количество дней типа int, соответсвующее ключу периода из командной строки
     */
    public static Period getPeriod(String[] lineArguments) {
        return lineArguments[2].equals("tomorrow") ? Period.DAY : Period.WEEK;
    }


    /**
     * @param lineArguments массив значений командной строки
     * @return true если значения корректны
     */
    public static boolean argumentsLineCorrect(String[] lineArguments) {
        if (lineArguments.length != 3) {
            return false;
        }
        return "rate".equals(lineArguments[0])
                && ("tomorrow".equals(lineArguments[2]) || "week".equals(lineArguments[2]));
    }

    /**
     * info по работе с программой
     */
    public static void printMan() {
        var message = """
                Параметры программы задаются при запуске, по порядку:
                1. Вид запроса(курс валюты):   rate
                2. Период:                     tomorrow week
                3. Tип валюты:                 TRY USD ERO
                Пример запуска из консоли: rate TRY tomorrow
                Вывести help menu: help
                Выход: exit
                """;
        System.out.println(message);
    }
}
