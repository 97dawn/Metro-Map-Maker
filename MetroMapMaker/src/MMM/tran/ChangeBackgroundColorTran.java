/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class ChangeBackgroundColorTran implements jTPS_Transaction{
    private Pane pane;
    private Color color;
    private Color oldColor;
    private ColorPicker cp;
    public ChangeBackgroundColorTran(Pane initPane, Color initColor, ColorPicker cp){
        pane = initPane;
        color = initColor;
        oldColor = (Color)pane.getBackground().getFills().get(0).getFill();
        this.cp  = cp;
    }
    @Override
    public void doTransaction(){
        BackgroundFill fill = new BackgroundFill(color, null, null);
        Background background = new Background(fill);
        pane.setBackground(background);
        cp.setValue(color);
    }
    @Override
    public void undoTransaction(){
        BackgroundFill fill = new BackgroundFill(oldColor, null, null);
        Background background = new Background(fill);
        pane.setBackground(background);    
        cp.setValue(oldColor);
    }
}
