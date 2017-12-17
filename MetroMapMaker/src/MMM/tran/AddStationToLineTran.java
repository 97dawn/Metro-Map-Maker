/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableEllipse;
import MMM.data.DraggableText;
import MMM.data.MMMData;
import MMM.data.MetroLine;
import MMM.gui.MMMCanvasController;
import djf.AppTemplate;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class AddStationToLineTran implements jTPS_Transaction {

    private MMMData dataManager;
    private MMMCanvasController c;
    private MetroLine metroLine;
    private Line rightLine;
    private DraggableEllipse station;
    private int index;
    private int size;
    private String selectedLineName;
    private DraggableText label;

    public AddStationToLineTran(AppTemplate initApp, DraggableText initLabel, int initSize, Line initRight, MetroLine initLine, DraggableEllipse initStation, int initIndex, String initName) {
        dataManager = (MMMData) initApp.getDataComponent();
        c = new MMMCanvasController(initApp);
        metroLine = initLine;
        station = initStation;
        index = initIndex;
        selectedLineName = initName;
        size = initSize;
        rightLine = initRight;
        label = initLabel;
    }

    @Override
    public void doTransaction() {
        if (metroLine.getStations().contains(station)) {
            metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty().bind(station.centerXProperty());
            metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty().bind(station.centerYProperty());
            label.xProperty().bind(rightLine.endXProperty());
            label.yProperty().bind(rightLine.endYProperty());
            dataManager.getNodes().add(0, rightLine);
            metroLine.getLineSegments().add(rightLine);
            metroLine.getStationNames().add(station.getId());
        } else {
            dataManager.addStationToLine(label, size, rightLine, metroLine, station, index, selectedLineName);
        }
    }

    @Override
    public void undoTransaction() {
        int dup = 0;
        for (String s : metroLine.getStationNames()) {
            if (s.equals(station.getId())) {
                dup++;
            }
        }
        if (dup > 1) {
            metroLine.getLineSegments().get(metroLine.getLineSegments().indexOf(rightLine) - 1).endXProperty().bind(label.xProperty());
            metroLine.getLineSegments().get(metroLine.getLineSegments().indexOf(rightLine) - 1).endYProperty().bind(label.yProperty());
            dataManager.getNodes().remove(rightLine);
            metroLine.getLineSegments().remove(rightLine);
            metroLine.getStationNames().remove(metroLine.getStationNames().lastIndexOf(station.getId()));
        } else {
            dataManager.removeStationFromLine(label, rightLine, metroLine, station, metroLine.getStations().indexOf(station), selectedLineName);
            // REMOVE LINE FROM STATION'S LINE LIST
            station.getLines().remove(selectedLineName);
        }
    }
}
