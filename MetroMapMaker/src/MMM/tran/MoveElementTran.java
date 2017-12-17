/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableEllipse;
import MMM.data.DraggableImage;
import MMM.data.DraggableText;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class MoveElementTran implements jTPS_Transaction{
    private Node node;
    private double oldx;
    private double oldy;
    private double newx;
    private double newy;
    public MoveElementTran(Node initNode, double initOldX, double initOldY){
        node = initNode;
        oldx = initOldX;
        oldy = initOldY;
        if(node instanceof DraggableEllipse){
            newx = ((DraggableEllipse)node).getCenterX();
            newy = ((DraggableEllipse)node).getCenterY();
        }
        else if(node instanceof  DraggableImage){
            newx = ((DraggableImage)node).getX();
            newy = ((DraggableImage)node).getY();
        }
        else if(node instanceof DraggableText){
            newx = ((DraggableText)node).getX();
            newy = ((DraggableText)node).getY();
        }
        
    }
     @Override
    public void doTransaction(){
        if(node instanceof DraggableImage){
            ((DraggableImage)node).setX(newx);
            ((DraggableImage)node).setY(newy);
        }
        else if(node instanceof DraggableEllipse){
            ((DraggableEllipse)node).setCenterX(newx);
            ((DraggableEllipse)node).setCenterY(newy);
        }
        else if(node instanceof DraggableText){
             ((DraggableText)node).setX(newx);
            ((DraggableText)node).setY(newy);
        } 
    }
    @Override
    public void undoTransaction(){
         if(node instanceof DraggableImage){
            ((DraggableImage)node).setX(oldx);
            ((DraggableImage)node).setY(oldy);
        }
        else if(node instanceof DraggableEllipse){
            ((DraggableEllipse)node).setCenterX(oldx);
            ((DraggableEllipse)node).setCenterY(oldy);
        }
        else if(node instanceof DraggableText){
             ((DraggableText)node).setX(oldx);
            ((DraggableText)node).setY(oldy);
        } 
       
    }
}
