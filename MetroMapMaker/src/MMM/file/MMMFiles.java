package MMM.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import MMM.data.MMMData;
import MMM.data.DraggableEllipse;
import MMM.data.DraggableImage;
import MMM.data.DraggableText;
import MMM.data.MetroLine;
import MMM.gui.MMMWorkspace;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.APP_TITLE;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import properties_manager.PropertiesManager;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class MMMFiles implements AppFileComponent {

    // FOR JSON LOADING
    static final String JSON_BG_COLOR = "background_color";
    static final String JSON_BG_IMAGE = "background_image";
    static final String JSON_COLOR = "color";
    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    static final String JSON_NAME = "name";

    static final String JSON_LINES = "lines";
    static final String JSON_CIRCULAR = "circular";
    static final String JSON_LINE_THICKNESS = "line_thickness";
    static final String JSON_START_X = "start_x";
    static final String JSON_START_Y = "start_y";
    static final String JSON_END_X = "end_x";
    static final String JSON_END_Y = "end_y";
    static final String JSON_LINE_SEG = "line_segments";
    static final String JSON_STATION_NAMES = "station_names";

    static final String JSON_STATIONS = "stations";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_RADIUS = "radius";
    static final String JSON_LINE_NAMES = "line_names";
    static final String JSON_ROTATION = "rotation";

    static final String JSON_IMAGES = "images";
    static final String JSON_IMAGE = "image";
    static final String JSON_TEXTS = "texts";
    static final String JSON_FONT_FAM = "font_fam";
    static final String JSON_FONT_SIZE = "font_size";
    static final String JSON_BOLD = "bold";
    static final String JSON_ITALIC = "italic";
    static final String JSON_TEXT = "text";
    static final String JSON_ALIGNMENT = "text_alignment";
    static final String JSON_OPACITY = "opacity";
    static final String JSON_WIDTH = "width";
    static final String JSON_HEIGHT = "height";

    static final String DEFAULT_DOCTYPE_DECLARATION = "<!doctype html>\n";
    static final String DEFAULT_ATTRIBUTE_VALUE = "";

    /**
     * This method is provided to satisfy the compiler, but it is not used by
     * this application.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        MMMData dataManager = (MMMData) data;
        // FIND CITY NAME
        String cityName = filePath.substring(filePath.lastIndexOf('\\') + 1);
        cityName = cityName.replace(" Metro.json", "");

        // NOW BUILD THE JSON OBJCTS TO SAVE
        JsonArrayBuilder linesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder stationsBuilder = Json.createArrayBuilder();
        for (MetroLine line : dataManager.getLines()) {
            // BUILD STATION NAMES
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            JsonArray stations;
            for (String s : line.getStationNames()) {
                arrayBuilder.add(s);
            }
            stations = arrayBuilder.build();
            JsonObject color = makeJsonColorObject((Color) line.getLineSegments().get(0).getStroke());
            JsonObject lineJson = Json.createObjectBuilder()
                    .add(JSON_NAME, line.getId())
                    .add(JSON_CIRCULAR, line.getIsCircular())
                    .add(JSON_COLOR, color)
                    .add(JSON_STATION_NAMES, stations).build();
            linesBuilder.add(lineJson);
            for (DraggableEllipse station : line.getStations()) {
                if (station.getLines().indexOf(line.getId()) == 0) {
                    JsonObject stationJson = Json.createObjectBuilder()
                            .add(JSON_NAME, station.getId())
                            .add(JSON_X, station.getX())
                            .add(JSON_Y, station.getY()).build();
                    stationsBuilder.add(stationJson);
                }
            }
        }
//         for (Node node : dataManager.getNodes()) {
//                if (node instanceof DraggableEllipse) {
//                    JsonObject stationJson = Json.createObjectBuilder()
//                            .add(JSON_NAME, node.getId())
//                            .add(JSON_X, ((DraggableEllipse)node).getX())
//                            .add(JSON_Y, ((DraggableEllipse)node).getY()).build();
//                    stationsBuilder.add(stationJson);
//                }
//            }
        JsonArray linesArray = linesBuilder.build();
        JsonArray stationsArray = stationsBuilder.build();
        // THEN PUT IT ALL TOGETHER IN A JsonObject
        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add(JSON_NAME, cityName)
                .add(JSON_LINES, linesArray)
                .add(JSON_STATIONS, stationsArray).build();
        // AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(dataManagerJSO);
        jsonWriter.close();

        // INIT THE WRITER
        OutputStream os = new FileOutputStream(filePath);
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(dataManagerJSO);
        String prettyPrinted = sw.toString();
        PrintWriter pw = new PrintWriter(filePath);
        pw.write(prettyPrinted);
        pw.close();
    }

    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     *
     * @param data The data management component for this application.
     *
     * @param filePath Path (including file name/extension) to where to save the
     * data to.
     *
     * @throws IOException Thrown should there be an error writing out data to
     * the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        // GET THE DATA
        MMMData dataManager = (MMMData) data;

        // FIRST THE BACKGROUND COLOR
        Color bgColor = dataManager.getBackgroundColor();
        JsonObject bgColorJson = makeJsonColorObject(bgColor);
        ImageView bgImage = dataManager.getBackgroundImage();
        JsonObject bgImageJson = Json.createObjectBuilder().build();
        if (bgImage != null) {
            Image image = bgImage.getImage();
            String path = image.impl_getUrl();
            JsonObject imageJson = Json.createObjectBuilder()
                    .add(JSON_X, bgImage.getX())
                    .add(JSON_Y, bgImage.getY())
                    .add(JSON_OPACITY, bgImage.getOpacity())
                    .add(JSON_IMAGE, path).build();
            bgImageJson = imageJson;
        }

        // THEN CITY NAME
        String cityName = filePath.substring(filePath.lastIndexOf('\\') + 1);
        cityName = cityName.replace(" Metro.json", "");

        // NOW BUILD THE JSON OBJCTS TO SAVE
        JsonArrayBuilder linesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder stationsBuilder = Json.createArrayBuilder();
        JsonArrayBuilder imagesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder textsBuilder = Json.createArrayBuilder();
        ObservableList<Node> nodes = dataManager.getNodes();
        ArrayList<MetroLine> metroLines = dataManager.getLines();
        for (MetroLine line : metroLines) {
            JsonObject lineJson = makeJsonLineObject(line);
            linesBuilder.add(lineJson);

            for (DraggableEllipse station : line.getStations()) {
                if (station.getLines().indexOf(line.getId()) == 0) {
                    JsonObject stationJson = makeJsonStationObject(station);
                    stationsBuilder.add(stationJson);
                }
            }
        }
        for (Node node : nodes) {
            if (node instanceof Line) {
                continue;
//                JsonObject lineJson = makeJsonLineObject((MetroLine) node);
//                linesBuilder.add(lineJson);
            } else if (node instanceof DraggableEllipse) {
                if (((DraggableEllipse) node).getLines().size() == 0) {
                    JsonObject stationJson = makeJsonStationObject((DraggableEllipse) node);
                    stationsBuilder.add(stationJson);
                }
            } else if (node instanceof DraggableImage) {
                JsonObject imageJson = makeJsonImageObject((DraggableImage) node);
                imagesBuilder.add(imageJson);
            } else if (node instanceof DraggableText) {
                JsonObject textJson = makeJsonTextObject((DraggableText) node);
                textsBuilder.add(textJson);
            }
        }
        JsonArray linesArray = linesBuilder.build();
        JsonArray stationsArray = stationsBuilder.build();
        JsonArray imagesArray = imagesBuilder.build();
        JsonArray textsArray = textsBuilder.build();

        //JsonArray shapesArray = arrayBuilder.build();
        // THEN PUT IT ALL TOGETHER IN A JsonObject
        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add(JSON_NAME, cityName)
                .add(JSON_BG_COLOR, bgColorJson)
                .add(JSON_BG_IMAGE, bgImageJson)
                .add(JSON_LINES, linesArray)
                .add(JSON_STATIONS, stationsArray)
                .add(JSON_IMAGES, imagesArray)
                .add(JSON_TEXTS, textsArray)
                .build();

        // AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(dataManagerJSO);
        jsonWriter.close();

        // INIT THE WRITER
        OutputStream os = new FileOutputStream(filePath);
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(dataManagerJSO);
        String prettyPrinted = sw.toString();
        PrintWriter pw = new PrintWriter(filePath);
        pw.write(prettyPrinted);
        pw.close();
    }

    private JsonObject makeJsonStationObject(DraggableEllipse s) {
        JsonObject color = makeJsonColorObject((Color) s.getFill());
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (String n : s.getLines()) {
            builder.add(n);
        }
        JsonArray lines = builder.build();
        JsonObject stationJson = Json.createObjectBuilder()
                .add(JSON_NAME, s.getId())
                .add(JSON_X, s.getCenterX())
                .add(JSON_Y, s.getCenterY())
                .add(JSON_COLOR, color)
                .add(JSON_LINE_NAMES, lines)
                .add(JSON_RADIUS, s.getRadiusX()).build();
        return stationJson;
    }

    private JsonObject makeJsonTextObject(DraggableText t) {
        JsonObject color = makeJsonColorObject((Color) t.getFill());
        JsonObject textJson = Json.createObjectBuilder()
                .add(JSON_X, t.getX())
                .add(JSON_Y, t.getY())
                .add(JSON_FONT_FAM, t.getFont().getFamily())
                .add(JSON_FONT_SIZE, t.getFont().getSize())
                .add(JSON_BOLD, t.getFont().getStyle().contains("Bold"))
                .add(JSON_ITALIC, t.getFont().getStyle().contains("Italic"))
                .add(JSON_TEXT, t.getText())
                .add(JSON_COLOR, color)
                .add(JSON_ALIGNMENT, t.getTextAlignment().toString())
                .add(JSON_ROTATION, t.getRotate())
                .build();
        return textJson;
    }

    private JsonObject makeJsonImageObject(DraggableImage i) {
        Image image = i.getImage();
        String path = image.impl_getUrl();
        JsonObject imageJson = Json.createObjectBuilder()
                .add(JSON_X, i.getX())
                .add(JSON_Y, i.getY())
                .add(JSON_IMAGE, path).build();
        return imageJson;
    }

    private JsonObject makeJsonLineObject(MetroLine l) {
        JsonObject color = makeJsonColorObject((Color) l.getLineSegments().get(0).getStroke());
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonArray stations;
        for (String s : l.getStationNames()) {
            arrayBuilder.add(s);
        }
        JsonArrayBuilder lineSegArrayBuilder = Json.createArrayBuilder();
        for (Line seg : l.getLineSegments()) {
            JsonObject xy = Json.createObjectBuilder()
                    .add(JSON_START_X, seg.getStartX())
                    .add(JSON_START_Y, seg.getStartY())
                    .add(JSON_END_X, seg.getEndX())
                    .add(JSON_END_Y, seg.getEndY())
                    .build();
            lineSegArrayBuilder.add(xy);
        }
        JsonArray lineSegs = lineSegArrayBuilder.build();
        stations = arrayBuilder.build();
        JsonObject lineJson = Json.createObjectBuilder()
                .add(JSON_NAME, l.getId())
                .add(JSON_CIRCULAR, l.getIsCircular())
                .add(JSON_LINE_THICKNESS, l.getLineSegments().get(0).getStrokeWidth())
                .add(JSON_LINE_SEG, lineSegs)
                .add(JSON_COLOR, color)
                .add(JSON_STATION_NAMES, stations).build();
        return lineJson;
    }

    private JsonObject makeJsonColorObject(Color color) {
        JsonObject colorJson = Json.createObjectBuilder()
                .add(JSON_RED, color.getRed())
                .add(JSON_GREEN, color.getGreen())
                .add(JSON_BLUE, color.getBlue())
                .add(JSON_ALPHA, color.getOpacity()).build();
        return colorJson;
    }

    /**
     * This method loads data from a JSON formatted file into the data
     * management component and then forces the updating of the workspace such
     * that the user may edit the data.
     *
     * @param data Data management component where we'll load the file into.
     *
     * @param filePath Path (including file name/extension) to where to load the
     * data from.
     *
     * @throws IOException Thrown should there be an error reading in data from
     * the file.
     */
    @Override
    public void loadData(AppDataComponent data, String filePath, AppTemplate app) throws IOException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        // CLEAR THE OLD DATA OUT
        MMMData dataManager = (MMMData) data;
        dataManager.resetData();

        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(filePath);

        // LOAD THE CITY NAME
        String cityName = filePath.substring(filePath.lastIndexOf('\\') + 1);
        cityName = cityName.replace(" Metro.json", "");
        app.getGUI().getWindow().setTitle(props.getProperty(APP_TITLE) + " - " + cityName);

        // LOAD THE BACKGROUND COLOR
        Color bgColor = loadColor(json, JSON_BG_COLOR);
        dataManager.setBackgroundColor(bgColor);

        // LOAD THE BACKGROUND IMAGE
        ImageView bgImage = loadBackgroundImage(json, JSON_BG_IMAGE);
        if (bgImage != null) {
            dataManager.setBackgroundImage(bgImage);
        }
        // LOAD ALL THE LINES
        JsonArray jsonLinesArray = json.getJsonArray(JSON_LINES);
        for (int i = 0; i < jsonLinesArray.size(); i++) {
            JsonObject jsonLine = jsonLinesArray.getJsonObject(i);
            MetroLine line = loadLine(jsonLine, dataManager);
            ((MMMWorkspace) app.getWorkspaceComponent()).getMetroLines().getItems().add(line.getId().replaceAll("\"", ""));
            dataManager.getLines().add(line);
        }
        // LOAD ALL THE STATIONS
        JsonArray jsonStationsArray = json.getJsonArray(JSON_STATIONS);
        for (int i = 0; i < jsonStationsArray.size(); i++) {
            JsonObject jsonShape = jsonStationsArray.getJsonObject(i);
            DraggableEllipse station = loadStation(jsonShape, dataManager);
            ((MMMWorkspace) app.getWorkspaceComponent()).getMetroStations().getItems().add(station.getId().replaceAll("\"", ""));
            ((MMMWorkspace) app.getWorkspaceComponent()).getDeparture().getItems().add(station.getId().replaceAll("\"", ""));
            ((MMMWorkspace) app.getWorkspaceComponent()).getArrival().getItems().add(station.getId().replaceAll("\"", ""));
            dataManager.getNodes().add(station);
        }

        // LOAD ALL THE IMAGES
        JsonArray jsonImagesArray = json.getJsonArray(JSON_IMAGES);
        for (int i = 0; i < jsonImagesArray.size(); i++) {
            JsonObject jsonLine = jsonImagesArray.getJsonObject(i);
            DraggableImage image = loadDraggableImage(jsonLine);
            dataManager.getNodes().add(image);
        }
        // LOAD ALL THE TEXTS
        JsonArray jsonTextsArray = json.getJsonArray(JSON_TEXTS);
        for (int i = 0; i < jsonTextsArray.size(); i++) {
            JsonObject jsonShape = jsonTextsArray.getJsonObject(i);
            DraggableText text = loadText(jsonShape, dataManager);
            dataManager.getNodes().add(text);
        }

    }

    private double getDataAsDouble(JsonObject json, String dataName) {
        JsonValue value = json.get(dataName);
        JsonNumber number = (JsonNumber) value;
        return number.bigDecimalValue().doubleValue();
    }

    private DraggableEllipse loadStation(JsonObject jsonStation, MMMData data) {
        // THEN LOAD ITS FILL AND OUTLINE PROPERTIES
        Color fillColor = loadColor(jsonStation, JSON_COLOR);
        double radius = getDataAsDouble(jsonStation, JSON_RADIUS);
        String name = jsonStation.get(JSON_NAME).toString();
        DraggableEllipse station = new DraggableEllipse(name.replaceAll("\"", ""));
        station.setRadiusX(radius);
        station.setRadiusY(radius);
        station.setFill(fillColor);
        double x = getDataAsDouble(jsonStation, JSON_X);
        double y = getDataAsDouble(jsonStation, JSON_Y);
        station.setCenterX(x);
        station.setCenterY(y);
        station.setStart((int) x, (int) y);
        JsonArray lines = jsonStation.getJsonArray(JSON_LINE_NAMES);
        for (JsonValue s : lines) {
            String n = s.toString().replaceAll("\"", "");
            station.addLineName(n);
            for (Node node : data.getNodes()) {
                if (!(node instanceof Line)) {
                    break;
                }
                if (node instanceof Line && node.getId().equals(n)) {
                    if (((Line) node).getStartX() == x && ((Line) node).getStartY() == y) {
                        ((Line) node).startXProperty().bind(station.centerXProperty());
                        ((Line) node).startYProperty().bind(station.centerYProperty());
                    }
                    if (((Line) node).getEndX() == x && ((Line) node).getEndY() == y) {
                        ((Line) node).endXProperty().bind(station.centerXProperty());
                        ((Line) node).endYProperty().bind(station.centerYProperty());
                    }
                }
            }
            for (MetroLine ml : data.getLines()) {
                if (ml.getId().equals(n)) {
                    // AVOID INDEX OUT OF BOUND EXCEPTION
                    if (ml.getStations().size() <= ml.getStationNames().indexOf(name.replaceAll("\"", ""))) {
                        ml.getStations().add(station);
                    } else {
                        ml.getStations().add(ml.getStationNames().indexOf(name.replaceAll("\"", "")), station);
                    }
                    break;
                }
            }
        }
        // ALL DONE, RETURN IT
        return station;
    }

    private MetroLine loadLine(JsonObject jsonLine, MMMData dataManager) {
        // THEN LOAD ITS FILL AND OUTLINE PROPERTIES
        Color fillColor = loadColor(jsonLine, JSON_COLOR);
        double thickness = getDataAsDouble(jsonLine, JSON_LINE_THICKNESS);
        String name = jsonLine.get(JSON_NAME).toString();
        boolean circular = jsonLine.getBoolean(JSON_CIRCULAR);
        MetroLine line = new MetroLine(name.replaceAll("\"", ""));
        line.setIsCircular(circular);

        JsonArray stations = jsonLine.getJsonArray(JSON_STATION_NAMES);
        for (JsonValue s : stations) {
            line.addStationName(s.toString().replaceAll("\"", ""));
        }

        // AND THEN ITS LINE SEGMENTS PROPERTIES
        JsonArray listOfLineSegs = jsonLine.getJsonArray(JSON_LINE_SEG);
        for (int i = 0; i < listOfLineSegs.size(); i++) {
            JsonObject xy = listOfLineSegs.getJsonObject(i);
            double startX = getDataAsDouble(xy, JSON_START_X);
            double startY = getDataAsDouble(xy, JSON_START_Y);
            double endX = getDataAsDouble(xy, JSON_END_X);
            double endY = getDataAsDouble(xy, JSON_END_Y);
            Line lineSeg = new Line(startX, startY, endX, endY);
            lineSeg.setOpacity(thickness);
            lineSeg.setStroke(fillColor);
            lineSeg.setStrokeWidth(thickness);
            lineSeg.setId(name.replaceAll("\"", ""));
            line.addLineSegment(lineSeg, dataManager);
        }
        return line;
    }

    private Color loadColor(JsonObject json, String colorToGet) {
        JsonObject jsonColor = json.getJsonObject(colorToGet);
        double red = getDataAsDouble(jsonColor, JSON_RED);
        double green = getDataAsDouble(jsonColor, JSON_GREEN);
        double blue = getDataAsDouble(jsonColor, JSON_BLUE);
        double alpha = getDataAsDouble(jsonColor, JSON_ALPHA);
        Color loadedColor = new Color(red, green, blue, alpha);
        return loadedColor;
    }

    private ImageView loadBackgroundImage(JsonObject json, String imageToGet) {
        JsonObject jsonImage = json.getJsonObject(imageToGet);
        ImageView i = loadImageView(jsonImage);
        return i;
    }

    private DraggableText loadText(JsonObject jsonText, MMMData data) {
        String text = jsonText.get(JSON_TEXT).toString();
        text = text.replaceAll("\"", "");
        DraggableText t = new DraggableText(text);
        Color color = loadColor(jsonText, JSON_COLOR);
        t.setFill(color);
        double rotation = getDataAsDouble(jsonText, JSON_ROTATION);
        t.setRotate(rotation);
        double x = getDataAsDouble(jsonText, JSON_X);
        double y = getDataAsDouble(jsonText, JSON_Y);
        t.setX(x);
        t.setY(y);
        boolean isStationLabel = false;
        for (Node node : data.getNodes()) {
            if (node instanceof DraggableEllipse && node.getId().equals(text)) {

                if (t.getX() > ((DraggableEllipse) node).getCenterX() && t.getY() < ((DraggableEllipse) node).getCenterY()) { // TOP RIGHT
                    t.xProperty().bind(((DraggableEllipse) node).centerXProperty().add(((DraggableEllipse) node).radiusXProperty()));
                    t.yProperty().bind(((DraggableEllipse) node).centerYProperty().subtract(((DraggableEllipse) node).radiusYProperty()));
                } else if (t.getX() < ((DraggableEllipse) node).getCenterX() && t.getY() < ((DraggableEllipse) node).getCenterY()) { // TOP LEFT
                    t.xProperty().bind(((DraggableEllipse) node).centerXProperty().subtract(((DraggableEllipse) node).radiusXProperty()).subtract(t.getLayoutBounds().getWidth()));
                    t.yProperty().bind(((DraggableEllipse) node).centerYProperty().subtract(((DraggableEllipse) node).radiusYProperty()));
                } else if (t.getX() > ((DraggableEllipse) node).getCenterX() && t.getY() > ((DraggableEllipse) node).getCenterY()) { // BOTTOM RIGHT
                    t.xProperty().bind(((DraggableEllipse) node).centerXProperty().add(((DraggableEllipse) node).radiusXProperty()));
                    t.yProperty().bind(((DraggableEllipse) node).centerYProperty().add(((DraggableEllipse) node).radiusYProperty()));
                } else { // BOTTOM LEFT
                    t.xProperty().bind(((DraggableEllipse) node).centerXProperty().subtract(((DraggableEllipse) node).radiusXProperty()).subtract(t.getLayoutBounds().getWidth()));
                    t.yProperty().bind(((DraggableEllipse) node).centerYProperty().add(((DraggableEllipse) node).radiusYProperty()));
                }
                t.setTextAlignment(TextAlignment.LEFT);
                isStationLabel = true;
                break;
            }
        }

        boolean isMetroLineLabel = false;
        if (!isStationLabel) {
            for (MetroLine ml : data.getLines()) {
                if (ml.getId().equals(text)) {
                    if (jsonText.getString(JSON_ALIGNMENT).equals("RIGHT")) {
                        t.setTextAlignment(TextAlignment.RIGHT);
                        t.xProperty().bind(ml.getLineSegments().get(0).startXProperty().subtract(t.getLayoutBounds().getWidth()));
                        t.yProperty().bind(ml.getLineSegments().get(0).startYProperty());
                        isMetroLineLabel = true;
                    } else {
                        t.setTextAlignment(TextAlignment.LEFT);
                        t.xProperty().bind(ml.getLineSegments().get(ml.getLineSegments().size() - 1).endXProperty());
                        t.yProperty().bind(ml.getLineSegments().get(ml.getLineSegments().size() - 1).endYProperty());
                        isMetroLineLabel = true;
                    }
                }
            }
        }
        if (!isMetroLineLabel) {
            t.setTextAlignment(TextAlignment.LEFT);
        }
        String fam = jsonText.get(JSON_FONT_FAM).toString();
        fam = fam.substring(1, fam.length() - 1);
        double size = getDataAsDouble(jsonText, JSON_FONT_SIZE);
        boolean bold = jsonText.getBoolean(JSON_BOLD);
        boolean italic = jsonText.getBoolean(JSON_ITALIC);
        if (!bold && !italic) {
            t.setFont(Font.font(fam, size));
        } else if (!bold && italic) {
            t.setFont(Font.font(fam, FontPosture.ITALIC, size));
        } else if (bold && !italic) {
            t.setFont(Font.font(fam, FontWeight.BOLD, size));
        } else {
            t.setFont(Font.font(fam, FontWeight.BOLD, FontPosture.ITALIC, size));
        }
        return t;
    }

    private ImageView loadImageView(JsonObject jsonImage) {
        ImageView i = new ImageView();
        JsonValue value = jsonImage.get(JSON_IMAGE);
        if (value != null) {
            String path = value.toString();
            path = path.substring(1, path.length() - 1);
            double x = getDataAsDouble(jsonImage, JSON_X);
            double y = getDataAsDouble(jsonImage, JSON_Y);
            double opacity = getDataAsDouble(jsonImage, JSON_OPACITY);
            Image image = new Image(path);
            i.setOpacity(opacity);
            i.setImage(image);
            i.setX(x);
            i.setY(y);
            return i;
        }
        return null;
    }

    private DraggableImage loadDraggableImage(JsonObject jsonImage) {
        DraggableImage i = new DraggableImage();
        JsonValue value = jsonImage.get(JSON_IMAGE);
        String path = value.toString();
        path = path.substring(1, path.length() - 1);
        double x = getDataAsDouble(jsonImage, JSON_X);
        double y = getDataAsDouble(jsonImage, JSON_Y);
        Image image = new Image(path);
        i.setImage(image);
        i.setX(x);
        i.setY(y);
        return i;
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }

    /**
     * This method is provided to satisfy the compiler, but it is not used by
     * this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        // AGAIN, WE ARE NOT USING THIS IN THIS ASSIGNMENT
    }
}
