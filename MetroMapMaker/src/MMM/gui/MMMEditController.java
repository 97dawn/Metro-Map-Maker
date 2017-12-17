package MMM.gui;

import MMM.data.DraggableEllipse;
import MMM.data.DraggableImage;
import MMM.data.DraggableText;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import MMM.data.MMMData;
import MMM.data.MMMPath;
import MMM.data.MMMState;
import MMM.data.MetroLine;
import MMM.tran.AddElementTran;
import MMM.tran.AddLineTran;
import MMM.tran.AddNewStationTran;
import MMM.tran.ChangeBackgroundColorTran;
import MMM.tran.ChangeFillColorTran;
import MMM.tran.ChangeStationCircleRadiusTran;
import MMM.tran.ChangeTextBoldTran;
import MMM.tran.ChangeTextColorTran;
import MMM.tran.ChangeTextFontTran;
import MMM.tran.ChangeTextItalicTran;
import MMM.tran.ChangeTextSizeTran;
import MMM.tran.ChangeThicknessTran;
import MMM.tran.EditLineTran;
import MMM.tran.MoveElementTran;
import MMM.tran.MoveLineEndTran;
import MMM.tran.MoveStationLabelTran;
import MMM.tran.RemoveElementTran;
import MMM.tran.RemoveLineTran;
import MMM.tran.RemoveStationTran;
import MMM.tran.RotateStationLabelTran;
import MMM.tran.SetImageBackgroundTran;
import djf.AppTemplate;
import djf.settings.AppStartupConstants;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppYesNoDialogSingleton;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import jtps.jTPS;

