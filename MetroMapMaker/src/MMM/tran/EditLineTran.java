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
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class EditLineTran implements jTPS_Transaction {

    private MMMWorkspace workspace;
    private MMMData data;
    private MetroLine oldLine;
    private String newName;
    private String oldName;
    private Color oldColor;
    private Color newColor;
    private boolean oldIsCircular;
    private boolean newIsCircular;
    
           

    public EditLineTran(MMMWorkspace initW, MMMData initD, MetroLine initLine, String initName, Color initColor, boolean initIsCircular) {
        data = initD;
        workspace = initW;
        oldLine = initLine;
        newName=initName;
        newColor=initColor;
        newIsCircular=initIsCircular;
        oldName=oldLine.getId();
        oldColor=(Color)oldLine.getLineSegments().get(0).getStroke();
        oldIsCircular=oldLine.getIsCircular();
    }

    @Override
    public void doTransaction() {
        // LINE COLOR CHANGED
        for (Line l : oldLine.getLineSegments()) {
            l.setStroke(newColor);
            l.setId(newName);
        }
        // LINE NAME CHANGED
        // UPDATE COMBOBOX
        int indexOfLine = workspace.getMetroLines().getItems().indexOf(oldLine.getId());
        workspace.getMetroLines().getItems().remove(indexOfLine);
        workspace.getMetroLines().getItems().add(indexOfLine, newName);
        workspace.getMetroLines().setValue(newName);
        // UPDATE THE LINE LABEL TEXTS
        for (Node n : data.getNodes()) {
            if (n instanceof DraggableText && ((DraggableText) n).getText().equals(oldLine.getId())) {
                ((DraggableText) n).setText(newName);
            }
        }
        // CHANGE THE METRO LINE'S ID
        oldLine.setId(newName);
        // CHANGE ISCIRCULAR
        oldLine.setIsCircular(newIsCircular);
    }

    @Override
    public void undoTransaction() {
        // LINE COLOR CHANGED
        for (Line l : oldLine.getLineSegments()) {
            l.setStroke(oldColor);
            l.setId(oldName);
        }
        // LINE NAME CHANGED
        // UPDATE COMBOBOX
        int indexOfLine = workspace.getMetroLines().getItems().indexOf(oldLine.getId());
        workspace.getMetroLines().getItems().remove(indexOfLine);
        workspace.getMetroLines().getItems().add(indexOfLine, oldName);
        workspace.getMetroLines().setValue(oldName);
        // UPDATE THE LINE LABEL TEXTS
        for (Node n : data.getNodes()) {
            if (n instanceof DraggableText && ((DraggableText) n).getText().equals(oldLine.getId())) {
                ((DraggableText) n).setText(oldName);
            }
        }
        oldLine.setId(oldName);
        oldLine.setIsCircular(oldIsCircular);
    }
}
