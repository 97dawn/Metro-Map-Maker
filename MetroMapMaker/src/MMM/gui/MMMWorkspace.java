package MMM.gui;

import MMM.MMMLanguageProperty;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import MMM.data.MMMData;
import djf.ui.AppYesNoCancelDialogSingleton;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppGUI;
import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static MMM.css.MMMStyle.*;
import MMM.data.DraggableEllipse;
import MMM.data.DraggableText;
import djf.settings.AppPropertyType;
import djf.settings.AppStartupConstants;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class MMMWorkspace extends AppWorkspaceComponent {

    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;

    // THIS IS WHERE WE'LL RENDER OUR DRAWING, NOTE THAT WE
    // CALL THIS A CANVAS, BUT IT'S REALLY JUST A Pane
    ScrollPane scroll;
    Pane canvas;
    Pane element;
    Pane grid;
    // HERE ARE THE CONTROLLERS
    MMMCanvasController canvasController;
    MMMEditController editController;

    // HAS ALL THE CONTROLS FOR EDITING
    VBox editToolbar;

    // FIRST ROW
    VBox row1;
    HBox row11;
    Label metroLinesText;
    ComboBox metroLines;
    Button editLine;
    HBox row12;
    Button addLine;
    Button removeLine;
    ToggleButton addStationToLine;
    ToggleButton removeStationFromLine;
    Button list;
    HBox row13;
    Slider lineThickness;

    // SECOND ROW
    VBox row2;
    HBox row21;
    Label metroStationsText;
    ComboBox metroStations;
    ColorPicker metroStationColor;
    HBox row22;
    Button addStation;
    Button removeStation;
    Button snap;
    Button moveLabel;
    Button rotateLabel;
    HBox row23;
    Slider radius;

    // THIRD ROW
    HBox row3;
    VBox row31;
    ComboBox departure;
    ComboBox arrival;
    VBox row32;
    Button route;

    // FORTH ROW
    VBox row4;
    HBox row41;
    Label decor;
    ColorPicker backgroundColor;
    HBox row42;
    Button setImageBackground;
    Button addImage;
    Button addLabel;
    Button removeElement;

    // FIFTH ROW
    VBox row5;
    HBox row51;
    Label font;
    ColorPicker fontColor;
    HBox row52;
    ToggleButton bold;
    ToggleButton italic;
    ComboBox size;
    ComboBox family;

    // SIXTH ROW
    VBox row6;
    HBox row61;
    Label navigation;
    CheckBox showGrid;
    Label showGridText;
    HBox row62;
    Button zoomIn;
    Button zoomOut;
    Button increaseMapSize;
    Button decreaseMapSize;

    Button saveAs;
    Button export;
    Button undo;
    Button redo;
    Button about;

    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    MMMData data;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public MMMWorkspace(AppTemplate initApp) {
        // KEEP THIS FOR LATER
        app = initApp;

        // KEEP THE GUI FOR LATER
        gui = app.getGUI();

        // LAYOUT THE APP
        initLayout();

        // HOOK UP THE CONTROLLERS
        initControllers();

        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();

    }
//    
//    // ACCESSOR METHODS FOR COMPONENTS THAT EVENT HANDLERS
//    // MAY NEED TO UPDATE OR ACCESS DATA FROM
//    
//    public ColorPicker getFillColorPicker() {
//	return fillColorPicker;
//    }
//    
//    public ColorPicker getOutlineColorPicker() {
//	return outlineColorPicker;
//    }
//    

    public CheckBox getShowGrid() {
        return showGrid;
    }

    public Pane getCanvas() {
        return canvas;
    }

    public ScrollPane getScroll() {
        return scroll;
    }

    public ColorPicker getFontColor() {
        return fontColor;
    }

    public Slider getRadius() {
        return radius;
    }

    public Button getSaveAs() {
        return saveAs;
    }

    public Button getExport() {
        return export;
    }

    public ColorPicker getBackgroundColorPicker() {
        return backgroundColor;
    }

    public ToggleButton getAddStationToLine() {
        return addStationToLine;
    }

    public ToggleButton getRemoveStationFromLine() {
        return removeStationFromLine;
    }

    public ComboBox getDeparture() {
        return departure;
    }

    public ComboBox getArrival() {
        return arrival;
    }

    public ComboBox getMetroLines() {
        return metroLines;
    }

    public ComboBox getMetroStations() {
        return metroStations;
    }

    public Pane getGrid() {
        return grid;
    }

    public Pane getElement() {
        return element;
    }

    public ColorPicker getMetroStationColorPicker() {
        return metroStationColor;
    }

    public Slider getLineThickness() {
        return lineThickness;
    }

    public ComboBox getFamily() {
        return family;
    }

    public ComboBox getSize() {
        return size;
    }

    public ToggleButton getBold() {
        return bold;
    }

    public ToggleButton getItalic() {
        return italic;
    }

    // HELPER SETUP METHOD
    private void initLayout() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        // THIS WILL GO IN THE LEFT SIDE OF THE WORKSPACE
        editToolbar = new VBox();

        // ROW 1
        row1 = new VBox();
        row11 = new HBox();
        row11.setSpacing(10);
        metroLinesText = new Label("Metro Lines");
        metroLines = new ComboBox();
        metroLines.setPromptText("Metro Line");
        editLine = new Button("Edit Line");
        editLine.setTooltip(new Tooltip("Edit line properties"));
        row11.getChildren().addAll(metroLinesText, metroLines, editLine);
        row12 = new HBox();
        row12.setSpacing(10);
        addLine = new Button("+");
        addLine.setTooltip(new Tooltip("Add new line"));
        removeLine = new Button("-");
        removeLine.setTooltip(new Tooltip("Remove line"));
        addStationToLine = new ToggleButton("Add Station");
        addStationToLine.setTooltip(new Tooltip("Add station to line"));
        removeStationFromLine = new ToggleButton("Remove Station");
        removeStationFromLine.setTooltip(new Tooltip("Remove station from line"));
        row12.getChildren().addAll(addLine, removeLine, addStationToLine, removeStationFromLine);
        list = gui.initChildButton(row12, MMMLanguageProperty.LIST_ICON.toString(), MMMLanguageProperty.LIST_TOOLTIP.toString(), false);
        lineThickness = new Slider(1, 10, 3);
        row1.getChildren().addAll(row11, row12, lineThickness);

        // ROW 2
        row2 = new VBox();
        row21 = new HBox();
        row21.setSpacing(10);
        metroStationsText = new Label("Metro Stations");
        metroStations = new ComboBox();
        metroStations.setPromptText("Metro Station");
        metroStationColor = new ColorPicker(Color.BLACK);
        row21.getChildren().addAll(metroStationsText, metroStations, metroStationColor);
        row22 = new HBox();
        row22.setSpacing(10);
        addStation = new Button("+");
        addStation.setTooltip(new Tooltip("Add new station"));
        removeStation = new Button("-");
        removeStation.setTooltip(new Tooltip("Remove station"));
        snap = new Button("Snap");
        snap.setTooltip(new Tooltip("Snap"));
        moveLabel = new Button("Move Label");
        moveLabel.setTooltip(new Tooltip("Move Label"));
        row22.getChildren().addAll(addStation, removeStation, snap, moveLabel);
        rotateLabel = gui.initChildButton(row22, MMMLanguageProperty.ROTATE_ICON.toString(), MMMLanguageProperty.ROTATE_TOOLTIP.toString(), false);
        radius = new Slider(10, 20, 10);
        row2.getChildren().addAll(row21, row22, radius);

        // ROW 3
        row3 = new HBox();
        row31 = new VBox();
        row31.setSpacing(10);
        departure = new ComboBox();
        departure.setPromptText("Departure");
        departure.setPrefWidth(200);
        arrival = new ComboBox();
        arrival.setPromptText("Arrival");
        arrival.setPrefWidth(200);
        row31.getChildren().addAll(departure, arrival);
        row32 = new VBox();
        row32.setSpacing(10);
        route = gui.initChildButton(row32, MMMLanguageProperty.ROUTE_ICON.toString(), MMMLanguageProperty.ROUTE_TOOLTIP.toString(), false);
        row3.getChildren().addAll(row31, row32);

        // ROW 4
        row4 = new VBox();
        row41 = new HBox();
        row41.setSpacing(10);
        decor = new Label("Decor");
        backgroundColor = new ColorPicker(Color.WHITE);
        row41.getChildren().addAll(decor, backgroundColor);
        row42 = new HBox();
        row42.setSpacing(10);
        setImageBackground = new Button("Set Image Background");
        addImage = new Button("Add Image Overlay");
        addLabel = new Button("Add Label");
        removeElement = new Button("Remove Element");
        row42.getChildren().addAll(setImageBackground, addImage, addLabel, removeElement);
        row4.getChildren().addAll(row41, row42);

        // ROW 5
        row5 = new VBox();
        row51 = new HBox();
        row51.setSpacing(10);
        font = new Label("Font");
        fontColor = new ColorPicker(Color.BLACK);
        row51.getChildren().addAll(font, fontColor);
        row52 = new HBox();
        row52.setSpacing(10);
        bold = new ToggleButton();
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(MMMLanguageProperty.BOLD_ICON.toString());
        Image buttonImage = new Image(imagePath);
        bold.setGraphic(new ImageView(buttonImage));
        bold.setDisable(false);
        bold.setTooltip(new Tooltip("Bold"));
        italic = new ToggleButton();
        italic.setTooltip(new Tooltip("Italicize"));
        imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(MMMLanguageProperty.ITALIC_ICON.toString());
        buttonImage = new Image(imagePath);
        italic.setGraphic(new ImageView(buttonImage));
        italic.setDisable(false);
        size = new ComboBox();
        size.setPromptText("Font Size");
        size.getItems().addAll("10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
        family = new ComboBox();
        family.setPromptText("Font Family");
        family.getItems().addAll("System", "Times New Roman", "Arial", "Georgia", "Calibri", "Verdana");
        row52.getChildren().addAll(bold, italic, size, family);
        row5.getChildren().addAll(row51, row52);

        // ROW 6
        row6 = new VBox();
        row61 = new HBox();
        row61.setSpacing(10);
        navigation = new Label("Navigation");
        showGrid = new CheckBox("Show Grid");
        row61.getChildren().addAll(navigation, showGrid);
        row62 = new HBox();
        row62.setSpacing(10);
        zoomIn = gui.initChildButton(row62, MMMLanguageProperty.ZOOMIN_ICON.toString(), MMMLanguageProperty.ZOOMIN_TOOLTIP.toString(), false);
        zoomOut = gui.initChildButton(row62, MMMLanguageProperty.ZOOMOUT_ICON.toString(), MMMLanguageProperty.ZOOMOUT_TOOLTIP.toString(), false);
        decreaseMapSize = gui.initChildButton(row62, MMMLanguageProperty.DECREASE_ICON.toString(), MMMLanguageProperty.DECREASE_TOOLTIP.toString(), false);
        increaseMapSize = gui.initChildButton(row62, MMMLanguageProperty.INCREASE_ICON.toString(), MMMLanguageProperty.INCREASE_TOOLTIP.toString(), false);
        row6.getChildren().addAll(row61, row62);

        // NOW ORGANIZE THE EDIT TOOLBAR
        editToolbar.getChildren().add(row1);
        editToolbar.getChildren().add(row2);
        editToolbar.getChildren().add(row3);
        editToolbar.getChildren().add(row4);
        editToolbar.getChildren().add(row5);
        editToolbar.getChildren().add(row6);

        FlowPane fileToolbar = app.getGUI().getFileToolbar();
        saveAs = gui.initChildButton(fileToolbar, MMMLanguageProperty.SAVEAS_ICON.toString(), MMMLanguageProperty.SAVEAS_TOOLTIP.toString(), false);
        export = gui.initChildButton(fileToolbar, MMMLanguageProperty.EXPORT_ICON.toString(), MMMLanguageProperty.EXPORT_TOOLTIP.toString(), false);
        undo = gui.initChildButton(fileToolbar, MMMLanguageProperty.UNDO_ICON.toString(), MMMLanguageProperty.UNDO_TOOLTIP.toString(), true);
        redo = gui.initChildButton(fileToolbar, MMMLanguageProperty.REDO_ICON.toString(), MMMLanguageProperty.REDO_TOOLTIP.toString(), true);
        about = gui.initChildButton(fileToolbar, MMMLanguageProperty.ABOUT_ICON.toString(), MMMLanguageProperty.ABOUT_TOOLTIP.toString(), false);
        // WE'LL RENDER OUR STUFF HERE IN THE CANVAS
        canvas = new StackPane();
        grid = new Pane();
        element = new Pane();
        grid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        app.getGUI().getPrimaryScene().setFill(null);
        canvas.getChildren().addAll(grid, element);
        scroll = new ScrollPane(canvas);
        element.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        canvas.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        scroll.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        grid.resize(scroll.getWidth(), scroll.getHeight());
        element.resize(scroll.getWidth(), scroll.getHeight());
        canvas.resize(scroll.getWidth(), scroll.getHeight());
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // AND MAKE SURE THE DATA MANAGER IS IN SYNCH WITH THE PANE
        data = (MMMData) app.getDataComponent();
        data.setNodes(element.getChildren());

        // AND NOW SETUP THE WORKSPACE
        workspace = new BorderPane();
        ((BorderPane) workspace).setLeft(editToolbar);
        ((BorderPane) workspace).setCenter(scroll);

    }

    // HELPER SETUP METHOD
    private void initControllers() {
        // MAKE THE EDIT CONTROLLER
        editController = new MMMEditController(app);

        editLine.setOnAction(e -> {
            editController.handleEditLine();
        });
        addLine.setOnAction(e -> {
            editController.handleAddLine();
        });
        removeLine.setOnAction(e -> {
            editController.handleRemoveLine();
        });
        addStationToLine.setOnAction(e -> {
            addStationToLine.setSelected(true);
            editController.handleAddStationToLine();
        });
        removeStationFromLine.setOnAction(e -> {
            editController.handleRemoveStationFromLine();
        });
        list.setOnAction(e -> {
            editController.handleShowList();
        });
        lineThickness.setOnMouseReleased(e -> {
            editController.handleChangeLineThickness();
        });
        metroStationColor.setOnAction(e -> {
            editController.handleMetroStationColorPicker();
        });
        addStation.setOnAction(e -> {
            editController.handleAddStation();
        });
        removeStation.setOnAction(e -> {
            editController.handleRemoveStation();
        });
        snap.setOnAction(e -> {
            editController.handleSnap();
        });
        moveLabel.setOnAction(e -> {
            editController.handleMoveLabel();
        });
        rotateLabel.setOnAction(e -> {
            editController.handleRotateLabel();
        });
        radius.setOnMouseReleased(e -> {
            editController.handleChangeRadius();
        });
        route.setOnAction(e -> {
            editController.handleFindShortestPath();
        });
        backgroundColor.setOnAction(e -> {
            editController.handleBackgroundColorPicker();
        });
        setImageBackground.setOnAction(e -> {
            editController.handleSetImageBackground();
        });
        addImage.setOnAction(e -> {
            editController.handleAddImage();
        });
        addLabel.setOnAction(e -> {
            editController.handleAddLabel();
        });
        removeElement.setOnAction(e -> {
            editController.handleRemoveElement();
        });
        fontColor.setOnAction(e -> {
            if (fontColor.isFocused()) {
                editController.handleTextColorPicker();
            }
        });
        bold.setOnAction(e -> {
            if (bold.isFocused()) {
                editController.handleSetBold();

            }
        });
        italic.setOnAction(e -> {
            if (italic.isFocused()) {
                editController.handleSetItalic();
            }
        });
        size.setOnAction(e -> {
            if (size.isFocused()) {
                editController.handleChangeFontSize();
            }
        });
        family.setOnAction(e -> {
            if (family.isFocused()) {
                editController.handleChangeFontFamily();
            }
        });
        showGrid.setOnAction(e -> {
            editController.handleShowGrid();
        });
        zoomIn.setOnAction(e -> {
            editController.handleZoomIn();
        });
        zoomOut.setOnAction(e -> {
            editController.handleZoomOut();
        });
        increaseMapSize.setOnAction(e -> {
            editController.handleIncreaseMapSize();
        });
        decreaseMapSize.setOnAction(e -> {
            editController.handleDecreaseMapSize();
        });
        undo.setOnAction(e -> {
            editController.handleUndo();
        });
        redo.setOnAction(e -> {
            editController.handleRedo();
        });
        about.setOnAction(e -> {
            editController.handleLearnAbout();
        });
        saveAs.setOnAction(e -> {
            Stage promptStage = new Stage();
            TextField textField = new TextField();
            Button ok = new Button("OK");
            Button close = new Button("Close");
            HBox buttons = new HBox(ok, close);
            VBox pane = new VBox(textField, buttons);
            pane.setPadding(new Insets(60, 80, 60, 80));
            File work = new File(AppStartupConstants.PATH_WORK);
            File[] dirs = work.listFiles();
            ArrayList<String> fileNames = new ArrayList<>();
            for (File f : dirs) {
                fileNames.add(f.getName().replace(" Metro.json", ""));
            }
            ok.setOnAction(a -> {
                if (textField.getText() != null && !textField.getText().isEmpty()) {
                    if (fileNames.contains(textField.getText())) {
                        Stage errorStage = new Stage();
                        errorStage.setTitle("Existing map name");
                        Label label = new Label(textField.getText() + " already exists. Change the name.");
                        Scene errorScene = new Scene(label);
                        label.setPadding(new Insets(60, 80, 60, 80));
                        errorStage.setScene(errorScene);
                        errorStage.show();
                    } else { //CREATE NEW FILE
                        promptStage.close();
                        try {
                            Path path = FileSystems.getDefault().getPath(AppStartupConstants.PATH_EXPORT + "/" + textField.getText());
                            Files.createDirectory(path);
                            File newFile = new File(AppStartupConstants.PATH_WORK + "/" + textField.getText() + " Metro.json");
                            app.getGUI().getFileController().saveWork(newFile);
                            app.getGUI().getFileController().setCurrentWorkFile(newFile);
                            saveAs.setDisable(true);
                        } catch (Exception ex) {
                            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                            dialog.show("Error in Save As", "Error in Save As");
                        }
                    }
                } else {
                    Stage errorStage = new Stage();
                    errorStage.setTitle("No map name");
                    Label label = new Label("You didn't enter the name of the map");
                    Scene errorScene = new Scene(label);
                    label.setPadding(new Insets(60, 80, 60, 80));
                    errorStage.setScene(errorScene);
                    errorStage.show();
                }
            });
            close.setOnAction(a -> {
                promptStage.close();
            });
            Scene scene = new Scene(pane);
            promptStage.setTitle("Enter the map name");
            promptStage.setScene(scene);
            promptStage.show();
            //app.getGUI().getFileController().handleSaveAsRequest();
        });
        export.setOnAction(e -> {
            try {
                String fileName = app.getGUI().getWindow().getTitle().replace("Metro Map Maker - ", "");
                File newFile = new File(AppStartupConstants.PATH_EXPORT + "/" + fileName + "/" + fileName + " Metro.json");
                app.getFileComponent().exportData(data, newFile.getPath());
                editController.processSnapshot();
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show("Export Done", "Exporting the map image and the map data has been successfully completed.");
            } catch (Exception ex) {
                PropertiesManager props = PropertiesManager.getPropertiesManager();
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(AppPropertyType.EXPORT_ERROR_TITLE), props.getProperty(AppPropertyType.EXPORT_ERROR_MESSAGE));
            }
        });
        // MAKE THE CANVAS CONTROLLER	
        canvasController = new MMMCanvasController(app);
        canvas.setOnMousePressed(e -> {
            canvasController.processCanvasMousePress((int) e.getX(), (int) e.getY());
        });
        canvas.setOnMouseReleased(e -> {
            canvasController.processCanvasMouseRelease((int) e.getX(), (int) e.getY());
        });
        canvas.setOnMouseDragged(e -> {
            canvasController.processCanvasMouseDragged((int) e.getX(), (int) e.getY());
        });
        app.getGUI().getPrimaryScene().setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.W)) {
                scroll.getContent().setTranslateY(scroll.getContent().getTranslateY() + 1);
            } else if (e.getCode().equals(KeyCode.A)) {
                scroll.getContent().setTranslateX(scroll.getContent().getTranslateX() + 1);
            } else if (e.getCode().equals(KeyCode.S)) {
                scroll.getContent().setTranslateY(scroll.getContent().getTranslateY() - 1);
            } else if (e.getCode().equals(KeyCode.D)) {
                scroll.getContent().setTranslateX(scroll.getContent().getTranslateX() - 1);
            }
        });
        app.getGUI().getWindow().setOnCloseRequest(eh -> {
            app.getGUI().getFileController().handleExitRequest();
        });
    }

    public void loadSelectedNodeSettings(Node node) {
        if (node != null) {
            if (node instanceof Line) {
                metroLines.setValue(node.getId());
                lineThickness.setValue(((Line) node).getStrokeWidth());
            } else if (node instanceof DraggableText) {
                fontColor.setValue((Color) ((DraggableText) node).getFill());
                family.setValue(((DraggableText) node).getFont().getFamily());
                for (Object s : size.getItems()) {
                    if (Double.parseDouble(s.toString()) == (((DraggableText) node).getFont().getSize())) {
                        size.setValue(s);
                        break;
                    }
                }
                if (((DraggableText) node).getFont().getStyle().contains("Bold")) {
                    bold.setSelected(true);
                } else {
                    bold.setSelected(false);
                }
                if (((DraggableText) node).getFont().getStyle().contains("Italic")) {
                    italic.setSelected(true);
                } else {
                    italic.setSelected(false);
                }
            } else if (node instanceof DraggableEllipse) {
                metroStationColor.setValue((Color) ((DraggableEllipse) node).getFill());
                metroStations.setValue(node.getId());
                radius.setValue(((DraggableEllipse) node).getRadiusX());
                for (Node n : data.getNodes()) {
                    if (n instanceof DraggableText && ((DraggableText) n).getText().equals(node.getId())) {
                        fontColor.setValue((Color) ((DraggableText) n).getFill());
                        for (Object fam : family.getItems()) {
                            if (fam.toString().equals(((DraggableText) n).getFont().getFamily())) {
                                family.setValue(fam);
                                break;
                            }
                        }
                        for (Object s : size.getItems()) {
                            if (Double.parseDouble(s.toString()) == (((DraggableText) n).getFont().getSize())) {
                                size.setValue(s);
                                break;
                            }
                        }
                        if (((DraggableText) n).getFont().getStyle().contains("Bold")) {
                            bold.setSelected(true);
                        } else {
                            bold.setSelected(false);
                        }
                        if (((DraggableText) n).getFont().getStyle().contains("Italic")) {
                            italic.setSelected(true);
                        } else {
                            italic.setSelected(false);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    public void initStyle() {
        // NOTE THAT EACH CLASS SHOULD CORRESPOND TO
        // A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
        // CSS FILE
        canvas.getStyleClass().add(CLASS_RENDER_CANVAS);

        // COLOR PICKER STYLE
        editLine.getStyleClass().add(CLASS_BUTTON);
        metroStationColor.getStyleClass().add(CLASS_BUTTON);
        backgroundColor.getStyleClass().add(CLASS_BUTTON);
        fontColor.getStyleClass().add(CLASS_BUTTON);

        editToolbar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
        row1.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row2.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row3.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row4.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row5.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row6.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        metroLinesText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        metroStationsText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        decor.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        font.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        navigation.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
    }

    /**
     * This function reloads all the controls for editing logos the workspace.
     */
    @Override
    public void reloadWorkspace(AppDataComponent data) {
        // RESET DEPARTURE COMBOBOX
        departure.getItems().clear();
        for (Object item : metroStations.getItems()) {
            departure.getItems().add(item);
        }
        // RESET ARRIVAL COMMBOBOX
        arrival.getItems().clear();
        for (Object item : metroStations.getItems()) {
            arrival.getItems().add(item);
        }
        saveAs.setDisable(false);
        export.setDisable(false);
        app.getGUI().updateToolbarControls(false);
        if (app.getJTPS().isTheMostRecentTransaction() && app.getJTPS().getMostRecentTransaction() != -1 && app.getJTPS().getTransactionsSize() > 0) {
            undo.setDisable(false);
            redo.setDisable(true);
        } else if (app.getJTPS().getTransactionsSize() == 0) {
            undo.setDisable(true);
            redo.setDisable(true);
        } else if (app.getJTPS().getMostRecentTransaction() == -1 && app.getJTPS().getTransactionsSize() > 0) {
            undo.setDisable(true);
            redo.setDisable(false);
        } else {
            undo.setDisable(false);
            redo.setDisable(false);
        }

        app.getGUI().getFileController().markAsEdited(gui);
    }

    @Override
    public void resetWorkspace() {
        undo.setDisable(true);
        redo.setDisable(true);
        saveAs.setDisable(false);
        export.setDisable(false);
    }
}
