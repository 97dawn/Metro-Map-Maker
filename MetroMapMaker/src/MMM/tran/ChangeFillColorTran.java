/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableEllipse;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class ChangeFillColorTran implements jTPS_Transaction {
    private DraggableEllipse station;
    private Color color;
    private Color oldColor;
    
    public ChangeFillColorTran(DraggableEllipse initStation, Color initColor) {
        station = initStation;
        color = initColor;
        oldColor = (Color)station.getFill();
    }

    @Override
    public void doTransaction() {
        station.setFill(color);
    }

    @Override
    public void undoTransaction() {
        station.setFill(oldColor);
    }    
}