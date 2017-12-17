package MMM.gui;

import MMM.data.Draggable;
import javafx.scene.Scene;
import MMM.data.MMMData;
import MMM.data.DraggableEllipse;
import MMM.data.DraggableImage;
import MMM.data.DraggableText;
import MMM.data.MMMState;
import MMM.data.MetroLine;
import MMM.tran.AddStationToLineTran;
import MMM.tran.MoveElementTran;
import MMM.tran.MoveLineEndTran;
import MMM.tran.RemoveStationFromLineTran;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jtps.jTPS;

/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class MMMCanvasController {

    AppTemplate app;
    Line lineToMove;
    MetroLine metroLineToMove;
    double oldX;
    double oldY;
    int startX;
    int startY;

    public MMMCanvasController(AppTemplate initApp) {
        app = initApp;
    }

    public MetroLine findSelectedLine(String lineName) {
        for (MetroLine node : ((MMMData) app.getDataComponent()).getLines()) {
            if (node instanceof MetroLine && node.getId().equals(lineName)) {
                return node;
            }
        }
        return null;
    }

    public int findIndexOfClosestStation(Node station, Node line) {
        int index = -1;
        double x = ((DraggableEllipse) station).getCenterX();
        double y = ((DraggableEllipse) station).getCenterY();
        double minDis = 100000000;
        ArrayList<DraggableEllipse> stations = ((MetroLine) line).getStations();
        for (int i = 0; i < stations.size(); i++) {
            double x1 = stations.get(i).getCenterX();
            double y1 = stations.get(i).getCenterY();
            double dis = findPythagorianDistance(x, y, x1, y1);
            if (minDis > dis) {
                minDis = dis;
                index = i;
            }
        }
        return index;
    }

    public double findPythagorianDistance(double x, double y, double x1, double y1) {
        return Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
    }

    public Line createRightLineSegment(MetroLine metroLine, DraggableEllipse node, int index) {
        Line leftLine = metroLine.getLineSegments().get(index);
        // GET OLD X,Y
        double oldendx = leftLine.getEndX();
        double oldendy = leftLine.getEndY();
        // LEFT LINE SEGMENT'S END X,Y COORDINATE BINDS WITH THE STATION'S CENTER X,Y COORDINATE
        leftLine.endXProperty().bind(node.centerXProperty());
        leftLine.endYProperty().bind(node.centerYProperty());
        // CREATE RIGHT LINE SEGMENT
        Line rightLine = new Line();
        rightLine.setStroke((Color) leftLine.getStroke());
        rightLine.setStrokeWidth(leftLine.getStrokeWidth());
        rightLine.setId(leftLine.getId());
        rightLine.setOpacity(1.0);
        // RIGHT LINE SEGMENT'S START X,Y COORDINATE BINDS WITH THE STATION'S CENTER X,Y COORDINATE
        rightLine.startXProperty().bind(node.centerXProperty());
        rightLine.startYProperty().bind(node.centerYProperty());
        // SET RIGHT LINE SEGMENT'S END X,Y COORDINATE
        rightLine.setEndX(oldendx);
        rightLine.setEndY(oldendy);
        return rightLine;
    }

    public void addStationToRightEnd(DraggableText label, MetroLine metroLine, Line rightLine, DraggableEllipse node, int indexToTheClosestStation, MMMData dataManager, String selectedLineName) {
        // ADD RIGHT LINE SEGMENT TO THE OBSERVABLE LIST
        dataManager.getNodes().add(0, rightLine);
        // ADD RIGHT LINE SEGMENT TO THE ARRAY LIST OF METRO LINE
        metroLine.getLineSegments().add(rightLine);
        metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 2).endXProperty().bind(((DraggableEllipse) node).centerXProperty());
        metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 2).endYProperty().bind(((DraggableEllipse) node).centerYProperty());
        label.xProperty().bind(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty());
        label.yProperty().bind(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty());
        // MOVE RIGHT LABEL
