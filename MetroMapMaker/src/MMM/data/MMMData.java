package MMM.data;

import MMM.gui.MMMCanvasController;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import MMM.gui.MMMWorkspace;
import djf.components.AppDataComponent;
import djf.AppTemplate;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.CornerRadii;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * This class serves as the data management component for this application.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class MMMData implements AppDataComponent {

    // THESE ARE THE NODES IN THE LOGO
    ObservableList<Node> nodes;
    ArrayList<MetroLine> lines;
//HashMap<String, Line> lines;
    //HashMap<String, DraggableEllipse> stations;
    //HashMap<MetroLine, ArrayList<DraggableEllipse>> connections;
    // THIS IS THE SHAPE CURRENTLY BEING SIZED BUT NOT YET ADDED
    Shape newShape;

    // THIS IS THE NODE CURRENTLY SELECTED
    Node selectedNode;

    // CURRENT STATE OF THE APP
    MMMState state;

    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;

    // USE THIS WHEN THE NODE IS SELECTED
    Effect highlightedEffect;
    ImageView backgroundImage;

    public static final String WHITE_HEX = "#FFFFFF";
    public static final String BLACK_HEX = "#000000";
    public static final String YELLOW_HEX = "#EEEE00";
    public static final Paint DEFAULT_BACKGROUND_COLOR = Paint.valueOf(WHITE_HEX);
    public static final Paint HIGHLIGHTED_COLOR = Paint.valueOf(YELLOW_HEX);
    public static final int HIGHLIGHTED_STROKE_THICKNESS = 3;

    // THE BACKGROUND COLOR
    Color backgroundColor;
// FOR FILL AND OUTLINE
    Color currentFillColor;
    Color currentOutlineColor;
    double currentBorderWidth;
    //int metroLineIndex = 0;

    /**
     * THis constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public MMMData(AppTemplate initApp) {
        // KEEP THE APP FOR LATER
        app = initApp;

        // NO SHAPE STARTS OUT AS SELECTED
        newShape = null;
        selectedNode = null;
        lines = new ArrayList<>();
        //stations = new HashMap<>();
        //lines = new HashMap<>();
        //connections = new HashMap<>();
        // THIS IS FOR THE SELECTED SHAPE
        DropShadow dropShadowEffect = new DropShadow();
        dropShadowEffect.setOffsetX(0.0f);
        dropShadowEffect.setOffsetY(0.0f);
        dropShadowEffect.setSpread(1.0);
        dropShadowEffect.setColor(Color.YELLOW);
        dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
        dropShadowEffect.setRadius(15);
        highlightedEffect = dropShadowEffect;
        backgroundImage = null;
    }

//    public int getMetroLineIndex() {
//        return metroLineIndex;
//    }
//    public HashMap<String, Line> getLines(){
//        return lines;
//    }
//    public HashMap<String, DraggableEllipse> getStations(){
//        return stations;
//    }
//    public HashMap<MetroLine, ArrayList<DraggableEllipse>> getConnections() {
//        return connections;
//    }
    public ArrayList<MetroLine> getLines() {
        return lines;
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ObservableList<Node> initNodes) {
        nodes = initNodes;
    }

    public void removeSelectedNode() {
        if (selectedNode != null) {
            nodes.remove(selectedNode);
            selectedNode = null;
        }
    }

//    public void fillConnections() {
//        for (Node node : nodes) {
//            if (node instanceof MetroLine) {
//                connections.put((MetroLine) node, new ArrayList<DraggableEllipse>());
//            } else if (node instanceof DraggableEllipse) {
//                
//            }
//        }
//    }
    /**
     * This function clears out the HTML tree and reloads it with the minimal
     * tags, like html, head, and body such that the user can begin editing a
     * page.
     */
    @Override
    public void resetData() {
        setState(MMMState.DOING_NOTHING);
        app.getJTPS().resetTransactions();
        newShape = null;
        selectedNode = null;
// INIT THE COLORS
        currentFillColor = Color.web(WHITE_HEX);
        currentOutlineColor = Color.web(BLACK_HEX);
        lines.clear();
        nodes.clear();
        backgroundImage = null;
        backgroundColor = Color.WHITE;
        ((MMMWorkspace) app.getWorkspaceComponent()).getGrid().setScaleX(1);
        ((MMMWorkspace) app.getWorkspaceComponent()).getGrid().setScaleY(1);
        ((MMMWorkspace) app.getWorkspaceComponent()).getShowGrid().setSelected(false);
        ((MMMWorkspace) app.getWorkspaceComponent()).getScroll().getContent().setScaleY(1);
        ((MMMWorkspace) app.getWorkspaceComponent()).getScroll().getContent().setScaleX(1);
        ((MMMWorkspace) app.getWorkspaceComponent()).getScroll().getContent().setTranslateY(0);
        ((MMMWorkspace) app.getWorkspaceComponent()).getScroll().getContent().setTranslateX(0);
        ((MMMWorkspace) app.getWorkspaceComponent()).getBackgroundColorPicker().setValue(Color.web(WHITE_HEX));
        ((MMMWorkspace) app.getWorkspaceComponent()).getElement().getChildren().clear();
        ((MMMWorkspace) app.getWorkspaceComponent()).getGrid().getChildren().clear();
        ((MMMWorkspace) app.getWorkspaceComponent()).getMetroLines().getItems().clear();
        ((MMMWorkspace) app.getWorkspaceComponent()).getMetroStations().getItems().clear();
        ((MMMWorkspace) app.getWorkspaceComponent()).getDeparture().getItems().clear();
        ((MMMWorkspace) app.getWorkspaceComponent()).getArrival().getItems().clear();
        ((MMMWorkspace) app.getWorkspaceComponent()).getGrid().setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

    }

    public ImageView getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(ImageView i) {
        Pane grid = ((MMMWorkspace) app.getWorkspaceComponent()).getGrid();

        if (grid.getChildren().size() > 0 && grid.getChildren().get(0) instanceof ImageView) {
            grid.getChildren().remove(0);
        }
        if (i != null) {
            i.setX(0);
            i.setY(0);
            backgroundImage = i;
            grid.getChildren().add(0, i);
        } else {
            backgroundImage = null;
        }
    }

    public Color getBackgroundColor() {
        return (Color) ((MMMWorkspace) app.getWorkspaceComponent()).getGrid().getBackground().getFills().get(0).getFill();
    }

    public void setBackgroundColor(Color color) {
        Pane grid = ((MMMWorkspace) app.getWorkspaceComponent()).getGrid();
        BackgroundFill fill = new BackgroundFill(color, null, null);
        Background background = new Background(fill);
        grid.setBackground(background);
    }

//    public void selectSizedShape() {
//        if (selectedNode != null) {
//            unhighlightNode(selectedNode);
//        }
//        selectedNode = newShape;
//        highlightNode(selectedNode);
//        newShape = null;
//        if (state == SIZING_SHAPE) {
//            state = ((Draggable) selectedNode).getStartingState();
//        }
//    }
    public void unhighlightNode(Node node) {
        node.setEffect(null);
    }

    public void highlightNode(Node node) {
        node.setEffect(highlightedEffect);
    }

    public Shape getNewShape() {
        return newShape;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node initSelectedNode) {
        selectedNode = initSelectedNode;
    }

    public Node selectTopNode(int x, int y) {
        Node node = getTopNode(x, y);
        if (node == selectedNode) {
            return node;
        }

        if (selectedNode != null) {
            unhighlightNode(selectedNode);
        }
        if (node != null) {
            highlightNode(node);
            MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
            workspace.loadSelectedNodeSettings(node);
        }
        //selectedNode = node;
//        if (node != null) {
//            ((Draggable) node).setStart(x, y);
//        }
        return node;
    }

    public boolean isShape(Draggable node) {
        return ((node.getNodeType() == Draggable.ELLIPSE)
                || (node.getNodeType() == Draggable.TEXT));
    }

//    public Draggable getSelectedDraggableNode() {
//        if (selectedNode == null) {
//            return null;
//        } else {
//            return (Draggable) selectedNode;
//        }
//    }
    public Node getTopNode(int x, int y) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = (Node) nodes.get(i);
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }

    public MMMState getState() {
        return state;
    }

    public void setState(MMMState initState) {
        state = initState;
    }

    public boolean isInState(MMMState testState) {
        return state == testState;
    }

    // METHODS NEEDED BY TRANSACTIONS
    public void moveNodeToIndex(Node nodeToMove, int index) {
        int currentIndex = nodes.indexOf(nodeToMove);
        int numberOfNodes = nodes.size();
        if ((currentIndex >= 0) && (index >= 0) && (index < numberOfNodes)) {
            // IS IT SUPPOSED TO BE THE LAST ONE?
            if (index == (numberOfNodes - 1)) {
                nodes.remove(currentIndex);
                nodes.add(nodeToMove);
            } else {
                nodes.remove(currentIndex);
                nodes.add(index, nodeToMove);
            }
        }
    }

    public void removeElement(Node nodeToRemove) {
        int currentIndex = nodes.indexOf(nodeToRemove);
        if (currentIndex >= 0) {
            nodes.remove(currentIndex);
        }
    }

    public void addElement(Node nodeToAdd) {
        int currentIndex = nodes.indexOf(nodeToAdd);
        if (currentIndex < 0) {
            nodes.add(nodeToAdd);
        }
    }

    public void addNewLine(MetroLine line, DraggableText leftText, DraggableText rightText) {
        int currentIndex = nodes.indexOf(line);
        if (currentIndex < 0) {
            //metroLineIndex++;
            lines.add(line);
            nodes.addAll(leftText, rightText);
        }
    }

    public void addLine(ArrayList<Line> lineSegs, DraggableText leftLabel, DraggableText rightLabel, MetroLine metroLine) {
        // ADD LINE SEGMENTS TO THE NODES
        for (Line line : lineSegs) {
            nodes.add(0, line);
        }
        // ADD LABELS TO THE NODES
        nodes.addAll(leftLabel, rightLabel);
        // ADD METRO LINE 
        lines.add(metroLine);
        // ADD LINE NAME FROM STATIONS
        for (DraggableEllipse st : metroLine.getStations()) {
            st.getLines().add(metroLine.getId());
        }
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        workspace.getMetroLines().getItems().add(metroLine.getId());
        workspace.getMetroLines().setValue(metroLine.getId());
    }

    public void removeLine(ArrayList<Line> lineSegs, DraggableText leftLabel, DraggableText rightLabel, MetroLine metroLine) {
        int currentIndex = nodes.indexOf(lineSegs.get(0));
        if (currentIndex >= 0) {
            MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
            // REMOVE LINE SEGMENTS FROM NODES
            for (Line line : lineSegs) {
                nodes.remove(line);
            }
            // REMOVE METRO LINE 
            lines.remove(metroLine);
            // REMOVE LINE LABELS
            nodes.removeAll(leftLabel, rightLabel);
            // REMOVE LINE NAME FROM STATIONS
            for (DraggableEllipse st : metroLine.getStations()) {
                st.getLines().remove(metroLine.getId());
            }
            // REMOVE LINE FROM COMBOBOX
            workspace.getMetroLines().getItems().remove(metroLine.getId());
        }
    }

    public void addNewStation(DraggableEllipse station, DraggableText stationLabel) {
        int currentIndex = nodes.indexOf(station);
        MMMWorkspace workspace = (MMMWorkspace) app.getWorkspaceComponent();
        if (currentIndex < 0) {
            nodes.add(station);
            nodes.add(stationLabel);
        }
    }

    public void removeStation(DraggableEllipse station, DraggableText label) {
        int currentIndex = nodes.indexOf(station);
        if (currentIndex >= 0) {
            // IF STATION IS NOT ON THE LINE
            if (station.getLines().size() == 0) {
                // REMOVE STATION AND LABEL FROM MAP
                unhighlightNode(station);
                nodes.removeAll(station, label);
                setSelectedNode(null);
            } // IF STATION IS ON THE LINE
            else {
                // FOR EACH STATION'S LINE
                int numOfMetroLine = 0;
                while (numOfMetroLine < station.getLines().size()) {
                    // FIND THE STATION'S LINE
                    for (MetroLine ml : lines) {
                        int index = ml.getStations().indexOf(station);
                        if (ml.getId().equals(station.getLines().get(numOfMetroLine))) {
                            // CHECK THE STATION IS ON THE RIGHT END OF THE METRO LINE
                            if (ml.getStations().indexOf(station) == ml.getStations().size() - 1) {
                                double oldendx = ml.getLineSegments().get(ml.getLineSegments().size() - 1).getEndX();
                                double oldendy = ml.getLineSegments().get(ml.getLineSegments().size() - 1).getEndY();
                                // REMOVE RIGHT  LINE
                                nodes.remove(ml.getLineSegments().get(ml.getLineSegments().size() - 1));
                                ml.getLineSegments().remove(ml.getLineSegments().size() - 1);
                                // FIND THE RIGHT LABEL
                                DraggableText rightLabel = null;
                                for (Node n : nodes) {
                                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(ml.getId()) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                                        rightLabel = (DraggableText) n;
                                        break;
                                    }
                                }
                                // RELOCATE LEFT LINE
                                ml.getLineSegments().get(ml.getLineSegments().size() - 2).endXProperty().unbind();
                                ml.getLineSegments().get(ml.getLineSegments().size() - 2).endYProperty().unbind();
                                ml.getLineSegments().get(ml.getLineSegments().size() - 2).endXProperty().set(oldendx);
                                ml.getLineSegments().get(ml.getLineSegments().size() - 2).endYProperty().set(oldendy);
                                rightLabel.xProperty().bind(ml.getLineSegments().get(index).endXProperty());
                                rightLabel.yProperty().bind(ml.getLineSegments().get(index).endYProperty());
                            } // CHECK THE STATION IS ON THE LEFT END OR THE MID OF THE METRO LINE 
                            else {
                                ml.getLineSegments().get(index).endXProperty().bind(ml.getStations().get(index + 1).centerXProperty());
                                ml.getLineSegments().get(index).endYProperty().bind(ml.getStations().get(index + 1).centerYProperty());
                                // REMOVE RIGHT  LINE
                                nodes.remove(ml.getLineSegments().get(index + 1));
                                ml.getLineSegments().remove(index + 1);
                            }
                            // REMOVE STATION
                            ml.removeStationName(station.getId());
                            ml.removeStation(station);
                            nodes.remove(station);
                            // REMOVE STATION LABEL
                            for (Node node : nodes) {
                                if (node instanceof DraggableText && ((DraggableText) node).getText().equals(station.getId())) {
                                    nodes.remove(node);
                                    break;
                                }
                            }
                        }
                    }
                    numOfMetroLine++;
                }
            }
        }
    }

    public int getIndexOfNode(Node node) {
        return nodes.indexOf(node);
    }

    public void addNodeAtIndex(Node node, int nodeIndex) {
        nodes.add(nodeIndex, node);
    }

    public boolean isTextSelected() {
        if (selectedNode == null) {
            return false;
        } else {
            return (selectedNode instanceof Text);
        }
    }

    public void removeStationFromLine(DraggableText label, Line rightLine, MetroLine metroLine, DraggableEllipse station, int index, String selectedLineName) {
        station.getLines().remove(metroLine.getId());
        // STATION IS ON THE RIGHT END
        if (index == metroLine.getStations().size() - 1) {
            double oldendx = metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndX();
            double oldendy = metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndY();
            // REMOVE RIGHT  LINE
            nodes.remove(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1));
            metroLine.getLineSegments().remove(metroLine.getLineSegments().size() - 1);
            // REMOVE STATION
            metroLine.removeStationName(station.getId());
            metroLine.removeStation(station);
            // FIND THE RIGHT LABEL
            DraggableText rightLabel = null;
            for (Node n : nodes) {
                if (n instanceof DraggableText && ((DraggableText) n).getText().equals(selectedLineName) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                    rightLabel = (DraggableText) n;
                    break;
                }
            }
            // RELOCATE LEFT LINE
            metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty().unbind();
            metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty().unbind();
            metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty().set(oldendx);
            metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty().set(oldendy);
            rightLabel.xProperty().bind(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty());
            rightLabel.yProperty().bind(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty());
        } // STATION IS ON THE LEFT END OR THE MID
        else {
            metroLine.getLineSegments().get(index).endXProperty().bind(metroLine.getStations().get(index + 1).centerXProperty());
            metroLine.getLineSegments().get(index).endYProperty().bind(metroLine.getStations().get(index + 1).centerYProperty());
            // REMOVE RIGHT  LINE
            nodes.remove(metroLine.getLineSegments().get(index + 1));
            metroLine.getLineSegments().remove(index + 1);
            // REMOVE STATION
            metroLine.removeStationName(station.getId());
            metroLine.removeStation(station);
        }

    }

    public void addStationToLine(DraggableText label, int size, Line rightLine, MetroLine metroLine, DraggableEllipse station, int index, String selectedLineName) {
        MMMCanvasController c = new MMMCanvasController(app);
        if (size == 0) {
            // ADD RIGHT LINE SEGMENT TO THE OBSERVABLE LIST
            nodes.add(0, rightLine);
            // ADD RIGHT LINE SEGMENT TO THE ARRAY LIST OF METRO LINE
            metroLine.getLineSegments().add(rightLine);
            metroLine.getLineSegments().get(0).endXProperty().bind(station.centerXProperty());
            metroLine.getLineSegments().get(0).endYProperty().bind(station.centerYProperty());
            // MOVE RIGHT LABEL
            label.xProperty().bind(rightLine.endXProperty());
            label.yProperty().bind(rightLine.endYProperty());
            // ADD THE STATION TO THE TWO ARRAY LISTS OF METROLINE
            metroLine.getStationNames().add(station.getId());
            metroLine.getStations().add(station);
            station.getLines().add(selectedLineName);
        } else {
            // IF THERE IS ONLY ONE STATION ON THE LINE
            if (metroLine.getStationNames().size() == 1) {
                // IF THE STATION TO ADD IS ON THE LEFT OF THE CLOSEST STATION
                if (c.findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(1).getEndX(), metroLine.getLineSegments().get(1).getEndY())
                        > c.findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(0).getStartX(), metroLine.getLineSegments().get(0).getStartY())) {
                    c.addStationToLeftEnd(label, metroLine, rightLine, station, 0, this, selectedLineName);
                } // IF THE STATION TO ADD IS ON THE RIGHT OF THE CLOSEST STATION
                else {
                    // CREATE RIGHT LINE SEGMENT
                    c.addStationToRightEnd(label, metroLine, rightLine, station, 0, this, selectedLineName);
                }
            } // IF THERE ARE MORE THAN ONE STATIONS ON THE LINE
            else {
                // INDEX OF THE CLOSEST STATION IS THE LAST STATION
                if (index == metroLine.getStations().size() - 1) {
                    // STATION IS ON THE LEFT OF THE LAST STATION
                    if (c.findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndX(), metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndY())
                            > c.findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getStations().get(index - 1).getCenterX(), metroLine.getStations().get(index - 1).getCenterY())) {
                        c.addStationBetweenTwoStations(metroLine, rightLine, station, metroLine.getStations().get(index), index, this, selectedLineName);
                    } // STATION IS ON THE RIGHT OF THE LAST STATION
                    else {
                        c.addStationToRightEnd(label, metroLine, rightLine, station, index, this, selectedLineName);
                    }
                } // INDEX OF THE CLOSEST STATION IS THE FIRST STATION
                else if (index == 0) {
                    // STATION IS ON THE LEFT OF THE FIRST STATION
                    if (c.findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getLineSegments().get(0).getStartX(), metroLine.getLineSegments().get(0).getStartY())
                            < c.findPythagorianDistance(station.getCenterX(), station.getCenterY(), metroLine.getStations().get(index + 1).getCenterX(), metroLine.getStations().get(index + 1).getCenterY())) {
                        c.addStationToLeftEnd(label, metroLine, rightLine, station, 0, this, selectedLineName);
                    } // STATION IS ON THE RIGHT OF THE FIRST STATION
                    else {
                        c.addStationBetweenTwoStations(metroLine, rightLine, station, metroLine.getStations().get(index + 1), index + 1, this, selectedLineName);
                    }
                } // INDEX OF THE CLOSEST STATION IS SOMEWHERE IN THE MIDDLE
                else {
                    c.addStationBetweenTwoStations(metroLine, rightLine, station, metroLine.getStations().get(index), index, this, selectedLineName);
                }
            }
        }
    }
