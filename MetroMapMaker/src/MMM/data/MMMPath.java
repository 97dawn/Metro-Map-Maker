/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.data;

import java.util.ArrayList;

/**
 *
 * @author Doeun Kim
 */
public class MMMPath {

    private DraggableEllipse startStation;
    private DraggableEllipse endStation;
    public final static int STATION_COST = 3;
    public final static int TRANSFER_COST = 10;
    private ArrayList<MetroLine> tripLines;
    private ArrayList<DraggableEllipse> tripStations;
    private ArrayList<String> tripStationNames;
    private ArrayList<String> tripLineNames;
    private ArrayList<DraggableEllipse> boardingStations;

    public MMMPath(DraggableEllipse initStartStation, DraggableEllipse initEndStation) {
        startStation = initStartStation;
        endStation = initEndStation;
        tripLines = new ArrayList();
        tripLineNames = new ArrayList();
        tripStations = new ArrayList();
        tripStationNames = new ArrayList();
        boardingStations = new ArrayList();
    }
    public ArrayList<DraggableEllipse> getBoardingStations(){
        return boardingStations;
    }
    public ArrayList<MetroLine> getTripLines() {
        return tripLines;
    }

    public MMMPath clone() {
        MMMPath clonedPath = new MMMPath(startStation, endStation);
        for (int i = 0; i < tripLines.size(); i++) {
            clonedPath.tripLines.add(tripLines.get(i));
            //clonedPath.tripLineNames[tripLines.get(i).getId()];
        }
        for (int i = 0; i < this.tripStations.size(); i++) {
            clonedPath.tripStations.add(tripStations.get(i));
            //clonedPath.tripStationNames[tripStations.get(i).getId()];
        }
        for (int i = 0; i < this.boardingStations.size(); i++) {
            clonedPath.boardingStations.add(boardingStations.get(i));
        }
        return clonedPath;
    }

    public void addBoarding(MetroLine boardingLine, DraggableEllipse boardingStation) {
        // WE'LL NEED THE LINE AND GET THE NAMES TOO FOR QUICK LOOKUP
        tripLines.add(boardingLine);
        //tripLineNames[boardingLine.getId()] = boardingLine.getId();

        // THESE ARE THE STATIONS WHERE A PERSON WOULD BOARD A TRAIN
        boardingStations.add(boardingStation);
    }

    public boolean hasLineWithStation(String testStationName) {
        // GO THROUGH ALL THE LINES AND SEE IF IT'S IN ANY OF THEM'
        for (int i = 0; i < tripLines.size(); i++) {
            if (tripLines.get(i).getStationNames().contains(testStationName)) {
                // YUP
                return true;
            }
        }
        // NOPE
        return false;
    }

    public boolean hasLine(String testLineName) {
        return tripLineNames.contains(testLineName);
    }

    public int calculateTimeOfTrip() {
        ArrayList<DraggableEllipse> stations = getTripStations();
        int stationsCost = (stations.size() - 1) * STATION_COST;
        int transferCost = (tripLines.size() - 1) * TRANSFER_COST;
        return stationsCost + transferCost;
    }

    public boolean isCompleteTrip() {
        if (tripLines.size() == 0) {
            return false;
        }

        // THEN, IS THE END STATION ON THE LAST LINE? IF IT IS NOT THEN THE TRIP IS INCOMPLETE
        if (!tripLines.get(tripLines.size() - 1).getStationNames().contains(endStation.getId())) {
            return false;
        }

        // NOW, ARE ALL THE BOARDING STATIONS ON ALL THE TRIP LINES, IF NOT IT'S INCORRECT
        for (int i = 0; i < boardingStations.size(); i++) {
            if (!tripLines.get(i).getStationNames().contains(boardingStations.get(i).getId())) {
                return false;
            }
        }

        // IF WE MADE IT THIS FAR WE KNOW IT'S A COMPLETE TRIP'
        return true;
    }

