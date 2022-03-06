package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.Csv;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class ArithmeticMeanTest {
    private Csv csv;

    @BeforeEach
    void setup() throws IOException {
        var path = Path.of("src/test/resources/CsvArithmeticMean.txt");
        csv = Csv.createCsv(path);
    }

    @Test
    void countingDay() {
        var arithmeticMean = new ArithmeticMean();
        var actual = arithmeticMean.calculating(csv, Period.DAY).get(0);
        assertThat(actual).isEqualTo(87.74, withPrecision(3d));
    }

    @Test
    void countingWeek() {
        ArithmeticMean arithmeticMean = new ArithmeticMean();
        var actual = arithmeticMean.calculating(csv, Period.WEEK)
                .stream()
                .map(x -> String.format("%.2f", x))
                .collect(Collectors.toList());
        var expect = List.of("87,74", "87,92", "88,10", "88,05", "87,89", "87,74", "87,76");
        assertThat(actual).isEqualTo(expect);
    }
}




















 /*
        double day1 = (86.5032 + 86.6419 + 88.4680 + 89.1511 + 88.9286 + 87.5925 + 86.9054) / 7;
        double day2 = (87.7415 + 86.6419 + 88.4680 + 89.1511 + 88.9286 + 87.5925 + 86.9054) / 7;
        double day3 = (87.9184 + 87.7415 + 88.4680 + 89.1511 + 88.9286 + 87.5925 + 86.9054) / 7;
        double day4 = (87.9184 + 87.7415 + 88.1007 + 89.1511 + 88.9286 + 87.5925 + 86.9054) / 7;
        double day5 = (87.9184 + 87.7415 + 88.1007 + 88.0483 + 88.9286 + 87.5925 + 86.9054) / 7;
        double day6 = (87.9184 + 87.7415 + 88.1007 + 88.0483 + 87.8907 + 87.5925 + 86.9054) / 7;
        double day7 = (87.9184 + 87.7415 + 88.1007 + 88.0483 + 87.8907 + 87.7424 + 86.9054) / 7;
        */