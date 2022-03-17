package view;

import model.Currency;
import model.Rate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Output {

    public String printData(List<Rate> rate) {
        StringBuilder sb = new StringBuilder();
        rate.stream()
                .map(this::createLine)
                .forEach(sb::append);
        return sb.toString();
    }

    public void printChart(Map<Currency, List<Rate>> map) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        for (Map.Entry<Currency, List<Rate>> pair : map.entrySet()) {
            final XYSeries element = new XYSeries(pair.getKey().toString());
            pair.getValue().forEach(e -> element.add(e.getDate().getDayOfMonth(), e.getCourse().doubleValue()));
            dataset.addSeries(element);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Course currencies statistics",
                "days",
                "course",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        int width = 640;
        int height = 480;
        File XYChart = new File("src/main/resources/XYLineChart.jpeg");
        try {
            ChartUtilities.saveChartAsJPEG(XYChart, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rate прогноз
     * @return строка в формате: Вт 22.02.2022 - 6,11
     */
    private String createLine(Rate rate) {
        LocalDate date = rate.getDate();
        BigDecimal value = rate.getCourse();
        var formatter = DateTimeFormatter.ofPattern("E dd.MM.yyyy", new Locale("RU"));
        var result = String.format("%s - %.2f\n", date.format(formatter), value);
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }
}
