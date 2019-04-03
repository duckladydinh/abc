package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Group;
import model.Team;
import org.apache.commons.lang3.Validate;
import solution.AbstractSolution;
import solution.GeometryNeighborSolution;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static utilities.DataTool.inputData;

public class App extends Application {
    @FXML
    private JFXTabPane tabPane;

    @FXML
    private StackPane tableContainer;

    @FXML
    private StackPane viewContainer;

    @FXML
    private JFXTextField pathInput;

    @FXML
    private JFXButton startButton;

    @FXML
    private JFXButton findButton;

    @FXML
    private TableView<Team> previewTable;

    @FXML
    private TableColumn<Team, String> teamNameColumn;

    @FXML
    private TableColumn<Team, String> teamXLocation;

    @FXML
    private TableColumn<Team, String> teamYLocation;

    private List<Team> teams = null;

    @FXML
    public void initialize() {
        findButton.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null && file.exists()) {
                pathInput.setText(file.getAbsolutePath());
            }
        });

        teamNameColumn.prefWidthProperty().bind(previewTable.widthProperty().divide(3));
        teamNameColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getName()));

        teamXLocation.prefWidthProperty().bind(previewTable.widthProperty().divide(3));
        teamXLocation.setCellValueFactory(
                cell -> new SimpleStringProperty("" + cell.getValue().getLocation().getX()));

        teamYLocation.prefWidthProperty().bind(previewTable.widthProperty().divide(3));
        teamYLocation.setCellValueFactory(
                cell -> new SimpleStringProperty("" + cell.getValue().getLocation().getY()));

        pathInput.textProperty().addListener ((observable, oldPath, newPath) -> {
            startButton.setDisable(true);
            if (newPath != null && !newPath.equals(oldPath)) {
                File file = new File(newPath);
                if (file.isFile()) {
                    try {
                        tabPane.getSelectionModel().select(0);
                        teams = inputData(file.getAbsolutePath());
                        previewTable.setItems(FXCollections.observableArrayList(teams));
                        findButton.setStyle("-fx-background-color: -fx-primary; -fx-text-fill: -fx-secondary");
                        startButton.setDisable(false);
                    } catch (Exception e) {
                        findButton.setStyle("-fx-background-color: red; -fx-text-fill: white");
                        previewTable.getItems().clear();
                    }
                }
            }
        });

        startButton.setOnMouseClicked(event -> {
            if (startButton.getText().equals("Start")) {
                startButton.setText("New?");
                findButton.setDisable(true);
                tabPane.getSelectionModel().select(1);
                tabPane.getTabs().get(1).setDisable(false);

                Validate.notNull(teams);
                AbstractSolution solution = new GeometryNeighborSolution();
                List<Group> groups = solution.solve(teams);

                ScatterChart<Number, Number> scatterChartView = createScatterChart(groups);
                viewContainer.getChildren().add(scatterChartView);

                tableContainer.getChildren().add(createTreeTableView(groups));
            }
            else {
                tabPane.getSelectionModel().select(0);
                tabPane.getTabs().get(1).setDisable(true);
                startButton.setText("Start");
                startButton.setDisable(true);
                viewContainer.getChildren().clear();
                previewTable.getItems().clear();
                findButton.setDisable(false);
                pathInput.clear();

                tableContainer.getChildren().clear();
                tableContainer.getChildren().add(previewTable);
            }
        });
    }

    TreeTableView<Team> createTreeTableView(List<Group> groups) {
        TreeItem<Team> root = new TreeItem(new Team("All (" + groups.size() + ")", null));
        root.setExpanded(true);

        for (int i = 0; i < groups.size(); ++i) {
            Group group = groups.get(i);
            TreeItem groupItem =  new TreeItem(new Team("GROUP " + (i+1) + " (" + group.getSize() + ")", null));
            TreeItem blankItem =  new TreeItem(new Team("", null));
            groupItem.setExpanded(true);

            for (Team team: group.getTeams()) {
                TreeItem teamItem = new TreeItem(team);
                groupItem.getChildren().add(teamItem);
            }

            root.getChildren().addAll(blankItem, groupItem);
        }

        TreeTableView<Team> table = new TreeTableView<>(root);

        TreeTableColumn<Team, String> nameCol = new TreeTableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getValue().getName()));
        nameCol.prefWidthProperty().bind(table.widthProperty().divide(3));

        TreeTableColumn<Team, String> xCol = new TreeTableColumn<>("X");
        xCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getValue().getLocation() == null ? "" : ("" + cell.getValue().getValue().getLocation().getX())));
        xCol.prefWidthProperty().bind(table.widthProperty().divide(3));

        TreeTableColumn<Team, String> yCol = new TreeTableColumn<>("Y");
        yCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getValue().getLocation() == null ? "" : ("" + cell.getValue().getValue().getLocation().getY())));
        yCol.prefWidthProperty().bind(table.widthProperty().divide(3));

        table.getColumns().addAll(nameCol, xCol, yCol);
        return table;
    }

    private ScatterChart<Number, Number> createScatterChart(List<Group> groups) {
        int xLowerBound = (int) 1e9, xUpperBound = (int) -1e9;
        int yLowerBound = (int) 1e9, yUpperBound = (int) -1e9;

        for (Group group: groups) {
            for (Team team: group.getTeams()) {
                xLowerBound = Math.min(xLowerBound, team.getLocation().getX());
                xUpperBound = Math.max(xUpperBound, team.getLocation().getX());
                yLowerBound = Math.min(yLowerBound, team.getLocation().getY());
                yUpperBound = Math.max(yUpperBound, team.getLocation().getY());
            }
        }

        xLowerBound -= 50;
        yLowerBound -= 50;
        xUpperBound += 50;
        yLowerBound += 50;
        int xTickDelta = (xUpperBound - xLowerBound) / 5;
        int yTickDelta = (yUpperBound - yLowerBound) / 5;

        NumberAxis xAxis = new NumberAxis(xLowerBound, xUpperBound, xTickDelta);
        NumberAxis yAxis = new NumberAxis(yLowerBound, yUpperBound, yTickDelta);
        ScatterChart<Number, Number> chartView = new ScatterChart<>(xAxis, yAxis);

        for (int i = 0; i < groups.size(); ++i) {
            Group group = groups.get(i);
            List<XYChart.Data<Number, Number>> items = group.getTeams().stream()
                    .map(team -> new XYChart.Data<Number, Number>(
                            team.getLocation().getX(),
                            team.getLocation().getY())
                    )
                    .collect(Collectors.toList());
            XYChart.Series<Number, Number> series = new XYChart.Series<>();

            series.setName("Group " + (i + 1));
            series.getData().addAll(items);
            chartView.getData().add(series);
        }

        return chartView;
    }


    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/App.fxml"));
            Scene scene = new Scene(root, 1000, 1000);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
