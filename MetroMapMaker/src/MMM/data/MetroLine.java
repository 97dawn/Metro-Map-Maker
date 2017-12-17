/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.data;

import java.util.ArrayList;
import javafx.scene.shape.Line;

/**
 *
 * @author user
 */
public class MetroLine extends Line {

    private ArrayList<DraggableEllipse> stations;
    private boolean isCircular;
    private ArrayList<Line> lineSegments;
    private ArrayList<String> stationNames;

    public MetroLine(String name) {
        setId(name);
        isCircular =false;
        lineSegments = new ArrayList<>();
       stations = new ArrayList<>();
       stationNames = new ArrayList<>();
    }
    public ArrayList<DraggableEllipse> getStations(){
        return stations;
    }
    public void addStation(DraggableEllipse station){
        stations.add(station);
    }
    public void removeStation(DraggableEllipse station){
        stations.remove(station);
    }
     public ArrayList<String> getStationNames(){
        return stationNames;
    }
    public void addStationName(String station){
        stationNames.add(station);
    }
    public void removeStationName(String station){
        stationNames.remove(station);
    }
    public void setIsCircular(boolean b){
        isCircular = b;
    }
    public boolean getIsCircular(){
        return isCircular;
    }
    public ArrayList<Line> getLineSegments(){
        return lineSegments;
    }
    public void addLineSegment(Line seg, MMMData data){
        data.getNodes().add(0,seg);
        lineSegments.add(seg);
    }
    public void removeLineSegment(Line seg){
        lineSegments.remove(seg);
    }
    public DraggableEllipse findIntersectingStation (MetroLine intersectingLine) {
        // GO TRHOUGH ALL THE STATIONS IN THIS LINE
        for (int i = 0; i < stations.size(); i++) {
            DraggableEllipse station1 = stations.get(i);
            String station1Name = station1.getId();

            // FOUND IT
            if (intersectingLine.getStationNames().contains(station1Name)) {
                return station1;
            }
        }
        // THEY DON'T SHARE A STATION'
        return null;
    }
}