//        for (Node n : dataManager.getNodes()) {
//            if (n instanceof DraggableText && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT) && ((DraggableText) n).getText().equals(selectedLineName)) {
//                ((DraggableText) n).xProperty().bind(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty());
//                ((DraggableText) n).yProperty().bind(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty());
//                break;
//            }
//        }
        // ADD THE STATION TO THE TWO ARRAY LISTS OF METROLINE
        metroLine.getStationNames().add(indexToTheClosestStation + 1, node.getId());
        metroLine.getStations().add(indexToTheClosestStation + 1, (DraggableEllipse) node);
        ((DraggableEllipse) node).getLines().add(selectedLineName);
    }

    public void addStationToLeftEnd(DraggableText label, MetroLine metroLine, Line rightLine, DraggableEllipse node, int indexToTheClosestStation, MMMData dataManager, String selectedLineName) {
        // ADD RIGHT LINE SEGMENT TO THE OBSERVABLE LIST
        dataManager.getNodes().add(0, rightLine);
        // ADD RIGHT LINE SEGMENT TO THE ARRAY LIST OF METRO LINE
        metroLine.getLineSegments().add(indexToTheClosestStation + 1, rightLine);
        // CONNECT LEFT LINE WITH STATION
        metroLine.getLineSegments().get(indexToTheClosestStation).endXProperty().bind(((DraggableEllipse) node).centerXProperty());
        metroLine.getLineSegments().get(indexToTheClosestStation).endYProperty().bind(((DraggableEllipse) node).centerYProperty());
        // MOVE LEFT LABEL
        label.xProperty().bind(metroLine.getLineSegments().get(0).startXProperty().subtract(label.getLayoutBounds().getWidth()));
        label.yProperty().bind(metroLine.getLineSegments().get(0).startYProperty());
        // CONNECT RIGHT LINE WITH ANOTHER STATION
        rightLine.endXProperty().bind(metroLine.getStations().get(0).centerXProperty());
        rightLine.endYProperty().bind(metroLine.getStations().get(0).centerYProperty());
        // ADD THE STATION TO THE TWO ARRAY LISTS OF METROLINE
        if (!metroLine.getStationNames().contains(node.getId())) {
            metroLine.getStationNames().add(0, node.getId());
            metroLine.getStations().add(0, (DraggableEllipse) node);
        }

        ((DraggableEllipse) node).getLines().add(selectedLineName);
    }

    public void addStationBetweenTwoStations(MetroLine metroLine, Line rightLine, DraggableEllipse node, DraggableEllipse rightnode, int indexToTheClosestStation, MMMData dataManager, String selectedLineName) {
        // ADD RIGHT LINE SEGMENT TO THE OBSERVABLE LIST
        dataManager.getNodes().add(0, rightLine);
        // ADD RIGHT LINE SEGMENT TO THE ARRAY LIST OF METRO LINE
        metroLine.getLineSegments().add(indexToTheClosestStation + 1, rightLine);
        // CONNECT LEFT LINE WITH RIGHT STATION
        metroLine.getLineSegments().get(indexToTheClosestStation).endXProperty().bind(node.centerXProperty());
        metroLine.getLineSegments().get(indexToTheClosestStation).endYProperty().bind(node.centerYProperty());

        // CONNECT RIGHT LINE WITH RIGHT STATION
        rightLine.endXProperty().bind(rightnode.centerXProperty());
        rightLine.endYProperty().bind(rightnode.centerYProperty());
        // ADD THE STATION TO THE TWO ARRAY LISTS OF METROLINE
        if (!metroLine.getStationNames().contains(node.getId())) {
            metroLine.getStationNames().add(indexToTheClosestStation, node.getId());
            metroLine.getStations().add(indexToTheClosestStation, node);
        }
        node.getLines().add(selectedLineName);
    }

    /**
     * Respond to mouse presses on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMousePress(int x, int y) {
        MMMData dataManager = (MMMData) app.getDataComponent();
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        Scene scene = app.getGUI().getPrimaryScene();
        Node node = dataManager.selectTopNode(x, y);
        // MAKE ALL NODES UNHIGHLIGHTED
        for (Node n : dataManager.getNodes()) {
            dataManager.unhighlightNode(n);
        }
        if (node == null) {
            workspace.getBold().setSelected(false);
            workspace.getItalic().setSelected(false);
            workspace.getRemoveStationFromLine().setSelected(false);
            workspace.getAddStationToLine().setSelected(false);
            dataManager.setSelectedNode(null);
            scene.setCursor(Cursor.DEFAULT);
            dataManager.setState(MMMState.DOING_NOTHING);
            workspace.getMetroStationColorPicker().setValue(Color.BLACK);
        } else {
            dataManager.highlightNode(node);
            workspace.loadSelectedNodeSettings(node);
            // ADD STATIONS TO LINE
            if (workspace.getAddStationToLine().isSelected()) {
                // CLICK ON THE STATION
                if (node instanceof DraggableEllipse && workspace.getMetroLines().getValue() != null) {
                    DraggableEllipse station = (DraggableEllipse) node;
                    String selectedLineName = workspace.getMetroLines().getValue().toString();
                    // FIND THE METRO LINE
                    MetroLine metroLine = null;
                    for (MetroLine ml : dataManager.getLines()) {
                        if (ml.getId().equals(selectedLineName)) {
                            metroLine = ml;
                            break;
                        }
                    }

                    AddStationToLineTran transaction = null;
                    // IF THE METRO LINE HAS NO STATION
                    if (metroLine.getStations().size() == 0) {
                        // FIND  RIGHT LABEL
                        DraggableText label = null;
                        for (Node n : dataManager.getNodes()) {
                            if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                                label = (DraggableText) n;
                                break;
                            }
                        }
                        Line rightLine = createRightLineSegment(metroLine, station, 0);
                        transaction = new AddStationToLineTran(app, label, metroLine.getStations().size(), rightLine, metroLine, station, 0, selectedLineName);
                    } else {
                        if (metroLine.getStations().contains(station)) {
                            Line leftLine = metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1);
                            Line rightLine = new Line();
                            rightLine.setStroke((Color) leftLine.getStroke());
                            rightLine.setStrokeWidth(leftLine.getStrokeWidth());
                            rightLine.setId(leftLine.getId());
                            rightLine.setOpacity(1.0);
                            rightLine.startXProperty().bind(station.centerXProperty());
                            rightLine.startYProperty().bind(station.centerYProperty());
                            rightLine.setEndX(leftLine.getEndX());
                            rightLine.setEndY(leftLine.getEndY());
                            DraggableText label = null;
                            for (Node n : dataManager.getNodes()) {
                                if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                                    label = (DraggableText) n;
                                    break;
                                }
                            }
                            transaction = new AddStationToLineTran(app, label, metroLine.getStations().size(), rightLine, metroLine, station, 0, selectedLineName);
                        } else {
                            // FIND THE INDEX OF THE CLOSEST STATION ON THE LINE
                            int indexOfTheClosestStation = findIndexOfClosestStation(station, metroLine);
                            // IF THERE IS ONLY ONE STATION ON THE LINE
                            if (metroLine.getStationNames().size() == 1) {
                                // IF THE STATION TO ADD IS ON THE LEFT OF THE CLOSEST STATION
                                if (findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(1).getEndX(), metroLine.getLineSegments().get(1).getEndY())
                                        > findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(0).getStartX(), metroLine.getLineSegments().get(0).getStartY())) {
                                    Line rightLine = createRightLineSegment(metroLine, station, indexOfTheClosestStation);
                                    //addStationToLeftEnd(metroLine, rightLine, station, 0, dataManager, selectedLineName);
                                    DraggableText label = null;
                                    for (Node n : dataManager.getNodes()) {
                                        if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.RIGHT)) {
                                            label = (DraggableText) n;
                                            break;
                                        }
                                    }
                                    transaction = new AddStationToLineTran(app, label, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);

                                } // IF THE STATION TO ADD IS ON THE RIGHT OF THE CLOSEST STATION
                                else {
                                    // CREATE RIGHT LINE SEGMENT
                                    Line rightLine = createRightLineSegment(metroLine, station, 1);
                                    //addStationToRightEnd(metroLine, rightLine, station, 0, dataManager, selectedLineName);
                                    DraggableText label = null;
                                    for (Node n : dataManager.getNodes()) {
                                        if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                                            label = (DraggableText) n;
                                            break;
                                        }
                                    }
                                    transaction = new AddStationToLineTran(app, label, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);
                                }
                            } // IF THERE ARE MORE THAN ONE STATIONS ON THE LINE
                            else {
                                // INDEX OF THE CLOSEST STATION IS THE LAST STATION
                                if (indexOfTheClosestStation == metroLine.getStations().size() - 1) {
                                    // STATION IS ON THE LEFT OF THE LAST STATION
                                    if (findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndX(), metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndY())
                                            > findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getStations().get(indexOfTheClosestStation - 1).getCenterX(), metroLine.getStations().get(indexOfTheClosestStation - 1).getCenterY())) {
                                        Line rightLine = createRightLineSegment(metroLine, station, indexOfTheClosestStation);
                                        //addStationBetweenTwoStations(metroLine, rightLine, station, metroLine.getStations().get(indexOfTheClosestStation), indexOfTheClosestStation, dataManager, selectedLineName);
                                        transaction = new AddStationToLineTran(app, null, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);

                                    } // STATION IS ON THE RIGHT OF THE LAST STATION
                                    else {
                                        Line rightLine = createRightLineSegment(metroLine, station, metroLine.getLineSegments().size() - 1);
                                        //addStationToRightEnd(metroLine, rightLine, station, indexOfTheClosestStation, dataManager, selectedLineName);
                                        DraggableText label = null;
                                        for (Node n : dataManager.getNodes()) {
                                            if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                                                label = (DraggableText) n;
                                                break;
                                            }
                                        }
                                        transaction = new AddStationToLineTran(app, label, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);

                                    }
                                } // INDEX OF THE CLOSEST STATION IS THE FIRST STATION
                                else if (indexOfTheClosestStation == 0) {
                                    // STATION IS ON THE LEFT OF THE FIRST STATION
                                    if (findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(0).getStartX(), metroLine.getLineSegments().get(0).getStartY())
                                            < findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getStations().get(indexOfTheClosestStation + 1).getCenterX(), metroLine.getStations().get(indexOfTheClosestStation + 1).getCenterY())) {
                                        Line rightLine = createRightLineSegment(metroLine, station, 0);
                                        // addStationToLeftEnd(metroLine, rightLine, station, 0, dataManager, selectedLineName);
                                        DraggableText label = null;
                                        for (Node n : dataManager.getNodes()) {
                                            if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.RIGHT)) {
                                                label = (DraggableText) n;
                                                break;
                                            }
                                        }
                                        transaction = new AddStationToLineTran(app, label, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);

                                    } // STATION IS ON THE RIGHT OF THE FIRST STATION
                                    else {
                                        Line rightLine = createRightLineSegment(metroLine, station, 1);
                                        //addStationBetweenTwoStations(metroLine, rightLine, station, metroLine.getStations().get(indexOfTheClosestStation + 1), indexOfTheClosestStation + 1, dataManager, selectedLineName);
                                        transaction = new AddStationToLineTran(app, null, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);

                                    }
                                } // INDEX OF THE CLOSEST STATION IS SOMEWHERE IN THE MIDDLE
                                else {
                                    Line rightLine = createRightLineSegment(metroLine, station, indexOfTheClosestStation);
                                    //addStationBetweenTwoStations(metroLine, rightLine, station, metroLine.getStations().get(indexOfTheClosestStation), indexOfTheClosestStation, dataManager, selectedLineName);
                                    transaction = new AddStationToLineTran(app, null, metroLine.getStations().size(), rightLine, metroLine, station, indexOfTheClosestStation, selectedLineName);

                                }
                            }
                        }

                    }
                    jTPS tps = app.getJTPS();
                    tps.addTransaction(transaction);
                    workspace.reloadWorkspace(dataManager);
                } else {
                    dataManager.unhighlightNode(node);
                    dataManager.setSelectedNode(null);
                    workspace.getAddStationToLine().setSelected(false);
                    scene.setCursor(Cursor.DEFAULT);
                    dataManager.setState(MMMState.DOING_NOTHING);
                }
            } // REMOVE STATIONS FROM LINE
            else if (workspace.getRemoveStationFromLine().isSelected()) {
                // CLICK ON THE STATION
                if (node instanceof DraggableEllipse && workspace.getMetroLines().getValue() != null && ((DraggableEllipse) node).getLines().size() > 0) {
                    DraggableEllipse station = (DraggableEllipse) node;
                    String selectedLine = workspace.getMetroLines().getValue().toString();
                    // IF THE STATION IS ON THE SELECTED LINE
                    if (station.getLines().contains(selectedLine)) {

                        // GET THE METRO LINE
                        MetroLine metroLine = null;
                        for (MetroLine ml : dataManager.getLines()) {
                            if (ml.getId().equals(selectedLine)) {
                                metroLine = ml;
                                break;
                            }
                        }
                        int dup = 0;
                        for (String s : metroLine.getStationNames()) {
                            if (s.equals(station.getId())) {
                                dup++;
                            }
                        }
                        int index = 0;
                        if (dup == 1) {
                            // GET INDEX OF STATION
                            index = metroLine.getStations().indexOf(station);
                        }
                        else{
                            index= metroLine.getStationNames().lastIndexOf(station.getId());
                        }

                        // GET RIGHT LINE SEGMENT OF THE STATION
                        Line rightLine = metroLine.getLineSegments().get(index + 1);
                        // GET  RIGHT LABEL
                        DraggableText label = null;
                        for (Node n : dataManager.getNodes()) {
                            if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLine) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                                label = (DraggableText) n;
                                break;
                            }
                        }
                        RemoveStationFromLineTran transaction = new RemoveStationFromLineTran(app, label, rightLine, metroLine, station, index, selectedLine);

                        jTPS tps = app.getJTPS();
                        tps.addTransaction(transaction);
                        workspace.reloadWorkspace(dataManager);
                    }
                } // CLICK NOT ON THE STATION
                else {
                    dataManager.unhighlightNode(node);
                    workspace.getRemoveStationFromLine().setSelected(false);
                    scene.setCursor(Cursor.DEFAULT);
                    dataManager.setState(MMMState.DOING_NOTHING);
                    dataManager.setSelectedNode(null);
                }
            } // SELECT A NODE
            else if (node == dataManager.getSelectedNode() && (dataManager.isInState(MMMState.SELECTING_ELEMENT) || dataManager.isInState(MMMState.SELECTING_LINE) || dataManager.isInState(MMMState.SELECTING_STATION))) {
                // SELECT A LINE LABEL
                if (dataManager.isInState(MMMState.SELECTING_LINE) && node instanceof DraggableText && workspace.getMetroLines().getItems().indexOf(((DraggableText) node).getText()) != -1) {
                    //dataManager.highlightNode(node);
                    dataManager.setState(MMMState.MOVING_LINE_END);
                    // FIND THE LINE
                    for (MetroLine ml : dataManager.getLines()) {
                        if (ml.getId().equals(((DraggableText) node).getText())) {
                            metroLineToMove = ml;
                            if (((DraggableText) node).getTextAlignment().equals(TextAlignment.RIGHT)) {
                                lineToMove = ml.getLineSegments().get(0);
                                oldX = lineToMove.getStartX();
                                oldY = lineToMove.getStartY();
                            } else {
                                lineToMove = ml.getLineSegments().get(ml.getLineSegments().size() - 1);
                                oldX = lineToMove.getEndX();
                                oldY = lineToMove.getEndY();
                            }
                            break;
                        }
                    }
                    startX = x;
                    startY = y;
                } // SELECT A STATION
                else if (dataManager.isInState(MMMState.SELECTING_STATION) && node instanceof DraggableEllipse) {
                    dataManager.setState(MMMState.MOVING_STATION);
                    oldX = ((DraggableEllipse) node).getCenterX();
                    oldY = ((DraggableEllipse) node).getCenterY();
                } // SELECT A LINE
                else if (node instanceof Line) {
                    for (Node line : dataManager.getNodes()) {
                        if (!(line instanceof Line)) {
                            break;
                        }
                        if (line.getId().equals(node.getId())) {
                            dataManager.highlightNode(line);
                        }
                    }
                } // SELECT EITHER A IMAGE OR A TEXT
                else if (dataManager.isInState(MMMState.SELECTING_ELEMENT)) {
                    dataManager.setState(MMMState.MOVING_ELEMENT);
                    if (node instanceof DraggableImage) {
                        oldX = ((DraggableImage) node).getX();
                        oldY = ((DraggableImage) node).getY();
                        ((DraggableImage) node).setStart(x, y);
                    } else {
                        oldX = ((DraggableText) node).getX();
                        oldY = ((DraggableText) node).getY();
                        ((DraggableText) node).setStart(x, y);
                    }
                }

            } else {
                if (node instanceof DraggableEllipse) {
                    dataManager.setState(MMMState.SELECTING_STATION);
                    dataManager.setSelectedNode(node);
                } else if (node instanceof DraggableText) {
                    dataManager.setSelectedNode(node);
                    if (workspace.getMetroLines().getItems().contains(((DraggableText) node).getText())) {
                        dataManager.setState(MMMState.SELECTING_LINE);
                    } else if (!workspace.getMetroStations().getItems().contains(((DraggableText) node).getText())) {
                        dataManager.setState(MMMState.SELECTING_ELEMENT);
                    }
                } else if (node instanceof DraggableImage) {
                    dataManager.setState(MMMState.SELECTING_ELEMENT);
                    dataManager.setSelectedNode(node);
                } else if (node instanceof Line) {
                    for (Node line : dataManager.getNodes()) {
                        if (!(line instanceof Line)) {
                            break;
                        }
                        if (line.getId().equals(node.getId())) {
                            dataManager.highlightNode(line);
                        }
                    }
                }
            }
        }
    }

    /**
     * Respond to mouse double presses on the rendering surface, which we call
     * canvas, but is actually a Pane.
     */
    public void processCanvasMouseDoublePress(int x, int y) {
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        MMMData data = (MMMData) app.getDataComponent();
        Node node = data.selectTopNode(x, y);
        Button ok = new Button("OK");
        Button close = new Button("CLOSE");
        Stage dialogStage = new Stage();
        // SELECTED NODE IS A LABEL
        if (node instanceof DraggableText && workspace.getMetroLines().getItems().indexOf(((DraggableText) node).getText()) == -1) {

        }
    }

    /**
     * Respond to mouse dragging on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseDragged(int x, int y) {
        MMMData data = (MMMData) app.getDataComponent();
        if (!(data.getSelectedNode() instanceof Line)) {
            Draggable selectedDraggableShape = (Draggable) data.getSelectedNode();
            if (data.isInState(MMMState.MOVING_LINE_END)) {
                if (((DraggableText) selectedDraggableShape).getTextAlignment().equals(TextAlignment.RIGHT)) {
                    double diffX = x - startX;
                    double diffY = y - startY;
                    double newX = lineToMove.getStartX() + diffX;
                    double newY = lineToMove.getStartY() + diffY;
                    lineToMove.setStartX(newX);
                    lineToMove.setStartY(newY);
                    startX = x;
                    startY = y;
                } else {
                    double diffX = x - startX;
                    double diffY = y - startY;
                    double newX = lineToMove.getEndX() + diffX;
                    double newY = lineToMove.getEndY() + diffY;
                    lineToMove.setEndX(newX);
                    lineToMove.setEndY(newY);
                    startX = x;
                    startY = y;
                }
            } else if (data.isInState(MMMState.MOVING_STATION)) {
                if (selectedDraggableShape != null) {
                    selectedDraggableShape.drag(x, y);
                }
            } else if (data.isInState(MMMState.MOVING_ELEMENT)) {
                if (selectedDraggableShape != null) {
                    selectedDraggableShape.drag(x, y);
                }
            }
        }
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call
     * canvas, but is actually a Pane.
     */
    public void processCanvasMouseRelease(int x, int y) {
        MMMData data = (MMMData) app.getDataComponent();
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        if (data.isInState(MMMState.MOVING_LINE_END)) {
            MoveLineEndTran transaction = new MoveLineEndTran((DraggableText) data.getSelectedNode(), lineToMove, oldX, oldY);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            data.setState(MMMState.DOING_NOTHING);
            workspace.reloadWorkspace(data);
        } else if (data.isInState(MMMState.MOVING_STATION) || data.isInState(MMMState.MOVING_ELEMENT)) {
            MoveElementTran transaction = new MoveElementTran(data.getSelectedNode(), oldX, oldY);
            jTPS tps = app.getJTPS();
            tps.addTransaction(transaction);
            data.setState(MMMState.DOING_NOTHING);
            workspace.reloadWorkspace(data);
        }
    }
}
