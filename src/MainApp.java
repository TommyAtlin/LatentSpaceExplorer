import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private static final String NORMAL_POINT_STYLE =
            "-fx-background-color: #f05a28; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-padding: 4px;";

    private static final String SELECTED_POINT_STYLE =
            "-fx-background-color: black; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-padding: 8px;";

    private static final String NEAREST_POINT_STYLE =
            "-fx-background-color: #ffd700; " +
                    "-fx-background-radius: 7px; " +
                    "-fx-padding: 7px;";

    private static final String INPUT_WORD_STYLE =
            "-fx-background-color: blue; " +
                    "-fx-background-radius: 7px; " +
                    "-fx-padding: 7px;";

    private static final String RESULT_WORD_STYLE =
            "-fx-background-color: green; " +
                    "-fx-background-radius: 9px; " +
                    "-fx-padding: 9px;";

    private static final String PROJECTION_POINT_STYLE =
            "-fx-background-color: #8e44ad; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-padding: 4px;";

    private WordStorage holder;
    private WordService wordService;

    private final Projections projectionService = new Projections();

    private ScatterChart<Number, Number> scatterChart;
    private ChartManager chartManager;
    private CommandInvoker commandInvoker;

    private ComboBox<Integer> xAxisBox;
    private ComboBox<Integer> yAxisBox;
    private ComboBox<Integer> zAxisBox;

    private TextField searchField;
    private TextField word1Field;
    private TextField word2Field;
    private TextField word3Field;
    private TextField kField;
    private TextField centroidField;

    private Label resultLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("LatentSpace Explorer");

        try {
            File fullFile = new File("full_vectors.json");
            File pcaFile = new File("pca_vectors.json");

            if (!fullFile.exists() || !pcaFile.exists()) {
                PythonRunner runner = new PythonRunner(
                        "py",
                        "src/embedder.py",
                        "."
                );

                runner.run();
            }

            JsonLoader loader = new JsonLoader();

            holder = loader.load(
                    "full_vectors.json",
                    "pca_vectors.json"
            );

            wordService = new WordService(holder, new Euclidean());

        } catch (Exception e) {
            showError("Failed to load data: " + e.getMessage());
            return;
        }

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("PCA X");
        yAxis.setLabel("PCA Y");

        scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setLegendVisible(false);

        chartManager = new ChartManager(scatterChart, projectionService);
        commandInvoker = new CommandInvoker();

        xAxisBox = new ComboBox<>();
        yAxisBox = new ComboBox<>();
        zAxisBox = new ComboBox<>();

        int pcaSize = holder.getWords().get(0).getPcaVector().size();

        for (int i = 0; i < pcaSize; i++) {
            xAxisBox.getItems().add(i);
            yAxisBox.getItems().add(i);
            zAxisBox.getItems().add(i);
        }

        xAxisBox.getSelectionModel().select(0);
        yAxisBox.getSelectionModel().select(1);
        zAxisBox.getSelectionModel().select(Math.min(2, pcaSize - 1));

        Button updateAxesButton = new Button("Update Axes");
        updateAxesButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Update Axes",
                                this::refreshChart,
                                this::restoreCleanChart
                        )
                )
        );

        Button open3DButton = new Button("Open 3D View");
        open3DButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Open 3D View",
                                this::open3DView,
                                () -> resultLabel.setText("Undo for 3D window is not supported.")
                        )
                )
        );

        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> undoCommand());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e -> redoCommand());

        searchField = new TextField();
        searchField.setPromptText("Search word");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Search Word",
                                this::searchWord,
                                this::restoreCleanChart
                        )
                )
        );

        word1Field = new TextField();
        word1Field.setPromptText("Word 1");

        word2Field = new TextField();
        word2Field.setPromptText("Word 2");

        word3Field = new TextField();
        word3Field.setPromptText("Word 3");

        kField = new TextField("5");
        kField.setPrefWidth(50);

        centroidField = new TextField();
        centroidField.setPromptText("Centroid words: king,queen,man");

        Button distanceButton = new Button("Distance");
        distanceButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Calculate Distance",
                                this::calculateDistance,
                                () -> resultLabel.setText("Undo: distance result cleared.")
                        )
                )
        );

        Button projectionButton = new Button("Custom Projection");
        projectionButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Custom Projection",
                                this::customProjection,
                                this::restoreCleanChart
                        )
                )
        );

        Button analogyButton = new Button("Vector Arithmetic");
        analogyButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Vector Arithmetic",
                                this::vectorArithmetic,
                                this::restoreCleanChart
                        )
                )
        );

        Button centroidButton = new Button("Centroid Nearest");
        centroidButton.setOnAction(e ->
                commandInvoker.executeCommand(
                        new FunctionalCommand(
                                "Centroid Nearest",
                                this::centroidNearest,
                                this::restoreCleanChart
                        )
                )
        );

        resultLabel = new Label("Ready");

        HBox row1 = new HBox(8);
        row1.getChildren().addAll(
                new Label("X:"), xAxisBox,
                new Label("Y:"), yAxisBox,
                new Label("Z:"), zAxisBox,
                updateAxesButton,
                open3DButton,
                undoButton,
                redoButton,
                new Label("K:"), kField
        );

        HBox row2 = new HBox(8);
        row2.getChildren().addAll(
                searchField,
                searchButton,
                word1Field,
                word2Field,
                word3Field
        );

        HBox row3 = new HBox(8);
        row3.getChildren().addAll(
                distanceButton,
                projectionButton,
                analogyButton
        );

        HBox row4 = new HBox(8);
        row4.getChildren().addAll(
                centroidField,
                centroidButton
        );

        VBox top = new VBox(8);
        top.getChildren().addAll(row1, row2, row3, row4, resultLabel);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(scatterChart);

        refreshChart();

        Scene scene = new Scene(root, 1200, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void refreshChart() {
        int xIndex = xAxisBox.getSelectionModel().getSelectedItem();
        int yIndex = yAxisBox.getSelectionModel().getSelectedItem();

        chartManager.refreshChart(
                holder,
                xIndex,
                yIndex,
                NORMAL_POINT_STYLE,
                this::showNearestWords
        );

        resultLabel.setText("Showing PCA " + xIndex + " vs PCA " + yIndex);
    }

    private void searchWord() {
        String text = searchField.getText().trim();

        if (text.isEmpty()) {
            resultLabel.setText("Enter a word to search.");
            return;
        }

        Word word = holder.getWord(text);

        if (word == null) {
            resultLabel.setText("Word not found: " + text);
            return;
        }

        int xIndex = xAxisBox.getSelectionModel().getSelectedItem();
        int yIndex = yAxisBox.getSelectionModel().getSelectedItem();

        Point2D point = projectionService.projectWord(word, xIndex, yIndex);

        chartManager.centerOnPoint(point, 2);

        resultLabel.setText("Centered on: " + word.getWord());
    }

    private void calculateDistance() {
        String firstText = word1Field.getText().trim();
        String secondText = word2Field.getText().trim();

        try {
            double euclidean = wordService.calculateDistance(
                    firstText,
                    secondText,
                    new Euclidean()
            );

            double cosine = wordService.calculateDistance(
                    firstText,
                    secondText,
                    new Cosine()
            );

            resultLabel.setText(
                    "Euclidean: " + String.format("%.4f", euclidean)
                            + " | Cosine distance: "
                            + String.format("%.4f", cosine)
                            + " | Undo stack: "
                            + commandInvoker.getHistorySize()
                            + " | Redo stack: "
                            + commandInvoker.getRedoSize()
            );

        } catch (Exception e) {
            resultLabel.setText("Distance error: " + e.getMessage());
        }
    }

    private void showNearestWords(Word word) {
        int k = getK();

        if (k <= 0) {
            return;
        }

        try {
            List<Word> words = wordService.findNearestToWord(word.getWord(), k);

            refreshChart();

            List<Word> selectedWord = new ArrayList<>();
            selectedWord.add(word);

            addHighlightedWordsToChart(
                    selectedWord,
                    "Selected word",
                    SELECTED_POINT_STYLE
            );

            addHighlightedWordsToChart(
                    words,
                    "Nearest words",
                    NEAREST_POINT_STYLE
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Nearest to ").append(word.getWord()).append(": ");

            for (Word currentWord : words) {
                sb.append(currentWord.getWord()).append(" ");
            }

            resultLabel.setText(sb.toString());

        } catch (Exception e) {
            resultLabel.setText("K nearest error: " + e.getMessage());
        }
    }

    private void customProjection() {
        Word start = holder.getWord(word1Field.getText().trim());
        Word end = holder.getWord(word2Field.getText().trim());

        if (start == null || end == null) {
            resultLabel.setText("Choose two valid words for projection axis.");
            return;
        }

        try {
            chartManager.clearChart();

            NumberAxis xAxis = (NumberAxis) scatterChart.getXAxis();
            NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();

            xAxis.setLabel("Projection on axis: " + start.getWord() + " -> " + end.getWord());
            yAxis.setLabel("Visual separation");

            xAxis.setAutoRanging(true);
            yAxis.setAutoRanging(true);

            XYChart.Series<Number, Number> projectionSeries = new XYChart.Series<>();
            projectionSeries.setName("Custom Projection");

            int index = 0;

            for (Word word : holder.getWords()) {
                double projectionValue = projectionService.projectX(word, start, end);

                double visualY = (index % 11 - 5) * 0.03;

                XYChart.Data<Number, Number> data =
                        new XYChart.Data<>(projectionValue, visualY);

                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(PROJECTION_POINT_STYLE);

                        Tooltip.install(
                                newNode,
                                new Tooltip(word.getWord() + " = " + String.format("%.3f", projectionValue))
                        );
                    }
                });

                projectionSeries.getData().add(data);
                index++;
            }

            scatterChart.getData().add(projectionSeries);

            resultLabel.setText(
                    "Custom projection axis: "
                            + start.getWord()
                            + " -> "
                            + end.getWord()
                            + ". Points are shown by their projection value."
            );

        } catch (Exception e) {
            resultLabel.setText("Projection error: " + e.getMessage());
        }
    }

    private void vectorArithmetic() {
        int k = getK();

        if (k <= 0) {
            return;
        }

        String firstText = word1Field.getText().trim();
        String secondText = word2Field.getText().trim();
        String thirdText = word3Field.getText().trim();

        try {
            Word first = wordService.findWord(firstText);
            Word second = wordService.findWord(secondText);
            Word third = wordService.findWord(thirdText);

            List<Word> closestWords = wordService.findAnalogy(
                    firstText,
                    secondText,
                    thirdText,
                    k
            );

            refreshChart();

            List<Word> inputWords = new ArrayList<>();
            inputWords.add(first);
            inputWords.add(second);
            inputWords.add(third);

            addHighlightedWordsToChart(
                    inputWords,
                    "Input words",
                    INPUT_WORD_STYLE
            );

            if (!closestWords.isEmpty()) {
                List<Word> bestResult = new ArrayList<>();
                bestResult.add(closestWords.get(0));

                addHighlightedWordsToChart(
                        bestResult,
                        "Closest result",
                        RESULT_WORD_STYLE
                );
            }

            StringBuilder sb = new StringBuilder();

            sb.append(thirdText)
                    .append(" + ")
                    .append(secondText)
                    .append(" - ")
                    .append(firstText)
                    .append(" => ");

            for (Word word : closestWords) {
                sb.append(word.getWord()).append(" ");
            }

            resultLabel.setText(sb.toString());

        } catch (Exception e) {
            resultLabel.setText("Vector arithmetic error: " + e.getMessage());
        }
    }

    private void centroidNearest() {
        String text = centroidField.getText().trim();

        if (text.isEmpty()) {
            resultLabel.setText("Enter words for centroid.");
            return;
        }

        String[] parts = text.split(",");
        List<Word> selectedWords = new ArrayList<>();

        for (String part : parts) {
            String wordText = part.trim();

            if (wordText.isEmpty()) {
                continue;
            }

            Word word = holder.getWord(wordText);

            if (word == null) {
                resultLabel.setText("Word not found: " + wordText);
                return;
            }

            selectedWords.add(word);
        }

        if (selectedWords.isEmpty()) {
            resultLabel.setText("No valid words entered.");
            return;
        }

        int k = getK();

        if (k <= 0) {
            return;
        }

        try {
            Vector centroid = projectionService.calculateCentroid(selectedWords);

            List<Word> closestWords = wordService.findNearestToVector(centroid, k);

            refreshChart();

            addHighlightedWordsToChart(
                    selectedWords,
                    "Centroid input words",
                    INPUT_WORD_STYLE
            );

            addHighlightedWordsToChart(
                    closestWords,
                    "Centroid nearest words",
                    NEAREST_POINT_STYLE
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Centroid nearest: ");

            for (Word word : closestWords) {
                sb.append(word.getWord()).append(" ");
            }

            resultLabel.setText(sb.toString());

        } catch (Exception e) {
            resultLabel.setText("Centroid error: " + e.getMessage());
        }
    }

    private void open3DView() {
        try {
            int xIndex = xAxisBox.getSelectionModel().getSelectedItem();
            int yIndex = yAxisBox.getSelectionModel().getSelectedItem();
            int zIndex = zAxisBox.getSelectionModel().getSelectedItem();

            Three_D viewin3D = new Three_D();
            viewin3D.show(holder, xIndex, yIndex, zIndex);

            resultLabel.setText("Opened 3D view: PCA "
                    + xIndex + ", " + yIndex + ", " + zIndex);

        } catch (Exception e) {
            resultLabel.setText("3D view error: " + e.getMessage());
        }
    }

    private int getK() {
        try {
            int k = Integer.parseInt(kField.getText().trim());

            if (k <= 0) {
                resultLabel.setText("K must be positive.");
                return -1;
            }

            return k;

        } catch (NumberFormatException e) {
            resultLabel.setText("K must be a number.");
            return -1;
        }
    }

    private void addHighlightedWordsToChart(List<Word> words, String title, String style) {
        int xIndex = xAxisBox.getSelectionModel().getSelectedItem();
        int yIndex = yAxisBox.getSelectionModel().getSelectedItem();

        chartManager.addHighlightedWordsToChart(
                words,
                title,
                style,
                xIndex,
                yIndex
        );
    }

    private void restoreCleanChart() {
        refreshChart();
        resultLabel.setText("Undo: returned to clean PCA view.");
    }

    private void undoCommand() {
        String commandName = commandInvoker.undo();

        if (commandName == null) {
            resultLabel.setText("Nothing to undo.");
            return;
        }

        resultLabel.setText(
                "Undo: " + commandName
                        + " | Undo stack: "
                        + commandInvoker.getHistorySize()
                        + " | Redo stack: "
                        + commandInvoker.getRedoSize()
        );
    }

    private void redoCommand() {
        String commandName = commandInvoker.redo();

        if (commandName == null) {
            resultLabel.setText("Nothing to redo.");
            return;
        }

        resultLabel.setText(
                "Redo: " + commandName
                        + " | Undo stack: "
                        + commandInvoker.getHistorySize()
                        + " | Redo stack: "
                        + commandInvoker.getRedoSize()
        );
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Application Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}