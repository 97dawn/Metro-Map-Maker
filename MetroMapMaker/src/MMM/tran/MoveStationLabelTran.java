/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MMM.tran;

import MMM.data.DraggableEllipse;
import MMM.data.DraggableText;
import jtps.jTPS_Transaction;

/**
 *
 * @author Doeun Kim
 */
public class MoveStationLabelTran implements jTPS_Transaction {

    private DraggableEllipse station;
    private DraggableText label;

    public MoveStationLabelTran(DraggableEllipse initStation, DraggableText initLabel) {
        station = initStation;
        label = initLabel;
    }

    @Override
    public void doTransaction() {
        // TOP RIGHT
        if (label.getX() > station.getCenterX() && label.getY() < station.getCenterY()) {
            label.xProperty().unbind();
            label.setX(station.getCenterX() - station.getRadiusX() - label.getLayoutBounds().getWidth());
            label.xProperty().bind(station.centerXProperty().subtract(station.radiusXProperty()).subtract(label.getLayoutBounds().getWidth()));
        } // TOP LEFT
        else if (label.getX() < station.getCenterX() && label.getY() <station.getCenterY() ) {
            label.yProperty().unbind();
            label.setY(station.getCenterY() + station.getRadiusY());
            label.yProperty().bind(station.centerYProperty().add(station.radiusYProperty()));
        } // BOTTOM LEFT
        else if (label.getX() < station.getCenterX()  && label.getY() > station.getCenterY() ) {
            label.xProperty().unbind();
            label.yProperty().unbind();
            label.setX(station.getCenterX() + station.getRadiusX());
            label.setY(station.getCenterY() + station.getRadiusY());
            label.xProperty().bind(station.centerXProperty().add(station.radiusXProperty()));
            label.yProperty().bind(station.centerYProperty().add(station.radiusYProperty()));
        } // BOTTOM RIGHT
        else {
            label.yProperty().unbind();
            label.setY(station.getCenterY() - station.getRadiusY());
            label.yProperty().bind(station.centerYProperty().subtract(station.radiusYProperty()));
        }

    }

    @Override
    public void undoTransaction() {

        // TOP RIGHT
        if (label.getX() > station.getCenterX() && label.getY() < station.getCenterY()) {
            // BOTTOM RIGHT
            label.yProperty().unbind();
            label.setY(station.getCenterY() + station.getRadiusY());
            label.yProperty().bind(station.centerYProperty().add(station.radiusYProperty()));
        } // TOP LEFT
        else if (label.getX() < station.getCenterX() && label.getY() <station.getCenterY()) {
            // TOP RIGHT
            label.xProperty().unbind();
            label.setX(station.getCenterX() + station.getRadiusX());
            label.xProperty().bind(station.centerXProperty().add(station.radiusXProperty()));
        } // BOTTOM LEFT
        else if (label.getX() < station.getCenterX()  && label.getY() > station.getCenterY() ) {
            // TOP LEFT
            label.yProperty().unbind();
            label.setY(station.getCenterY() - station.getRadiusY());
            label.yProperty().bind(station.centerYProperty().subtract(station.radiusYProperty()));
        } // BOTTOM RIGHT
        else {
            // BOTTOM LEFT
            label.xProperty().unbind();
            label.setX(station.getCenterX() - station.getRadiusX());
            label.xProperty().bind(station.centerXProperty().subtract(station.radiusXProperty()).subtract(label.getLayoutBounds().getWidth()));
        }
    }

}
