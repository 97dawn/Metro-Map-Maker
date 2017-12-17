/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableEllipse;
import MMM.data.DraggableText;
import MMM.data.MMMData;
import MMM.gui.MMMWorkspace;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class AddNewStationTran implements jTPS_Transaction {

    private MMMData data;
    private DraggableEllipse station;
    private DraggableText label;
    private MMMWorkspace workspace;

    public AddNewStationTran(MMMWorkspace initW,MMMData initData, DraggableEllipse initStation, DraggableText initLabel) {
        data = initData;
        station = initStation;
        label = initLabel;
        workspace = initW;
    }

    @Override
    public void doTransaction() {
        data.addNewStation(station, label);
            // LOAD COMBO BOX 
            workspace.getMetroStations().getItems().add(label.getText());
            workspace.getMetroStations().setValue(label.getText());
            workspace.reloadWorkspace(data);
    }

    @Override
    public void undoTransaction() {
        data.removeStation(station, label);
            // LOAD COMBO BOX 
            workspace.getMetroStations().getItems().remove(label.getText());
            workspace.reloadWorkspace(data);
    }
}
