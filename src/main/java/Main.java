import algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Csv;
import utils.MainUtils;
import view.PrintConsole;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static final String EUR_CSV = "src/main/resources/EUR_F01_02_2002_T01_02_2022.csv";
    public static final String TRY_CSV = "src/main/resources/TRY_F01_02_2002_T01_02_2022.csv";
    public static final String USD_CSV = "src/main/resources/USD_F01_02_2002_T01_02_2022.csv";

    public static void main(String[] args) {
        logger.info("Start program.");
        Csv csvEUR = Csv.createCsv(Path.of(EUR_CSV));
        Csv csvTRY = Csv.createCsv(Path.of(TRY_CSV));
        Csv csvUSD = Csv.createCsv(Path.of(USD_CSV));
        Scanner sc = new Scanner(System.in);
        String[] lineArgument;
        MainUtils.printMan();
        while (true) {
            lineArgument = sc.nextLine().trim().split(" ");
            if ("exit".equals(lineArgument[0])) {
                logger.info("End program.");
                break;
            }
            if ("help".equals(lineArgument[0])) {
                MainUtils.printMan();
                continue;
            }
            if (!MainUtils.argumentsLineCorrect(lineArgument)) {
                continue;
            }
            Csv csv = switch (lineArgument[1]) {
                case "TRY" -> csvTRY;
                case "EUR" -> csvEUR;
                case "USD" -> csvUSD;
                default -> null;
            };
            if (csv != null) {
                new PrintConsole()
                        .print(Algorithm.ARITHMETIC_MEAN
                                .getMethod(csv, MainUtils.getPeriod(lineArgument)), LocalDate.now());
            }
        }
        sc.close();
    }
}
