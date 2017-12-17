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
public class ChangeTextFontTran implements jTPS_Transaction{
    private DraggableText text;
    private String oldFam;
    private String newFam;
    public ChangeTextFontTran(DraggableText initText, String oldFam, String newFam){
        this.oldFam=oldFam;
        text=initText;
        this.newFam = newFam;
    }
    @Override
    public void doTransaction(){
        if(text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(newFam, FontWeight.BOLD, FontPosture.ITALIC, text.getFont().getSize()));
        }
        else if(!text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
             text.setFont(Font.font(newFam,  FontPosture.ITALIC, text.getFont().getSize()));
        }
        else if(text.getFont().getStyle().contains("Bold") && !text.getFont().getStyle().contains("Italic")){
             text.setFont(Font.font(newFam, FontWeight.BOLD, text.getFont().getSize()));
        }
        else{
             text.setFont(Font.font(newFam, text.getFont().getSize()));
        }        
    }
    @Override
    public void undoTransaction(){
         if(text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(oldFam, FontWeight.BOLD, FontPosture.ITALIC, text.getFont().getSize()));
        }
        else if(!text.getFont().getStyle().contains("Bold") && text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(oldFam, FontPosture.ITALIC, text.getFont().getSize()));
        }
        else if(text.getFont().getStyle().contains("Bold") && !text.getFont().getStyle().contains("Italic")){
            text.setFont(Font.font(oldFam, FontWeight.BOLD, text.getFont().getSize()));
        }
        else{
            text.setFont(Font.font(oldFam, text.getFont().getSize()));
        }  
    }
}
