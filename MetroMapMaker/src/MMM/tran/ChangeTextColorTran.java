/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableText;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class ChangeTextColorTran implements jTPS_Transaction{
    private DraggableText text;
    private Color oldColor;
    private Color newColor;
    public ChangeTextColorTran(DraggableText initText, Color initOld ){
        text=initText;
        oldColor = initOld;
        newColor = (Color)text.getFill();
    }
    @Override
    public void doTransaction(){
         text.setFill(newColor);
    }
    @Override
    public void undoTransaction(){
        text.setFill(oldColor);
    }
}