//    int index = 0;
//                MetroLine selectedLine = null;
//                for (MetroLine line : lines) {
//                    if (line.getStations().contains(station)) {
//                        index = line.getStations().indexOf(station);
//                        selectedLine = line;
//                        break;
//                    }
//                }
//                DraggableText rightLabel=null;
//                for(Node node : nodes){
//                    if(node instanceof DraggableText && ((DraggableText)node).getText().equals(selectedLine.getId()) && ((DraggableText)node).getTextAlignment().equals(TextAlignment.LEFT) ){
//                        rightLabel = (DraggableText)node;
//                        break;
//                    }
//                }
//                
//                if (index + 1 == selectedLine.getLineSegments().size() - 1) {
//                    // BIND LEFT LINE TO RIGHT LABEL
//                    selectedLine.getLineSegments().get(index).endXProperty().bind(rightLabel.xProperty());
//                    selectedLine.getLineSegments().get(index).endYProperty().bind(rightLabel.yProperty());
//                    // UNBIND WITH RIGHT LABEL
//                    selectedLine.getLineSegments().get(index + 1).endXProperty().unbind();
//                    selectedLine.getLineSegments().get(index + 1).endYProperty().unbind();
//                    // REMOVE RIGHT LINE
//                    nodes.remove(selectedLine.getLineSegments().get(index + 1));
//                    selectedLine.getLineSegments().remove(selectedLine.getLineSegments().get(index + 1));
//                    // REMOVE STATION LABEL
//                    nodes.remove(label);
//                    // REMOBE STATION
//                    nodes.remove(station);
//                    selectedLine.getStationNames().remove(station.getId());
//                    selectedLine.getStations().remove(station);
//                }
}
