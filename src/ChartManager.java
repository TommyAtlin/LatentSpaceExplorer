import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import java.util.List;
import java.util.function.Consumer;

public class ChartManager {
    private final ScatterChart<Number, Number> scatterChart;
    private final Projections projectionService;

    public ChartManager(ScatterChart<Number, Number> scatterChart, Projections projectionService) {
        if (scatterChart == null) {
            throw new IllegalArgumentException("Scatter chart cannot be null");
        }

        if (projectionService == null) {
            throw new IllegalArgumentException("Projection service cannot be null");
        }

        this.scatterChart = scatterChart;
        this.projectionService = projectionService;
    }

    public void refreshChart(
            WordStorage holder,
            int xIndex,
            int yIndex,
            String normalStyle,
            Consumer<Word> wordClickHandler
    ) {
        if (holder == null) {
            throw new IllegalArgumentException("WordStorage cannot be null");
        }

        scatterChart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (Word word : holder.getWords()) {
            Point2D point = projectionService.projectWord(word, xIndex, yIndex);

            XYChart.Data<Number, Number> data =
                    new XYChart.Data<>(point.getX(), point.getY());

            data.setExtraValue(word);

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(normalStyle);

                    Tooltip.install(newNode, new Tooltip(word.getWord()));

                    if (wordClickHandler != null) {
                        newNode.setOnMouseClicked(e -> wordClickHandler.accept(word));
                    }
                }
            });

            series.getData().add(data);
        }

        scatterChart.getData().add(series);

        NumberAxis xAxis = (NumberAxis) scatterChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();

        xAxis.setLabel("PCA X");
        yAxis.setLabel("PCA Y");

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
    }

    public void addHighlightedWordsToChart(
            List<Word> words,
            String title,
            String style,
            int xIndex,
            int yIndex
    ) {
        if (words == null || words.isEmpty()) {
            return;
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(title);

        for (Word word : words) {
            Point2D point = projectionService.projectWord(word, xIndex, yIndex);

            XYChart.Data<Number, Number> data =
                    new XYChart.Data<>(point.getX(), point.getY());

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(style);
                    Tooltip.install(newNode, new Tooltip(word.getWord()));
                }
            });

            series.getData().add(data);
        }

        scatterChart.getData().add(series);
    }

    public void centerOnPoint(Point2D point, double range) {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }

        if (range <= 0) {
            throw new IllegalArgumentException("Range must be positive");
        }

        NumberAxis xAxis = (NumberAxis) scatterChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        xAxis.setLowerBound(point.getX() - range);
        xAxis.setUpperBound(point.getX() + range);
        yAxis.setLowerBound(point.getY() - range);
        yAxis.setUpperBound(point.getY() + range);
    }

    public void clearChart() {
        scatterChart.getData().clear();
    }

    public ScatterChart<Number, Number> getScatterChart() {
        return scatterChart;
    }
}