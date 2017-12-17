/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.MMMData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class SetImageBackgroundTran implements jTPS_Transaction{
    private ImageView image;
    private ImageView old;
    private Pane pane;
    private MMMData dataManager;
    public SetImageBackgroundTran(ImageView initImage, Pane initPane, MMMData data){
        image=initImage;
        pane = initPane;
        dataManager = data;
        if(pane.getChildren().size()>0 && pane.getChildren().get(0) instanceof ImageView){
            old = (ImageView)pane.getChildren().get(0);
        }
        else{
            old=null;
        }
    }
    @Override
    public void doTransaction() {
        dataManager.setBackgroundImage(image);
    }
    @Override
    public void undoTransaction() {
        dataManager.setBackgroundImage(old);
    }
}
