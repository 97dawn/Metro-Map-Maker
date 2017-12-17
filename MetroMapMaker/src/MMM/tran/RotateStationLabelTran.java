/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableText;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class RotateStationLabelTran implements jTPS_Transaction{
    private DraggableText label;
    public RotateStationLabelTran(DraggableText initLabel){
        label=initLabel;
    }
     @Override
    public void doTransaction(){
        label.setRotate(label.getRotate()-90);
    }
    @Override
    public void undoTransaction(){
        label.setRotate(label.getRotate()+90);
    } 
    
}
