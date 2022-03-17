package Storage;

import model.Csv;
import model.Rate;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CsvTest {

    @Test
    void parseCsvFromTable() {
        var path = Path.of("src/test/resources/CsvHelper.txt");
        var csv = new Csv(path);
        var table = csv.getTable();
        List<String> actual = new ArrayList<>();
        table.values().stream().sorted().map(Rate::toString).forEach(actual::add);
        var expect = List.of(
                "1 05.03.2022 7.44 TRY",
                "1 04.03.2022 7.90 TRY",
                "1 03.03.2022 7.36 TRY",
                "1 02.03.2022 6.61 TRY",
                "1 01.03.2022 6.77 TRY",
                "1 28.02.2022 5.94 TRY",
                "1 27.02.2022 5.94 TRY",
                "1 26.02.2022 5.94 TRY",
                "1 25.02.2022 6.15 TRY",
                "1 24.02.2022 5.84 TRY",
                "1 23.02.2022 5.84 TRY");
        assertThat(actual).isEqualTo(expect);
    }
}