/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableText;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class ChangeTextSizeTran implements jTPS_Transaction{
    private DraggableText text;
    private double olds;
    private double news;
    public ChangeTextSizeTran(DraggableText initText, double initOld, double initNew){
        text=initText;
        olds=initOld;
        news=initNew;
    }
    @Override
    public void doTransaction(){
         if(text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, news));
        }
        else if(!text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
             text.setFont(Font.font(text.getFont().getFamily(),  FontPosture.ITALIC, news));
        }
        else if(text.getFont().getStyle().contains("Bold") && !text.getFont().getStyle().contains("Italic")){
             text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, news));
        }
        else{
             text.setFont(Font.font(text.getFont().getFamily(), news));
        }        
    }
    @Override
    public void undoTransaction(){
         if(text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, olds));
        }
        else if(!text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(text.getFont().getFamily(), FontPosture.ITALIC, olds));
        }
        else if(text.getFont().getStyle().contains("Bold") && !text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, olds));
        }
        else{
            text.setFont(Font.font(text.getFont().getFamily(), olds));
        }  
    }
}
