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
public class ChangeTextBoldTran implements jTPS_Transaction{
    private DraggableText text;
    private boolean oldb;
    public ChangeTextBoldTran(DraggableText initText, boolean old){
        text=initText;
        oldb=old;
    }
    @Override
    public void doTransaction(){
        if(text.getFont().getStyle().contains("Italic") && oldb == false){
            text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, text.getFont().getSize()));
        }
        else if(!text.getFont().getStyle().contains("Italic") && oldb == false){
             text.setFont(Font.font(text.getFont().getFamily(),  FontWeight.BOLD, text.getFont().getSize()));
        }
        else if(text.getFont().getStyle().contains("Italic") && oldb==true){
             text.setFont(Font.font(text.getFont().getFamily(), FontPosture.ITALIC,text.getFont().getSize()));
        }
        else{
             text.setFont(Font.font(text.getFont().getFamily(), text.getFont().getSize()));
        }        
    }
    @Override
    public void undoTransaction(){
         if(text.getFont().getStyle().contains("Italic") && oldb == true){
            text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, text.getFont().getSize()));
        }
        else if(!text.getFont().getStyle().contains("Italic") && oldb==true){
            text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, text.getFont().getSize()));
        }
        else if(text.getFont().getStyle().contains("Italic") && oldb==false){
            text.setFont(Font.font(text.getFont().getFamily(),FontPosture.ITALIC, text.getFont().getSize()));
        }
        else{
            text.setFont(Font.font(text.getFont().getFamily(), text.getFont().getSize()));
        }  
    }
}
