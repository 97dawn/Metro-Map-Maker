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
public class RemoveElementTran implements jTPS_Transaction {
    private MMMData data;
    private Node node;
    //private int nodeIndex;
    
    public RemoveElementTran(MMMData initData, Node initNode) {
        data = initData;
        node = initNode;
        //nodeIndex = data.getIndexOfNode(node);
    }

    @Override
    public void doTransaction() {
        data.removeElement(node);
    }

    @Override
    public void undoTransaction() {
        data.addElement(node);
    }
}
