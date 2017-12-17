/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.MMMData;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class AddElementTran implements jTPS_Transaction{
    private Node node;
    private MMMData data;
    public AddElementTran(Node initNode, MMMData initData){
        node = initNode;
        data= initData;
    }
     @Override
    public void doTransaction(){
        data.addElement(node);
    }
    @Override
    public void undoTransaction(){
        data.removeElement(node);
    }
}
