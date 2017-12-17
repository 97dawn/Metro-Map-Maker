/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableEllipse;
import javafx.scene.control.Slider;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class ChangeStationCircleRadiusTran implements jTPS_Transaction{
    private DraggableEllipse station;
    private double oldR;
    private double newR;
    private Slider radius;
    public ChangeStationCircleRadiusTran(DraggableEllipse initStation, double initNew, Slider radius){
        station = initStation;
        oldR = station.getRadiusX();
        newR = initNew;
        this.radius = radius;
    }
     @Override
    public void doTransaction(){
        station.setRadiusX(newR);
        station.setRadiusY(newR);
        radius.setValue(newR);
    }
    @Override
    public void undoTransaction(){
    station.setRadiusX(oldR);
        station.setRadiusY(oldR);
        radius.setValue(oldR);
    } 
}
