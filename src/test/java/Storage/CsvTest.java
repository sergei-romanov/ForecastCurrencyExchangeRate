package Storage;

import org.junit.jupiter.api.Test;
import storage.Csv;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CsvTest {

    @Test
    void parseCsvFromTable() {
        var path = Path.of("src/test/resources/CsvHelper.txt");
        var csv = Csv.createCsv(path);
        var actual = csv.getTable();
        var expect = List.of(List.of("01.02.2022", "86,5032", "Евро"),
                List.of("29.01.2022", "86,6419", "Евро"),
                List.of("28.01.2022", "88,4680", "Евро"));
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i)).isEqualTo(expect.get(i));
        }
    }

    @Test
    void parseCsvFromTableHeader() {
        var path = Path.of("src/test/resources/CsvHelper.txt");
        var csv = Csv.createCsv(path);
        var actual = csv.getHeader();
        String[] expect = {"data", "curs", "cdx"};
        assertThat(actual).isEqualTo(expect);
    }
}