/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableText;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class MoveLineEndTran implements jTPS_Transaction {

    private Line line;
    private DraggableText label;
    private double oldx;
    private double oldy;
    private double newx;
    private double newy;

    public MoveLineEndTran(DraggableText initLabel, Line initLine, double initOldX, double initOldY) {
        line = initLine;
        label = initLabel;
        if (label.getTextAlignment().equals(TextAlignment.LEFT)) {
            newx = line.getEndX();
            newy = line.getEndY();
        } else {
            newx = line.getStartX();
            newy = line.getStartY();
        }
        oldx = initOldX;
        oldy = initOldY;
    }

    @Override
    public void doTransaction() {
        if (label.getTextAlignment().equals(TextAlignment.LEFT)) {
            line.setEndX(newx);
             line.setEndY(newy);
        } else {
             line.setStartX(newx);
            line.setStartY(newy);
        }
    }

    @Override
    public void undoTransaction() {
        if (label.getTextAlignment().equals(TextAlignment.LEFT)) {
            line.setEndX(oldx);
            line.setEndY(oldy);
        } else {
            line.setStartX(oldx);
            line.setStartY(oldy);
        }
    }
}
