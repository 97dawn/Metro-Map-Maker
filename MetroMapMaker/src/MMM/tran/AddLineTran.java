/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableText;
import MMM.data.MMMData;
import MMM.data.MetroLine;
import MMM.gui.MMMWorkspace;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class AddLineTran implements jTPS_Transaction {

    private MMMData data;
    private MetroLine line;
    private DraggableText left;
    private DraggableText right;
    private Line lineSeg;
    private MMMWorkspace workspace;

    public AddLineTran(MMMWorkspace initW,MMMData initData, MetroLine initLine, Line initLineSeg, DraggableText initLeft, DraggableText initRight) {
        data = initData;
        line = initLine;
        left = initLeft;
        right = initRight;
        lineSeg = initLineSeg;
        workspace = initW;
    }

    @Override
    public void doTransaction() {
        // ADD LINE END LABELS AND METROLINE
        data.addNewLine(line, left, right);
        // ADD LINE SEGMENT
        line.addLineSegment(lineSeg, data);
        // LOAD COMBO BOX AND COLOR PICKER
        workspace.getMetroLines().getItems().add(line.getId());
        workspace.getMetroLines().setValue(line.getId());
    }

    @Override
    public void undoTransaction() {
        data.getLines().remove(line);
        line.removeLineSegment(lineSeg);
        data.getNodes().remove(lineSeg);
        data.getNodes().removeAll(left, right);
        // LOAD COMBO BOX AND COLOR PICKER
        workspace.getMetroLines().getItems().remove(line.getId());
    }
}
