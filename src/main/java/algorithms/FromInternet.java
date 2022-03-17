package algorithms;

import model.Currency;
import model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Storage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.DoubleStream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * алгоритм линейной регрессии
 */
public class FromInternet extends Algorithm {
    private static final Logger LOG = LoggerFactory.getLogger(FromInternet.class);
    private final static double AMOUNT_POINTS = 30.0;

    public FromInternet(Storage storage) {
        super(storage);
    }

    @Override
    protected BigDecimal doSomeDayRate(LocalDate date, Map<LocalDate, Rate> csv) {
        var countDays = (int) DAYS.between(date, date);
        BigDecimal result = BigDecimal.valueOf(getElementLinerRegression(csv, AMOUNT_POINTS + countDays));
        LOG.info("Спрогнозированный курс на {} на основе алгоритма линейной регрессии: {}", date, result);
        return result;
    }

    /**
     * @param csv     map прогнозами, на основе 30 последних будет строиться линейная регрессия
     * @param nextDay день на который нужен прогноз
     * @return прогнозируемый курс валюты на указанную дату
     */
    private Double getElementLinerRegression(Map<LocalDate, Rate> csv, double nextDay) {
        double[] courseForLast30Days = getCourseForLast30Days(csv);
        Currency currency = csv.values().stream().map(Rate::getCurrency).findFirst().orElse(null);
        LOG.info("курс {} за последние 30 дней для расчета линейной регрессии: {}"
                , currency, Arrays.toString(courseForLast30Days));
        double[] array = DoubleStream.iterate(1.0, n -> n + 1.0)
                .limit(30)
                .toArray();
        LinearRegression linearRegression = new LinearRegression(array, courseForLast30Days);
        return linearRegression.predict(nextDay);
    }

    /**
     * @param csv map прогнозами, на основе 30 последних будет строиться линейная регрессия
     * @return массив последних 30 прогнозов валюты, начиная с последнего из указанного в файле
     * если есть пропуски то считается, что курс не менялся
     */
    private double[] getCourseForLast30Days(Map<LocalDate, Rate> csv) {
        return csv.values().stream()
                .sorted()
                .limit(30)
                .sorted(Comparator.reverseOrder())
                .map(e -> e.getCourse().doubleValue())
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    private static class LinearRegression {
        private final double intercept, slope;
        private final double r2;
        private final double svar0, svar1;

        /**
         * Performs a linear regression on the data points {@code (y[i], x[i])}.
         *
         * @param x the values of the predictor variable
         * @param y the corresponding values of the response variable
         * @throws IllegalArgumentException if the lengths of the two arrays are not equal
         */
        public LinearRegression(double[] x, double[] y) {
            if (x.length != y.length) {
                throw new IllegalArgumentException("array lengths are not equal");
            }
            int n = x.length;
            double sumX = 0.0, sumY = 0.0;
            for (int i = 0; i < n; i++) {
                sumX += x[i];
                sumY += y[i];
            }
            double barX = sumX / n;
            double barY = sumY / n;
            double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
            for (int i = 0; i < n; i++) {
                xxbar += (x[i] - barX) * (x[i] - barX);
                yybar += (y[i] - barY) * (y[i] - barY);
                xybar += (x[i] - barX) * (y[i] - barY);
            }
            slope = xybar / xxbar;
            intercept = barY - slope * barX;
            double rss = 0.0;
            double ssr = 0.0;
            for (int i = 0; i < n; i++) {
                double fit = slope * x[i] + intercept;
                rss += (fit - y[i]) * (fit - y[i]);
                ssr += (fit - barY) * (fit - barY);
            }
            int degreesOfFreedom = n - 2;
            r2 = ssr / yybar;
            double svar = rss / degreesOfFreedom;
            svar1 = svar / xxbar;
            svar0 = svar / n + barX * barX * svar1;
        }

        /**
         * @param x the value of the predictor variable
         * @return the expected response {@code y} given the value of the predictor
         * variable {@code x}
         */
        public double predict(double x) {
            return slope * x + intercept;
        }
    }
}