    public ArrayList<DraggableEllipse> getTripStations() {
        // WE'LL RETURN AN ARRAY OF STATIONS AND WE'LL USE THE NAMES
        // FOR A QUICK LOOKUP
        tripStations = new ArrayList<>();
        tripStationNames = new ArrayList<>();

        // WE ONLY DO THIS IF WE HAVE A VALID TRIP
        if (isCompleteTrip()) {
            // IF WE MADE IT THIS FAR WE KNOW IT'S A GOOD TRIP
            int i = 0;
            while (i < this.boardingStations.size() - 1) {
                ArrayList<DraggableEllipse> stationsToAdd = generateStationsForPathOnLine(tripLines.get(i), boardingStations.get(i), boardingStations.get(i + 1));
                for (int j = 0; j < stationsToAdd.size(); j++) {
                    DraggableEllipse stationToAdd = stationsToAdd.get(i);
                    if (!tripStationNames.contains(stationToAdd.getId())) {
                        tripStations.add(stationToAdd);
                        //tripStationNames[stationToAdd.name] = stationToAdd.getId();
                    }
                }

                // ONTO THE NEXT LINE
                i++;
            }
            // AND NOW FOR THE LAST LINK IN THE CHAIN
            ArrayList<DraggableEllipse> stationsToAdd = generateStationsForPathOnLine(tripLines.get(i), boardingStations.get(i), endStation);
            for (int k = 0; k < stationsToAdd.size(); k++) {
                DraggableEllipse stationToAdd = stationsToAdd.get(k);
                this.tripStations.add(stationToAdd);
            }
        }

        // RETURN THE STATIONS
        return tripStations;
    }
    
    public ArrayList<DraggableEllipse> generateStationsForPathOnLine(MetroLine line, DraggableEllipse station1, DraggableEllipse station2) {
        ArrayList<DraggableEllipse> stationsOnPath = new ArrayList<>();
        int station1Index = line.getStationNames().indexOf(station1.getId());
        int station2Index = line.getStationNames().indexOf(station2.getId());
        
        // FOR CIRCULAR LINES WE CAN GO IN EITHER DIRECTION
        if (line.getIsCircular()) {
            if (station1Index >= station2Index) {
                int forward = station1Index - station2Index;
                int reverse = station2Index + line.getStations().size() - station1Index;
                if (forward < reverse) {
                    for (int i = station1Index; i >= station2Index; i--) {
                        DraggableEllipse stationToAdd = line.getStations().get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
                else {
                    for (int i = station1Index; i < line.getStations().size(); i++) {
                        DraggableEllipse stationToAdd = line.getStations().get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                    for (int i = 0; i <= station2Index; i++) {
                        DraggableEllipse stationToAdd = line.getStations().get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
            }
            // STILL CIRCULAR, BUT station1 IS BEFORE station2 IN THE ARRAY
            else {
                int forward = station2Index - station1Index;
                int reverse = station1Index + line.getStations().size() - station2Index;
                if (forward < reverse) {
                    for (int i = station1Index; i <= station2Index; i++) {
                        DraggableEllipse stationToAdd = line.getStations().get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
                else {
                    for (int i = station1Index; i >= 0; i--) {
                        DraggableEllipse stationToAdd = line.getStations().get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                    for (int i = line.getStations().size() - 1; i >= station2Index; i--) {
                        DraggableEllipse stationToAdd = line.getStations().get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
            }
        }
        // NOT CIRCULAR
        else {
            if (station1Index >= station2Index) {
                for (int i = station1Index; i >= station2Index; i--) {
                    DraggableEllipse stationToAdd = line.getStations().get(i);
                    stationsOnPath.add(stationToAdd);
                }
            }
            else {
                for (int i = station1Index; i <= station2Index; i++) {
                    DraggableEllipse stationToAdd = line.getStations().get(i);
                    stationsOnPath.add(stationToAdd);
                }
            }
        }
        return stationsOnPath;
    }
}
