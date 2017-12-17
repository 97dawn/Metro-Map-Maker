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
import MMM.gui.MMMWorkspace;
import djf.AppTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class RemoveStationTran implements jTPS_Transaction {

    private MMMData data;
    private DraggableEllipse station;
    private DraggableText label;
    private boolean stationIsOnTheLine;
    //private int indexOfStationInArray;
    private HashMap<MetroLine, ArrayList<Integer>> metroLinesOfStation;
    private MMMCanvasController c;
    private MMMWorkspace workspace;

    public RemoveStationTran(MMMCanvasController initC, AppTemplate initApp, DraggableEllipse initStation, DraggableText initStationLabel) {
        data = (MMMData) initApp.getDataComponent();
        workspace = (MMMWorkspace) initApp.getWorkspaceComponent();
        c = initC;
        station = initStation;
        label = initStationLabel;
        stationIsOnTheLine = initStation.getLines().size() != 0;
        //indexOfStationInArray = index;
        metroLinesOfStation = new HashMap<MetroLine, ArrayList<Integer>>();
        for (MetroLine ml : data.getLines()) {
            if (ml.getStations().contains(station)) {
                metroLinesOfStation.put(ml, new ArrayList<>());
                for (int i = 0; i < ml.getStationNames().size(); i++) {
                    if (ml.getStationNames().get(i).equals(station.getId())) {
                        metroLinesOfStation.get(ml).add(i);
                    }
                }
            }
        }
    }

    @Override
    public void doTransaction() {
        for (MetroLine ml : metroLinesOfStation.keySet()) {
            // FIND THE RIGHT LABEL
            DraggableText rightLabel = null;
            for (Node n : data.getNodes()) {
                if (n instanceof DraggableText && ((DraggableText) n).getText().equals(ml.getId()) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                    rightLabel = (DraggableText) n;
                    break;
                }
            }
            ArrayList<Line> rightLines = new ArrayList<>();
            for (Integer i : metroLinesOfStation.get(ml)) {
                // GET LEFT LINE
                Line leftLine = ml.getLineSegments().get(i);
                // GET RIGHT LINE
                Line rightLine = ml.getLineSegments().get(i + 1);
                rightLines.add(rightLine);
                // GET END X,Y OF RIGHT LINE
                double oldx = rightLine.getEndX();
                double oldy = rightLine.getEndY();
                // RELOATE END X,Y OF LEFT LINE
                // IF THE STATION IS THE LAST STATION ON THE METROLINE
                if (i == ml.getStationNames().size() - 1) {
                    leftLine.endXProperty().unbind();
                    leftLine.endYProperty().unbind();
                    leftLine.endXProperty().set(oldx);
                    leftLine.endYProperty().set(oldy);
                    rightLabel.xProperty().bind(leftLine.endXProperty());
                    rightLabel.yProperty().bind(leftLine.endYProperty());
                } else {
                    leftLine.endXProperty().bind(ml.getStations().get(i + 1).centerXProperty());
                    leftLine.endYProperty().bind(ml.getStations().get(i + 1).centerYProperty());
                }
            }
            // REMOVE ALL RIGHT LINES
            for (Line r : rightLines) {
                data.getNodes().remove(r);
                ml.getLineSegments().remove(r);
            }
            // REMOVE ALL STATION NAMES
            for (int i = 0; i < ml.getStationNames().size(); i++) {
                if (ml.getStationNames().get(i).equals(station.getId())) {
                    ml.getStationNames().remove(station.getId());
                }
            }
            // REMOVE STATION
            ml.getStations().remove(station);
            station.getLines().remove(ml.getId());
        }
        
        data.getNodes().removeAll(station,label);
        workspace.getMetroStations().getItems().remove(label.getText());
        workspace.reloadWorkspace(data);
    }

    @Override
    public void undoTransaction() {
        workspace.getMetroStations().getItems().add(label.getText());
        data.getNodes().addAll(station, label);
        for (MetroLine metroLineOfStation : metroLinesOfStation.keySet()) {
            DraggableText rightLabel = null;
            for (Node n : data.getNodes()) {
                if (n instanceof DraggableText && ((DraggableText) n).getText().equals(metroLineOfStation.getId()) && ((DraggableText) n).getTextAlignment().equals(TextAlignment.LEFT)) {
                    rightLabel = (DraggableText) n;
                    break;
                }
            }
            
            for (Integer i : metroLinesOfStation.get(metroLineOfStation)) {
                if (stationIsOnTheLine) {
                    // IF THERE WAS ONLY ONE STATION ON THE LINE
                    if (metroLineOfStation.getStationNames().size() == 0) {
                        Line rightLine = c.createRightLineSegment(metroLineOfStation, station, 0);
                        rightLabel.xProperty().bind(rightLine.endXProperty());
                        rightLabel.yProperty().bind(rightLine.endYProperty());
                        data.getNodes().add(0, rightLine);
                        metroLineOfStation.getLineSegments().add(rightLine);
                        metroLineOfStation.getStationNames().add(station.getId());
                        station.getLines().add(metroLineOfStation.getId());
                    } // IF THERE WERE MORE THAN ONE STATIONS ON THE LINE
                    else {
                        if (i == 0) {
                            Line rightLine = c.createRightLineSegment(metroLineOfStation, station, 0);
                            data.getNodes().add(0, rightLine);
                            metroLineOfStation.getLineSegments().add(1, rightLine);
                            rightLine.endXProperty().bind(metroLineOfStation.getStations().get(0).centerXProperty());
                            rightLine.endYProperty().bind(metroLineOfStation.getStations().get(0).centerYProperty());
                            // ADD THE STATION TO THE TWO ARRAY LISTS OF METROLINE
                            metroLineOfStation.getStationNames().add(0, station.getId());
                            station.getLines().add(metroLineOfStation.getId());
                        } else if (i == metroLineOfStation.getStationNames().size()) {
                            Line rightLine = c.createRightLineSegment(metroLineOfStation, station, metroLineOfStation.getLineSegments().size() - 1);
                            data.getNodes().add(0, rightLine);
                            rightLabel.xProperty().bind(rightLine.endXProperty());
                            rightLabel.yProperty().bind(rightLine.endYProperty());
                            metroLineOfStation.getLineSegments().add(i + 1, rightLine);
                            metroLineOfStation.getStationNames().add(i, station.getId());
                            station.getLines().add(metroLineOfStation.getId());
                        } else {
                            Line rightLine = c.createRightLineSegment(metroLineOfStation, station, i);
                            c.addStationBetweenTwoStations(metroLineOfStation, rightLine, station, metroLineOfStation.getStations().get(i), i, data, metroLineOfStation.getId());
                            metroLineOfStation.getStations().remove(station);
                        }
                    }
                }
            }metroLineOfStation.getStations().add(metroLinesOfStation.get(metroLineOfStation).get(0), station);
        }
        workspace.reloadWorkspace(data);
    }
}
