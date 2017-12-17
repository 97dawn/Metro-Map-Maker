/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableText;
import MMM.data.MMMData;
import MMM.data.MetroLine;
import java.util.ArrayList;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class RemoveLineTran implements jTPS_Transaction{
     private MMMData data;
    private ArrayList<Line> lineSegs;
    private DraggableText leftLabel;
    private DraggableText rightLabel;
    private MetroLine metroLine;
    
    public RemoveLineTran(MMMData initData, ArrayList<Line> initLine, DraggableText initLeftLabel, DraggableText initRightLabel, MetroLine initML){
        data = initData;
        lineSegs = initLine;
        leftLabel = initLeftLabel;
        rightLabel = initRightLabel;
        metroLine = initML;
    }
    @Override
    public void doTransaction(){
         data.removeLine(lineSegs, leftLabel, rightLabel, metroLine);
    }
    @Override
    public void undoTransaction(){
         data.addLine(lineSegs, leftLabel, rightLabel, metroLine);
    }
}
