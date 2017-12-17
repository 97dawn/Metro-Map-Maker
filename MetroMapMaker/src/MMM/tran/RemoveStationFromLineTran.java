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
public class RemoveStationFromLineTran implements jTPS_Transaction {

    private MMMData dataManager;
    private MMMCanvasController c;
    private MetroLine metroLine;
    private Line rightLine;
    private DraggableEllipse station;
    private int index;
    private int dup;
    private String selectedLineName;
    private DraggableText label;

    public RemoveStationFromLineTran(AppTemplate initApp, DraggableText initLabel, Line initRight, MetroLine initLine, DraggableEllipse initStation, int initIndex, String initName) {
        dataManager = (MMMData) initApp.getDataComponent();
        c = new MMMCanvasController(initApp);
        metroLine = initLine;
        station = initStation;
        index = initIndex;
        selectedLineName = initName;
        //size = initSize;
        rightLine = initRight;
        label = initLabel;
        dup = 0;
        for (String s : metroLine.getStationNames()) {
            if (s.equals(station.getId())) {
                dup++;
            }
        }
    }

    @Override
    public void doTransaction() {

        if (dup > 1) {
            //  RIGHT LINE IS THE LAST LINE 
            if (metroLine.getLineSegments().indexOf(rightLine) == metroLine.getLineSegments().size() - 1) {
                metroLine.getLineSegments().get(metroLine.getLineSegments().indexOf(rightLine) - 1).endXProperty().bind(label.xProperty());
                metroLine.getLineSegments().get(metroLine.getLineSegments().indexOf(rightLine) - 1).endYProperty().bind(label.yProperty());
                // INDEX OF LEFT LINE
                index = metroLine.getLineSegments().indexOf(rightLine) - 1;
            } else {
                metroLine.getLineSegments().get(metroLine.getLineSegments().indexOf(rightLine) - 1).endXProperty().bind(metroLine.getStations().get(metroLine.getStationNames().lastIndexOf(station.getId())).centerXProperty());
                metroLine.getLineSegments().get(metroLine.getLineSegments().indexOf(rightLine) - 1).endYProperty().bind(metroLine.getStations().get(metroLine.getStationNames().lastIndexOf(station.getId())).centerYProperty());
                index = metroLine.getLineSegments().indexOf(rightLine) - 1;
            }
            metroLine.getStationNames().remove(metroLine.getStationNames().lastIndexOf(station.getId()));
            dataManager.getNodes().remove(rightLine);
            metroLine.getLineSegments().remove(rightLine);
        } else {
            dataManager.removeStationFromLine(label, rightLine, metroLine, station, index, selectedLineName);
        }
    }

    @Override
    public void undoTransaction() {
        if (dup > 1) {
            if (index == metroLine.getLineSegments().size() - 1) {
                rightLine.setEndX(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndX());
                rightLine.setEndY(metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).getEndY());
                metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endXProperty().bind(station.centerXProperty());
                metroLine.getLineSegments().get(metroLine.getLineSegments().size() - 1).endYProperty().bind(station.centerYProperty());
                rightLine.startXProperty().bind(station.centerXProperty());
                rightLine.startYProperty().bind(station.centerYProperty());
                label.xProperty().bind(rightLine.endXProperty());
                label.yProperty().bind(rightLine.endYProperty());
                dataManager.getNodes().add(0, rightLine);
                metroLine.getLineSegments().add( rightLine);
                metroLine.getStationNames().add( station.getId());
            } else {
                metroLine.getLineSegments().get(index).endXProperty().bind(station.centerXProperty());
                metroLine.getLineSegments().get(index).endYProperty().bind(station.centerYProperty());
                rightLine.startXProperty().bind(station.centerXProperty());
                rightLine.startYProperty().bind(station.centerYProperty());
                rightLine.endXProperty().bind(metroLine.getStations().get(index).centerXProperty());
                rightLine.endYProperty().bind(metroLine.getStations().get(index).centerYProperty());
                dataManager.getNodes().add(0, rightLine);
                metroLine.getLineSegments().add(index + 1, rightLine);
                metroLine.getStationNames().add(index, station.getId());
            }

        } else {
            // ADD RIGHT LINE SEGMENT TO THE OBSERVABLE LIST
            dataManager.getNodes().add(0, rightLine);
            // ADD RIGHT LINE SEGMENT TO THE ARRAY LIST OF METRO LINE
            if (index == metroLine.getLineSegments().size() - 1) {
                metroLine.getLineSegments().add(rightLine);
            } else {
                metroLine.getLineSegments().add(index + 1, rightLine);
            }
            metroLine.getLineSegments().get(index).endXProperty().bind(station.centerXProperty());
            metroLine.getLineSegments().get(index).endYProperty().bind(station.centerYProperty());
            rightLine.startXProperty().bind(station.centerXProperty());
            rightLine.startYProperty().bind(station.centerYProperty());
            if (index == metroLine.getStations().size()) {
                // MOVE RIGHT LABEL
                label.xProperty().bind(rightLine.endXProperty());
                label.yProperty().bind(rightLine.endYProperty());
            } else {
                rightLine.endXProperty().bind(metroLine.getStations().get(index).centerXProperty());
                rightLine.endYProperty().bind(metroLine.getStations().get(index).centerYProperty());
            }
            // ADD THE STATION TO THE TWO ARRAY LISTS OF METROLINE
            metroLine.getStationNames().add(index, station.getId());
            metroLine.getStations().add(index, station);
            station.getLines().add(selectedLineName);
        }

    }
}
