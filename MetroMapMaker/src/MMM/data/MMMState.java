package MMM.data;

/**
 * This enum has the various possible states of the logo maker app
 * during the editing process which helps us determine which controls
 * are usable or not and what specific user actions should affect.
 * 
 * @author Doeun Kim
 * @version 1.0
 */
public enum MMMState {
    SELECTING_STATION,
    SELECTING_LINE,
    SELECTING_ELEMENT,
    
    ADDING_LINE,
    ADDING_STATION,
    ADDING_ELEMENT,
    
    MOVING_LINE_END,
    MOVING_STATION,
    MOVING_ELEMENT,
    
    REMOVING_STATION,
    REMOVING_ELEMENT,
    
    DOING_NOTHING,
    
    
    
}
