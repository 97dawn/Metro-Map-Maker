package MMM.data;

import java.util.ArrayList;
import javafx.scene.shape.Ellipse;

/**
 * This is a draggable ellipse for our goLogoLo application.
 * 
 * @author Doeun Kim
 * @version 1.0
 */
public class DraggableEllipse extends Ellipse implements Draggable {
    double startCenterX;
    double startCenterY;
    ArrayList<String> lineNames;
    
    public DraggableEllipse(String name) {
	setCenterX(100.0);
	setCenterY(100.0);
	setRadiusX(10.0);
	setRadiusY(10.0);
	setOpacity(1.0);
	startCenterX = getCenterX();
	startCenterY = getCenterY();
                  setId(name);
                  lineNames = new ArrayList<>();
    }
    public void addLineName(String m){
        lineNames.add(m);
    }
    public ArrayList<String> getLines(){
        return lineNames;
    }
//    @Override
//    public DraggableEllipse makeClone() {
//        DraggableEllipse cloneEllipse = new DraggableEllipse();
//        cloneEllipse.setRadiusX(getRadiusX());
//        cloneEllipse.setRadiusY(getRadiusY());
//        PropertiesManager props = PropertiesManager.getPropertiesManager();
//        cloneEllipse.setCenterX(Double.parseDouble(props.getProperty(MMMLanguageProperty.DEFAULT_NODE_X)));
//        cloneEllipse.setCenterY(Double.parseDouble(props.getProperty(MMMLanguageProperty.DEFAULT_NODE_Y)));
//        cloneEllipse.setOpacity(getOpacity());
//        cloneEllipse.setFill(getFill());
//        cloneEllipse.setStroke(getStroke());
//        cloneEllipse.setStrokeWidth(getStrokeWidth());
//        return cloneEllipse;
//    }
    
    
    @Override
    public void start(int x, int y) {
	startCenterX = x;
	startCenterY = y;
    }
    
    @Override
    public void setStart(int initStartX, int initStartY) {
        startCenterX = initStartX;
        startCenterY = initStartY;
    }
    
    @Override
    public void drag(int x, int y) {
	double diffX = x - startCenterX;
	double diffY = y - startCenterY;
	double newX = getCenterX() + diffX;
	double newY = getCenterY() + diffY;
	setCenterX(newX);
	setCenterY(newY);
	startCenterX = x;
	startCenterY = y;
    }
    
    @Override
    public void size(int x, int y) {
	double width = x - startCenterX;
	double height = y - startCenterY;
	double centerX = startCenterX + (width / 2);
	double centerY = startCenterY + (height / 2);
	setCenterX(centerX);
	setCenterY(centerY);
	setRadiusX(width / 2);
	setRadiusY(height / 2);	
	
    }
        
    @Override
    public double getX() {
	return getCenterX() - getRadiusX();
    }

    @Override
    public double getY() {
	return getCenterY() - getRadiusY();
    }

    @Override
    public double getWidth() {
	return getRadiusX() * 2;
    }

    @Override
    public double getHeight() {
	return getRadiusY() * 2;
    }
        
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	setCenterX(initX + (initWidth/2));
	setCenterY(initY + (initHeight/2));
	setRadiusX(initWidth/2);
	setRadiusY(initHeight/2);
    }
    
    @Override
    public String getNodeType() {
	return ELLIPSE;
    }
}