/**
 * This class responds to interactions with other UI logo editing controls.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class MMMEditController {

    AppTemplate app;
    MMMData dataManager;
    String selectedLine;
    String selectedStation;

    public MMMEditController(AppTemplate initApp) {
        app = initApp;
        dataManager = (MMMData) app.getDataComponent();
    }

    public void handleEditLine() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        MetroLine line = null;
        // FIND THE LINE
        for (MetroLine ml : data.getLines()) {
            if (ml.getId().equals(workspace.getMetroLines().getValue().toString())) {
                line = ml;
            }
        }
        if (line != null) {
            Button ok = new Button("OK");
            Button close = new Button("CLOSE");
            Stage dialogStage = new Stage();
            Label promptLineName = new Label("Line name: ");
            TextField typeLineName = new TextField(workspace.getMetroLines().getValue().toString());
            HBox lineName = new HBox(promptLineName, typeLineName);
            lineName.setPadding(new Insets(20));
            Label promptLineColor = new Label("Line color: ");
            ColorPicker pickLineColor = new ColorPicker((Color) line.getLineSegments().get(0).getStroke());
            HBox lineColor = new HBox(promptLineColor, pickLineColor);
            lineColor.setPadding(new Insets(20));
            Label promptLineCircular = new Label("Line is circular: ");
            CheckBox isLineCircular = new CheckBox();
            isLineCircular.setSelected(line.getIsCircular());
            HBox lineCircular = new HBox(promptLineCircular, isLineCircular);
            lineCircular.setPadding(new Insets(20));
            HBox buttons = new HBox(ok, close);
            buttons.setSpacing(20);
            buttons.setAlignment(Pos.CENTER);
            VBox pane = new VBox(lineName, lineColor, lineCircular, buttons);
            pane.setPadding(new Insets(50));
            Scene dialogScene = new Scene(pane);
            dialogStage.setScene(dialogScene);
            dialogStage.setTitle("Edit Line");
            dialogStage.show();

            // ADD LISTENER TO OK BUTTON
            ok.setOnAction(e -> {
                MetroLine selectedLine = null;
                // FIND THE LINE
                for (MetroLine ml : data.getLines()) {
                    if (ml.getId().equals(workspace.getMetroLines().getValue().toString())) {
                        selectedLine = ml;
                    }
                }

                EditLineTran transaction = new EditLineTran(workspace, data, selectedLine, typeLineName.getText(), pickLineColor.getValue(), isLineCircular.isSelected());
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(data);
                dialogStage.close();
            });
            // ADD LISTENER TO CLOSE BUTTON
            close.setOnAction(e -> {
                dialogStage.close();
            });
        }

    }

    public void handleAddLine() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        //  CONSTRUCT A DIALOG
        Label promptLineName = new Label("Line name: ");
        TextField typeLineName = new TextField();
        HBox lineName = new HBox(promptLineName, typeLineName);
        lineName.setPadding(new Insets(20));
        Label promptLineColor = new Label("Line color: ");
        ColorPicker pickLineColor = new ColorPicker();
        HBox lineColor = new HBox(promptLineColor, pickLineColor);
        lineColor.setPadding(new Insets(20));
        Label promptLineCircular = new Label("Line is circular: ");
        CheckBox isLineCircular = new CheckBox();
        HBox lineCircular = new HBox(promptLineCircular, isLineCircular);
        lineCircular.setPadding(new Insets(20));
        Button ok = new Button("OK");
        Button close = new Button("CLOSE");
        HBox buttons = new HBox(ok, close);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);
        VBox pane = new VBox(lineName, lineColor, lineCircular, buttons);
        pane.setPadding(new Insets(50));
        Scene dialogScene = new Scene(pane);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Line");
        dialogStage.setScene(dialogScene);

        // WHEN THE USER PRESS OK BUTTON TO ADD A LINE
        ok.setOnAction(eh -> {
            if (!typeLineName.getText().equals("") && pickLineColor.getValue() != null) {

                // CONSTRUCT A METRO LINE
                MetroLine line = new MetroLine(typeLineName.getText());
                // CONSTRUCT A REAL LINE SEGMENT
                Line lineSeg = new Line(100, 100, 200, 100);
                line.setIsCircular(isLineCircular.isSelected());
                lineSeg.setId(typeLineName.getText());
                lineSeg.setOpacity(1.0);
                lineSeg.setStroke((Color) pickLineColor.getValue());
                lineSeg.setStrokeWidth(workspace.getLineThickness().getValue());
                // ADD LEFT END LABEL
                DraggableText leftText = new DraggableText(line.getId());
                leftText.setFill((Color) line.getStroke());
                leftText.xProperty().bind(lineSeg.startXProperty().subtract(leftText.getLayoutBounds().getWidth()));
                leftText.yProperty().bind(lineSeg.startYProperty());
                leftText.setTextAlignment(TextAlignment.RIGHT);
                // ADD RIGHT END LABEL
                DraggableText rightText = new DraggableText(line.getId());
                rightText.xProperty().bind(lineSeg.endXProperty());
                rightText.yProperty().bind(lineSeg.endYProperty());
                rightText.setFill((Color) line.getStroke());
                rightText.setTextAlignment(TextAlignment.LEFT);
                AddLineTran transaction = new AddLineTran(workspace, (MMMData) app.getDataComponent(), line, lineSeg, leftText, rightText);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                dialogStage.close();
                workspace.reloadWorkspace(dataManager);
            } else {
                AppMessageDialogSingleton message = AppMessageDialogSingleton.getSingleton();
                message.show("Incompletion", "You haven't incompleted setting line properties");
            }
        });
        close.setOnAction(eh -> {
            dialogStage.close();
        });
        dialogStage.show();
    }

    public void handleRemoveLine() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        String lineName = workspace.getMetroLines().getValue().toString();
        AppYesNoDialogSingleton dialog = AppYesNoDialogSingleton.getSingleton();
        dialog.show("Remove Metro Line", "Do you really want to remove this line?");
        String selection = dialog.getSelection();
        if (selection.equals(AppYesNoDialogSingleton.YES)) {
            dialog.close();
            // FIND LINE SEGMENTS
            ArrayList<Line> lineSegs = new ArrayList<>();
            for (Node node : dataManager.getNodes()) {
                // FIND LINE SEGMENT
                if (node instanceof Line && !(node instanceof MetroLine) && ((Line) node).getId().equals(lineName)) {
                    lineSegs.add((Line) node);
                }
            }

            // LOAD ANOTHER LINE NAME TO COMBOBOX
            if (workspace.getMetroLines().getItems().size() > 0) {
                workspace.getMetroLines().setValue(workspace.getMetroLines().getItems().get(0));
            }
            // FIND LINE LABELS
            DraggableText leftLabel = null;
            DraggableText rightLabel = null;
            for (Node node : dataManager.getNodes()) {
                if (node instanceof DraggableText && ((DraggableText) node).getText().equals(lineSegs.get(0).getId())) {
                    if (((DraggableText) node).getTextAlignment().equals(TextAlignment.RIGHT)) {
                        leftLabel = ((DraggableText) node);
                    } else {
                        rightLabel = ((DraggableText) node);
                    }
                }
            }
            MetroLine metroLine = null;
            // FIND METRO LINE
            for (MetroLine line : dataManager.getLines()) {
                if (line.getId().equals(lineSegs.get(0).getId())) {
                    metroLine = line;
                    break;
                }
            }
            // REMOVE TRANSACTION
            RemoveLineTran transaction = new RemoveLineTran((MMMData) app.getDataComponent(), lineSegs, leftLabel, rightLabel, metroLine);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } else {
            dialog.close();
        }
    }

    public void handleAddStationToLine() {
        ((MMMData) app.getDataComponent()).setState(MMMState.ADDING_STATION);
        Scene scene = app.getGUI().getPrimaryScene();
        // CHANGE THE CURSOR
        scene.setCursor(Cursor.HAND);
    }

    public void handleRemoveStationFromLine() {
        ((MMMData) app.getDataComponent()).setState(MMMState.REMOVING_STATION);
        Scene scene = app.getGUI().getPrimaryScene();
        // CHANGE THE CURSOR
        scene.setCursor(Cursor.HAND);
    }

    public void handleShowList() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        String lineName = workspace.getMetroLines().getSelectionModel().getSelectedItem().toString();
        //CONSTRUCT DIALOG
        Label title = new Label();
        title.setText(lineName + " Stops");
        title.setFont(Font.font(20));
        VBox list = new VBox();
        list.setAlignment(Pos.TOP_LEFT);
        Button ok = new Button("OK");
        VBox dialogPane = new VBox();
        dialogPane.getChildren().addAll(title, list, ok);
        Scene dialogScene = new Scene(dialogPane);
        Stage dialogStage = new Stage();
        dialogPane.setAlignment(Pos.CENTER);
        dialogPane.setPadding(new Insets(70));
        dialogPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        dialogPane.setSpacing(70);
        dialogStage.setTitle("Metro Map Maker - Metro Line Stops");
        dialogStage.setScene(dialogScene);
        ArrayList<DraggableEllipse> stations = null;
        for (MetroLine line : dataManager.getLines()) {
            if (line.getId().equals(((MMMWorkspace) app.getWorkspaceComponent()).getMetroLines().getSelectionModel().getSelectedItem())) {
                stations = line.getStations();
                break;
            }
        }
        for (DraggableEllipse station : stations) {
            Label label = new Label("● " + station.getId());
            label.setFont(Font.font(15));
            list.getChildren().add(label);
        }
        ok.setOnAction(e -> {
            dialogStage.close();
        });
        dialogStage.show();
    }

    public void handleChangeLineThickness() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        double newThickness = workspace.getLineThickness().getValue();
        MMMData data = (MMMData) app.getDataComponent();
        String selectedLineName = workspace.getMetroLines().getValue().toString();
        MetroLine selectedLine = null;
        for (MetroLine line : data.getLines()) {
            if (line.getId().equals(selectedLineName)) {
                selectedLine = line;
                break;
            }
        }
        if ((selectedLine != null) && selectedLine instanceof MetroLine) {
            ChangeThicknessTran transaction = new ChangeThicknessTran(selectedLine, newThickness, workspace.getLineThickness());
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        }
    }

    public void handleMetroStationColorPicker() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Color selectedColor = workspace.getMetroStationColorPicker().getValue();
        String selectedStationName = workspace.getMetroStations().getValue().toString();
        DraggableEllipse station = null;
        for (Node node : data.getNodes()) {
            if (node instanceof DraggableEllipse && node.getId().equals(selectedStationName)) {
                station = (DraggableEllipse) node;
                break;
            }
        }
        if (selectedColor != null) {
            if (station != null) {
                ChangeFillColorTran transaction = new ChangeFillColorTran(station, selectedColor);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleAddStation() {
        // DraggableEllipse station = new DraggableEllipse();
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        // CONSTRUCT THE DIALOG
        Label promptStationName = new Label("Station name: ");
        TextField typeStationName = new TextField();
        HBox lineName = new HBox(promptStationName, typeStationName);
        lineName.setPadding(new Insets(20));
        Button ok = new Button("OK");
        Button close = new Button("CLOSE");
        HBox buttons = new HBox(ok, close);
        buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        VBox pane = new VBox(lineName, buttons);
        pane.setPadding(new Insets(50));
        Scene dialogScene = new Scene(pane);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Station");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
        // ADD LISTENER TO OK BUTTON
        ok.setOnAction(e -> {
            if (!typeStationName.getText().equals("")) {
                // ADD NEW LINE
                DraggableEllipse station = new DraggableEllipse(typeStationName.getText());
                DraggableText stationLabel = new DraggableText(station.getId());
                stationLabel.xProperty().bind(station.centerXProperty().add(station.radiusXProperty()));
                stationLabel.yProperty().bind(station.centerYProperty().subtract(station.radiusYProperty()));
                AddNewStationTran transaction = new AddNewStationTran(workspace, data, station, stationLabel);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                dialogStage.close();
                data.setState(MMMState.SELECTING_STATION);
                workspace.reloadWorkspace(dataManager);
            } else {
                // ERROR
                AppMessageDialogSingleton error = AppMessageDialogSingleton.getSingleton();
                error.show("Incompletion", "You haven't incompleted setting a station property");
            }
        });
        // ADD LISTENER TO CLOSE BUTTON
        close.setOnAction(e -> {
            dialogStage.close();
        });
    }

    public void handleRemoveStation() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        String stationName = workspace.getMetroStations().getValue().toString();
        AppYesNoDialogSingleton dialog = AppYesNoDialogSingleton.getSingleton();
        dialog.show("Remove Metro Station", "Do you really want to remove this station?");
        String selection = dialog.getSelection();
        if (selection.equals(AppYesNoDialogSingleton.YES)) {
            dialog.close();
            DraggableText stationLabelToRemove = null;
            DraggableEllipse stationToRemove = null;
            for (Node n : data.getNodes()) {
                // FIND THE STATION LABEL
                if (n instanceof DraggableText && ((DraggableText) n).getText().equals(stationName)) {
                    stationLabelToRemove = (DraggableText) n;
                } // FIND THE CORRESPONDING STATION
                else if (n instanceof DraggableEllipse && n.getId().equals(stationName)) {
                    stationToRemove = (DraggableEllipse) n;
                }
            }
            MMMCanvasController c = new MMMCanvasController(app);
            RemoveStationTran transaction = new RemoveStationTran(c, app, stationToRemove, stationLabelToRemove);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } else {
            dialog.close();
        }
    }

    public void handleSnap() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        if (node instanceof DraggableEllipse) {
            DraggableEllipse station = (DraggableEllipse) node;
            double centerX = station.getCenterX();
            double centerY = station.getCenterY();
            double newCenterX = centerX;
            double newCenterY = centerY;
            if (centerX % 10 >= 5) {
                double diff = 10 - centerX % 10; // 5,4,3,2,1
                newCenterX = centerX + diff;
            } else if (centerX % 10 < 5 && centerX % 10 != 0) {
                newCenterX = centerX - centerX % 10;
            }
            if (centerY % 10 >= 5) {
                double diff = 10 - centerY % 10; // 5,4,3,2,1
                newCenterY = centerY + diff;
            } else if (centerY % 10 < 5 && centerY % 10 != 0) {
                newCenterY = centerY - centerY % 10;
            }
            station.setCenterX(newCenterX);
            station.setCenterY(newCenterY);
            if (centerX % 10 != 0 || centerY % 10 != 0) {
                MoveElementTran transaction = new MoveElementTran(station, centerX, centerY);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        } else if (data.getSelectedNode() instanceof DraggableText && workspace.getMetroLines().getItems().contains(((DraggableText) data.getSelectedNode()).getText())) {
            DraggableText lineEnd = (DraggableText) node;
            double oldX = lineEnd.getX();
            double oldY = lineEnd.getY();
            Line line = null;
            for (MetroLine ml : dataManager.getLines()) {
                if (ml.getId().equals(lineEnd.getText())) {
                    if (lineEnd.getTextAlignment().equals(TextAlignment.RIGHT)) {
                        line = ml.getLineSegments().get(0);
                    } else {
                        line = ml.getLineSegments().get(ml.getLineSegments().size() - 1);
                    }
                    break;
                }
            }
            if (oldX % 10 >= 5) {
                double diff = 10 - oldX % 10; // 5,4,3,2,1
                if (lineEnd.getTextAlignment().equals(TextAlignment.RIGHT)) {
                    oldX = line.getStartX();
                    line.setStartX(line.getStartX() + diff);
                } else {
                    oldX = line.getEndX();
                    line.setEndX(line.getEndX() + diff);
                }
            } else if (oldX % 10 < 5 && oldX % 10 != 0) {
                if (lineEnd.getTextAlignment().equals(TextAlignment.RIGHT)) {
                    oldX = line.getStartX();
                    line.setStartX(line.getStartX() - oldX % 10);
                } else {
                    oldX = line.getEndX();
                    line.setEndX(line.getEndX() - oldX % 10);
                }
            }
            if (oldY % 10 >= 5) {
                double diff = 10 - oldY % 10; // 5,4,3,2,1
                if (lineEnd.getTextAlignment().equals(TextAlignment.RIGHT)) {
                    oldY = line.getStartY();
                    line.setStartY(line.getStartY() + diff);
                } else {
                    oldY = line.getEndY();
                    line.setEndY(line.getEndY() + diff);
                }
            } else if (oldY % 10 < 5 && oldY % 10 != 0) {
                if (lineEnd.getTextAlignment().equals(TextAlignment.RIGHT)) {
                    oldY = line.getStartY();
                    line.setStartY(line.getStartY() - oldY % 10);
                } else {
                    oldY = line.getEndY();
                    line.setEndY(line.getEndY() - oldY % 10);
                }
            }
            if (oldX % 10 != 0 || oldY % 10 != 0) {
                MoveLineEndTran transaction = new MoveLineEndTran(lineEnd, line, oldX, oldY);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleMoveLabel() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        String selectedStationName = workspace.getMetroStations().getValue().toString();
        DraggableEllipse station = null;
        DraggableText label = null;
        for (Node node : data.getNodes()) {
            if (node instanceof DraggableEllipse && node.getId().equals(selectedStationName)) {
                station = (DraggableEllipse) node;
            } else if (node instanceof DraggableText && ((DraggableText) node).getText().equals(selectedStationName)) {
                label = (DraggableText) node;
            }
        }
        if (station != null && label != null) {
            MoveStationLabelTran transaction = new MoveStationLabelTran(station, label);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        }
    }

    public void handleRotateLabel() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        String selectedStationName = workspace.getMetroStations().getValue().toString();
        DraggableText label = null;
        for (Node node : data.getNodes()) {
            if (node instanceof DraggableText && ((DraggableText) node).getText().equals(selectedStationName)) {
                label = (DraggableText) node;
                break;
            }
        }
        if (label != null) {
            RotateStationLabelTran transaction = new RotateStationLabelTran(label);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        }
    }

    public void handleChangeRadius() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        String selectedStationName = workspace.getMetroStations().getValue().toString();
        DraggableEllipse station = null;
        for (Node node : data.getNodes()) {
            if (node instanceof DraggableEllipse && node.getId().equals(selectedStationName)) {
                station = (DraggableEllipse) node;
                break;
            }
        }
        if (station != null) {
            ChangeStationCircleRadiusTran transaction = new ChangeStationCircleRadiusTran(station, workspace.getRadius().getValue(), workspace.getRadius());
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        }
    }

    public void handleFindShortestPath() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        //CONSTRUCT DIALOG
        String departure = workspace.getDeparture().getSelectionModel().getSelectedItem().toString();
        String arrival = workspace.getArrival().getSelectionModel().getSelectedItem().toString();
        Label title = new Label();
        title.setFont(Font.font("Route from " + departure + " to " + arrival, FontWeight.BOLD, 15));
        // CHECK WHETHER THE STATION IS ON THE LINE
        boolean isDepartureOnLine = false;
        boolean isArrivalOnLine = false;
        for (MetroLine ml : data.getLines()) {
            if (ml.getStationNames().contains(departure) ) {
                isDepartureOnLine=true;
            } if (ml.getStationNames().contains(arrival)) {
                isArrivalOnLine=true;
            }
        }
        boolean done = false;
        if (isDepartureOnLine && isArrivalOnLine) {
            // CHECK WHETHER WE CAN REACH ARRIVAL FROM DEPARTURE
            ArrayList<ArrayList<String>> lineConnected = new ArrayList<ArrayList<String>>();
            // FIND THE JUNCTIONS
            for (Node node : data.getNodes()) {
                if (node instanceof DraggableEllipse) {
                    DraggableEllipse st = (DraggableEllipse) node;
                    ArrayList<String> lines = st.getLines();
                    if (lines.size() > 1) {
                        if (lineConnected.size() == 0) {
                            lineConnected.add(lines);
                        } else {
                            for (int i = 0; i < lineConnected.size(); i++) {
                                if (lineConnected.get(i) != null) {
                                    // REMOVE DUPLICATED JUNCTIONS
                                    int duplication = 0;
                                    for (String s : lineConnected.get(i)) {
                                        if (lines.contains(s)) {
                                            duplication++;
                                        }
                                    }
                                    if (duplication == lineConnected.get(i).size()) {
                                        lineConnected.remove(i);
                                    }
                                    lineConnected.add(lines);
                                }
                            }
                        }
                    }
                }
            }

            // GET LINES OF STATIONS
            ArrayList<String> linesOfDepartureAndArrival = new ArrayList<>();
            ArrayList<String> lines = null;
            for (Node node : data.getNodes()) {
                if (node instanceof DraggableEllipse && node.getId().equals(departure)) {
                    DraggableEllipse st = (DraggableEllipse) node;
                    lines = st.getLines();
                    for (String l : lines) {
                        if (!linesOfDepartureAndArrival.contains(l)) {
                            linesOfDepartureAndArrival.add(l);
                        }
                    }
                } else if (node instanceof DraggableEllipse && node.getId().equals(arrival)) {
                    DraggableEllipse st = (DraggableEllipse) node;
                    lines = st.getLines();
                    for (String l : lines) {
                        if (!linesOfDepartureAndArrival.contains(l)) {
                            linesOfDepartureAndArrival.add(l);
                        }
                    }
                }
            }
            
            if (linesOfDepartureAndArrival.size() == 1) {
                done = true;
            }
            if (!done) {
                for (ArrayList<String> line : lineConnected) {
                    int found = 0;
                    done = false;
                    for (String l : linesOfDepartureAndArrival) {
                        if (line.contains(l)) {
                            found++;
                        }
                    }
                    if (found == line.size()) {
                        done = true;
                        break;
                    }
                }
            }
        } 

        if (done) {
            MMMPath path = findMinimumTransferPath(departure, arrival);
            Label pathLabel = updatePathDescription(path, departure, arrival);
            Label titleLabel = new Label("Route from " + departure + " to " + arrival);
            titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
            pathLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            Button ok = new Button("Close");
            VBox dialogPane = new VBox();
            dialogPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            dialogPane.setPadding(new Insets(20));
            dialogPane.setSpacing(20);
            dialogPane.setAlignment(Pos.CENTER);
            dialogPane.getChildren().addAll(title, titleLabel, pathLabel, ok);
            Scene dialogScene = new Scene(dialogPane);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Metro Map Maker - Route");
            dialogStage.setScene(dialogScene);
            dialogStage.show();
            ok.setOnMouseReleased(e -> {
                dialogStage.close();
            });
        } else {
            AppMessageDialogSingleton m = AppMessageDialogSingleton.getSingleton();
            m.show("No Route", "There is no route from " + departure + " to " + arrival);
        }
    }

    private Label updatePathDescription(MMMPath path, String startStationName, String endStationName) {
        // ONLY DO THIS IF THERE ACTUALLY IS A PATH
        String text1 = "Origin: " + startStationName + "\nDestination: " + endStationName + "\n";
        String text2 = "";
        int totalStops = 0;
        Label label = new Label();
        if ((path != null)) {
            MetroLine l = path.getTripLines().get(path.getTripLines().size() - 1); // LINE OF ARRIVAL
            int i = 0;
            MetroLine prevLine = l;
            for (; i < path.getTripLines().size(); i++) {
                MetroLine line = path.getTripLines().get(i);
                text2 += ". Board " + line.getId() + " at " + path.getBoardingStations().get(i).getId() + "\n";
                if (i > 0) {
                    int stops = Math.abs(prevLine.getStationNames().indexOf(startStationName) - prevLine.getStationNames().indexOf(path.getBoardingStations().get(i).getId()));
                    totalStops += stops;
                    text1 += prevLine.getId() + " (" + stops + " stops)\n";
                }
                startStationName = path.getBoardingStations().get(i).getId();
                prevLine = line;
            }
            int stops = Math.abs(l.getStationNames().indexOf(startStationName) - l.getStationNames().indexOf(endStationName));
            totalStops += stops;
            text1 += l.getId() + " (" + stops + " stops)\n";
            text1 += "Total Stops: " + totalStops + "\nEstimated Time: " + totalStops * 3 + " minutes\n\n\n\n";
            text2 += ". Disembark " + l.getId() + " at " + endStationName + "\n";
            label.setText(text1 + text2);
            label.setFont(Font.font(15));
        }
        return label;
    }

    private MMMPath findMinimumTransferPath(String departure, String arrival) {
        MMMData data = (MMMData) app.getDataComponent();
        DraggableEllipse startStation = null;
        DraggableEllipse endStation = null;
        for (MetroLine l : data.getLines()) {
            for (DraggableEllipse n : l.getStations()) {
                if (n.getId().equals(departure)) {
                    startStation = n;
                }
                if (n.getId().equals(arrival)) {
                    endStation = n;
                }
            }
        }

        // THESE WILL BE PATHS THAT WE WILL BUILD TO TEST
        ArrayList<MMMPath> testPaths = new ArrayList<>();

        // START BY PUTTING ALL THE LINES IN THE START STATION
        // IN OUR linesToTest Array
        for (int i = 0; i < startStation.getLines().size(); i++) {
            MMMPath path = new MMMPath(startStation, endStation);
            testPaths.add(path);
            for (MetroLine ml : data.getLines()) {
                if (ml.getId().equals(startStation.getLines().get(i))) {
                    path.addBoarding(ml, startStation);
                    break;
                }
            }
        }
        // THIS WILL COUNT HOW MANY TRANSFERS
        int numTransfers = 0;
        boolean found = false;
        boolean morePathsPossible = true;
        ArrayList<MMMPath> completedPaths = new ArrayList<>();
        while (!found && morePathsPossible) {
            ArrayList<MMMPath> updatedPaths = new ArrayList<>();
            for (int i = 0; i < testPaths.size(); i++) {
                MMMPath testPath = testPaths.get(i);

                // FIRST CHECK TO SEE IF THE DESTINATION IS ALREADY ON THE PATH
                if (testPath.hasLineWithStation(arrival)) {
                    completedPaths.add(testPath);
                    found = true;
                    morePathsPossible = false;
                } else if (morePathsPossible) {
                    // GET ALL THE LINES CONNECTED TO THE LAST LINE ON THE TEST PATH
                    // THAT HAS NOT YET BEEN VISITED
                    MetroLine lastLine = testPath.getTripLines().get(testPath.getTripLines().size() - 1);
                    // COLLECT TRANSFER LINES OF LASTLINE
                    ArrayList<MetroLine> transferLines = new ArrayList<>();
                    for (Node node : data.getNodes()) {
                        if (node instanceof DraggableEllipse && ((DraggableEllipse) node).getLines().size() > 1 && ((DraggableEllipse) node).getLines().contains(lastLine.getId())) {
                            for (MetroLine ml : data.getLines()) {
                                if (((DraggableEllipse) node).getLines().contains(ml.getId()) && !ml.getId().equals(lastLine.getId()) && !transferLines.contains(ml)) {
                                    transferLines.add(ml);
                                }
                            }
                        }
                    }
                    for (int j = 0; j < transferLines.size(); j++) {
                        MetroLine testLine = transferLines.get(j);
                        if (!testPath.hasLine(testLine.getId())) {
                            MMMPath newPath = testPath.clone();
                            DraggableEllipse intersectingStation = lastLine.findIntersectingStation(testLine);
                            newPath.addBoarding(testLine, intersectingStation);
                            updatedPaths.add(newPath);
                        }
                        // DEAD ENDS DON'T MAKE IT TO THE NEXT ROUND
                    }
                }
            }
            if (updatedPaths.size() > 0) {
                testPaths = updatedPaths;
                numTransfers++;
            } else {
                morePathsPossible = false;
            }
        }
        // WAS A PATH FOUND?
        if (found) {
            MMMPath shortestPath = completedPaths.get(0);
            int shortestTime = shortestPath.calculateTimeOfTrip();
            for (int i = 1; i < completedPaths.size(); i++) {
                MMMPath testPath = completedPaths.get(i);
                int timeOfTrip = testPath.calculateTimeOfTrip();
                if (timeOfTrip < shortestTime) {
                    shortestPath = testPath;
                    shortestTime = timeOfTrip;
                }
            }
            // WE NOW KNOW THE SHORTEST PATH, COMPLETE ITS DATA FOR EASY USE
            return shortestPath;
        } // NO PATH FOUND
        else {
            return null;
        }
    }

    public void handleBackgroundColorPicker() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        Color selectedColor = workspace.getBackgroundColorPicker().getValue();
        if (selectedColor != null) {
            Pane p = workspace.getGrid();
            ChangeBackgroundColorTran transaction = new ChangeBackgroundColorTran(p, selectedColor, workspace.getBackgroundColorPicker());
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        }
    }

    public void handleSetImageBackground() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        // OPEN A DIALOG AND PROMPT THE USER AS TO WHETHER THEY WANT A DIALOG
        AppYesNoDialogSingleton ask = AppYesNoDialogSingleton.getSingleton();
        ask.show("Want a dialog", "Do you want a dialog for selecting a background image?");
        String selection = ask.getSelection();
        if (selection.equals(AppYesNoDialogSingleton.YES)) {
            ask.close();
            File file = promptForImage();
            Image imageToAdd = new Image("file:" + file.getAbsolutePath());
            if (imageToAdd != null) {
                ImageView imageViewToAdd = new ImageView(imageToAdd);
                SetImageBackgroundTran transaction = new SetImageBackgroundTran(imageViewToAdd, workspace.getGrid(), data);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        } else {
            ask.close();
        }
    }

    public void handleAddImage() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        // ASK THE USER TO SELECT AN IMAGE
        File file = promptForImage();
        Image imageToAdd = new Image("file:" + file.getAbsolutePath());

        if (imageToAdd != null) {
            DraggableImage imageViewToAdd = new DraggableImage();
            imageViewToAdd.setImage(imageToAdd);
            imageViewToAdd.start(100, 100);
            // MAKE AND ADD THE TRANSACTION
            AddElementTran transaction = new AddElementTran(imageViewToAdd, data);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            data.setState(MMMState.SELECTING_ELEMENT);
            workspace.reloadWorkspace(dataManager);
        }
    }

    private File promptForImage() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // SETUP THE FILE CHOOSER FOR PICKING IMAGES
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./images/"));
        FileChooser.ExtensionFilter extFilterBMP = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.BMP");
        FileChooser.ExtensionFilter extFilterGIF = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.GIF");
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterBMP, extFilterGIF, extFilterJPG, extFilterPNG);
        fileChooser.setSelectedExtensionFilter(extFilterPNG);

        // OPEN THE DIALOG
        File file = fileChooser.showOpenDialog(null);
        return file;
    }

    public void handleAddLabel() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        // CONSTRUCT THE DIALOG
        Label promptLabelText = new Label("Label text: ");
        TextField typeabelText = new TextField();
        HBox labelText = new HBox(promptLabelText, typeabelText);
        labelText.setPadding(new Insets(20));
        Button ok = new Button("OK");
        Button close = new Button("CLOSE");
        HBox buttons = new HBox(ok, close);
        buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        VBox pane = new VBox(labelText, buttons);
        pane.setPadding(new Insets(50));
        Scene dialogScene = new Scene(pane);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Label");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
        ok.setOnAction(e -> {
            dialogStage.close();
            DraggableText label = new DraggableText(typeabelText.getText());
            label.start(100, 100);
            AddElementTran transaction = new AddElementTran(label, data);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            data.setState(MMMState.SELECTING_ELEMENT);
            workspace.reloadWorkspace(dataManager);
        });
        close.setOnAction(e -> {
            dialogStage.close();
        });
    }

    public void handleRemoveElement() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        if (node != null) {
            if (node instanceof DraggableImage || (node instanceof DraggableText && !workspace.getMetroLines().getItems().contains(((DraggableText) node).getId()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getId()))) {
                RemoveElementTran transaction = new RemoveElementTran(data, node);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleTextColorPicker() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        Color color = null;
        ChangeTextColorTran transaction = null;
        if (node != null) {
            // SELECT STATION
            if (node instanceof DraggableEllipse) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(node.getId())) {
                        text = (DraggableText) n;
                        color = (Color) text.getFill();
                        break;
                    }
                }
                text.setFill(workspace.getFontColor().getValue());
                transaction = new ChangeTextColorTran(text, color);

            } // SELECT LINE END
            else if (node instanceof DraggableText && workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                color = (Color) ((DraggableText) node).getFill();
                ((DraggableText) node).setFill(workspace.getFontColor().getValue());
                transaction = new ChangeTextColorTran(((DraggableText) node), color);
            } // SELECT LABEL
            else if (node instanceof DraggableText && !workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                color = (Color) ((DraggableText) node).getFill();
                ((DraggableText) node).setFill(workspace.getFontColor().getValue());
                transaction = new ChangeTextColorTran(((DraggableText) node), color);
            }
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } // CHECK METRO STATION COMBO BOX
        else {
            if (workspace.getMetroStations().getValue() != null) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(workspace.getMetroStations().getValue().toString())) {
                        text = (DraggableText) n;
                        color = (Color) text.getFill();
                        break;
                    }
                }
                text.setFill(workspace.getFontColor().getValue());
                transaction = new ChangeTextColorTran(text, color);
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleSetBold() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        ChangeTextBoldTran transaction = null;
        if (node != null) {
            // SELECT STATION
            if (node instanceof DraggableEllipse) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(node.getId())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextBoldTran(text, text.getFont().getStyle().contains("Bold"));

            } // SELECT LINE END
            else if (node instanceof DraggableText && workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {

                transaction = new ChangeTextBoldTran(((DraggableText) node), ((DraggableText) node).getFont().getStyle().contains("Bold"));
            } // SELECT LABEL
            else if (node instanceof DraggableText && !workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {

                transaction = new ChangeTextBoldTran(((DraggableText) node), ((DraggableText) node).getFont().getStyle().contains("Bold"));
            }
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } // CHECK METRO STATION COMBO BOX
        else {
            if (workspace.getMetroStations().getValue() != null) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(workspace.getMetroStations().getValue().toString())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextBoldTran(text, text.getFont().getStyle().contains("Bold"));
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleSetItalic() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        ChangeTextItalicTran transaction = null;
        if (node != null) {
            // SELECT STATION
            if (node instanceof DraggableEllipse) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(node.getId())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextItalicTran(text, text.getFont().getStyle().contains("Italic"));
            } // SELECT LINE END
            else if (node instanceof DraggableText && workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                transaction = new ChangeTextItalicTran(((DraggableText) node), ((DraggableText) node).getFont().getStyle().contains("Italic"));
            } // SELECT LABEL
            else if (node instanceof DraggableText && !workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                transaction = new ChangeTextItalicTran(((DraggableText) node), ((DraggableText) node).getFont().getStyle().contains("Italic"));
            }
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } // CHECK METRO STATION COMBO BOX
        else {
            if (workspace.getMetroStations().getValue() != null) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(workspace.getMetroStations().getValue().toString())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextItalicTran(text, text.getFont().getStyle().contains("Italic"));
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleChangeFontSize() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        ChangeTextSizeTran transaction = null;
        if (node != null) {
            // SELECT STATION
            if (node instanceof DraggableEllipse) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(node.getId())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextSizeTran(text, text.getFont().getSize(), Double.parseDouble(workspace.getSize().getValue().toString()));
            } // SELECT LINE END
            else if (node instanceof DraggableText && workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                transaction = new ChangeTextSizeTran(((DraggableText) node), ((DraggableText) node).getFont().getSize(), Double.parseDouble(workspace.getSize().getValue().toString()));
            } // SELECT LABEL
            else if (node instanceof DraggableText && !workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                transaction = new ChangeTextSizeTran(((DraggableText) node), ((DraggableText) node).getFont().getSize(), Double.parseDouble(workspace.getSize().getValue().toString()));
            }
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } // CHECK METRO STATION COMBO BOX
        else {
            if (workspace.getMetroStations().getValue() != null) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(workspace.getMetroStations().getValue().toString())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextSizeTran(text, text.getFont().getSize(), Double.parseDouble(workspace.getSize().getValue().toString()));
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleChangeFontFamily() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.getSelectedNode();
        ChangeTextFontTran transaction = null;
        if (node != null) {
            // SELECT STATION
            if (node instanceof DraggableEllipse) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(node.getId())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextFontTran(text, text.getFont().getFamily(), workspace.getFamily().getValue().toString());

            } // SELECT LINE END
            else if (node instanceof DraggableText && workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                transaction = new ChangeTextFontTran(((DraggableText) node), ((DraggableText) node).getFont().getFamily(), workspace.getFamily().getValue().toString());
            } // SELECT LABEL
            else if (node instanceof DraggableText && !workspace.getMetroLines().getItems().contains(((DraggableText) node).getText()) && !workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                transaction = new ChangeTextFontTran(((DraggableText) node), ((DraggableText) node).getFont().getFamily(), workspace.getFamily().getValue().toString());
            }
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            workspace.reloadWorkspace(dataManager);
        } // CHECK METRO STATION COMBO BOX
        else {
            if (workspace.getMetroStations().getValue() != null) {
                DraggableText text = null;
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(workspace.getMetroStations().getValue().toString())) {
                        text = (DraggableText) n;
                        break;
                    }
                }
                transaction = new ChangeTextFontTran(text, text.getFont().getFamily(), workspace.getFamily().getValue().toString());
                jTPS tps = app.getJTPS();
                tps.addTransaction(transaction);
                workspace.reloadWorkspace(dataManager);
            }
        }
    }

    public void handleShowGrid() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        if (workspace.getShowGrid().isSelected()) {
            // HORIZONTAL LINES
            for (int i = 10; i < workspace.getGrid().getHeight(); i = i + 10) {
                Line line = new Line();
                line.setStartX(0.0);
                line.setStartY(i);
                line.setEndX(workspace.getGrid().getWidth());
                line.setEndY(i);
                workspace.getGrid().getChildren().add(line);
            }
            // VERTICAL LINES
            for (int i = 10; i < workspace.getGrid().getWidth(); i = i + 10) {
                Line line = new Line();
                line.setStartX(i);
                line.setStartY(0.0);
                line.setEndX(i);
                line.setEndY(workspace.getGrid().getHeight());
                workspace.getGrid().getChildren().add(line);
            }
        } else {
            if (workspace.getGrid().getChildren().get(0) instanceof ImageView) {
                workspace.getGrid().getChildren().remove(1, workspace.getGrid().getChildren().size());
            } else {
                workspace.getGrid().getChildren().clear();
            }
        }
    }

    public void handleZoomIn() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        workspace.getScroll().getContent().setScaleX(workspace.getScroll().getContent().getScaleX() + 0.1);
        workspace.getScroll().getContent().setScaleY(workspace.getScroll().getContent().getScaleY() + 0.1);
    }

    public void handleZoomOut() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        workspace.getScroll().getContent().setScaleX(workspace.getScroll().getContent().getScaleX() - 0.1);
        workspace.getScroll().getContent().setScaleY(workspace.getScroll().getContent().getScaleY() - 0.1);
    }

    public void handleIncreaseMapSize() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        workspace.getGrid().setScaleX(workspace.getGrid().getScaleX() + 0.1);
        workspace.getGrid().setScaleY(workspace.getGrid().getScaleY() + 0.1);
        for (Node n : workspace.getGrid().getChildren()) {
            n.setScaleX(1);
            n.setScaleY(1);
        }
        for (Node n : workspace.getElement().getChildren()) {
            n.setScaleX(1);
            n.setScaleY(1);
        }
    }

    public void handleDecreaseMapSize() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        System.out.println(workspace.getGrid().getBoundsInParent().getWidth());
        System.out.println(workspace.getGrid().getBoundsInParent().getHeight());
        if (workspace.getGrid().getBoundsInParent().getWidth() > 200 && workspace.getGrid().getBoundsInParent().getHeight() > 200) {
            workspace.getGrid().setScaleX(workspace.getGrid().getScaleX() - 0.1);
            workspace.getGrid().setScaleY(workspace.getGrid().getScaleY() - 0.1);
            for (Node n : workspace.getGrid().getChildren()) {
                n.setScaleX(1);
                n.setScaleY(1);
            }
            for (Node n : workspace.getElement().getChildren()) {
                n.setScaleX(1);
                n.setScaleY(1);
            }
        }
    }

    public void handleExport() {
        processSnapshot();
    }

    public void handleUndo() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        app.getJTPS().undoTransaction();
        workspace.reloadWorkspace(dataManager);
    }

    public void handleRedo() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        app.getJTPS().doTransaction();
        workspace.reloadWorkspace(dataManager);
    }

    public void handleLearnAbout() {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About the application");
        VBox pane = new VBox();
        Label name = new Label("App Name: Metro Map Maker");
        Label framework = new Label("Framework: DesktopJavaFramekwork, PropertiesManager, jTPS");
        Label developer = new Label("Developer: Doeun Kim");
        Label year = new Label("2017");
        pane.getChildren().addAll(name, framework, developer, year);
        pane.setPadding(new Insets(80, 60, 80, 60));
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(20);
        Scene aboutScene = new Scene(pane);
        aboutStage.setScene(aboutScene);
        aboutStage.show();
    }

    /**
     * This method processes a user request to take a snapshot of the current
     * scene.
     */
    public void processSnapshot() {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        Pane canvas = workspace.getCanvas();
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        String fileName = app.getGUI().getWindow().getTitle().replace("Metro Map Maker - ", "");
        File file = new File(AppStartupConstants.PATH_EXPORT + "/" + fileName + "/" + fileName + " Metro.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
