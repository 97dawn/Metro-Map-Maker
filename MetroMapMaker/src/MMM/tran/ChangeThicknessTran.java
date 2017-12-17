/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;
import MMM.data.MetroLine;
import javafx.scene.control.Slider;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;
/**
 *
 * @author Doeun Kim
 */
public class ChangeThicknessTran implements jTPS_Transaction{
    private MetroLine line;
    private double outlineThickness;
    private double oldOutlineThickness;
    private Slider thick;
    public ChangeThicknessTran(MetroLine initLine, double initOutlineThickness, Slider thick) {
        line = initLine;
        outlineThickness = initOutlineThickness;
        oldOutlineThickness = line.getLineSegments().get(0).getStrokeWidth();
        this.thick=thick;
    }

    @Override
    public void doTransaction() {
        for(Line line : line.getLineSegments()){
            line.setStrokeWidth(outlineThickness);
        }
        thick.setValue(outlineThickness);
    }

    @Override
    public void undoTransaction() {
        for(Line line : line.getLineSegments()){
            line.setStrokeWidth(oldOutlineThickness);
        }
        thick.setValue(oldOutlineThickness);
    }    
}
