package djf.controller;

import djf.ui.AppYesNoCancelDialogSingleton;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppGUI;
import djf.components.AppDataComponent;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import properties_manager.PropertiesManager;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import static djf.settings.AppPropertyType.NEW_COMPLETED_MESSAGE;
import static djf.settings.AppPropertyType.NEW_COMPLETED_TITLE;
import static djf.settings.AppPropertyType.NEW_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.NEW_ERROR_TITLE;
import static djf.settings.AppPropertyType.SAVE_COMPLETED_MESSAGE;
import static djf.settings.AppPropertyType.SAVE_COMPLETED_TITLE;
import static djf.settings.AppPropertyType.SAVE_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.SAVE_ERROR_TITLE;
import static djf.settings.AppPropertyType.SAVE_UNSAVED_WORK_MESSAGE;
import static djf.settings.AppPropertyType.SAVE_UNSAVED_WORK_TITLE;
import static djf.settings.AppPropertyType.SAVE_WORK_TITLE;
import djf.settings.AppStartupConstants;
import static djf.settings.AppStartupConstants.PATH_WORK;
import djf.ui.AppYesNoDialogSingleton;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class provides the event programmed responses for the file controls that
 * are provided by this framework.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class AppFileController {

    // HERE'S THE APP
    AppTemplate app;

    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    boolean saved;

    // THIS IS THE FILE FOR THE WORK CURRENTLY BEING WORKED ON
    File currentWorkFile;

    /**
     * This constructor just keeps the app for later.
     *
     * @param initApp The application within which this controller will provide
     * file toolbar responses.
     */
    public AppFileController(AppTemplate initApp) {
        // NOTHING YET
        saved = true;
        app = initApp;
    }

    public File getCurrentWorkFile() {
        return currentWorkFile;
    }

    public void setCurrentWorkFile(File f) {
        currentWorkFile = f;
    }
    public void setSaved(){
        saved=true;
        app.getGUI().updateToolbarControls(saved);
    }

    /**
     * This method marks the appropriate variable such that we know that the
     * current Work has been edited since it's been saved. The UI is then
     * updated to reflect this.
     *
     * @param gui The user interface editing the Work.
     */
    public void markAsEdited(AppGUI gui) {
        // THE WORK IS NOW DIRTY
        saved = false;

        // LET THE UI KNOW
        gui.updateToolbarControls(saved);
    }

    /**
     * This method starts the process of editing new Work. If work is already
     * being edited, it will prompt the user to save it first.
     *
     */
    public void handleNewRequest() {
        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave();
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                Stage promptStage = new Stage();
                TextField textField = new TextField();
                Button ok = new Button("OK");
                Button close = new Button("Close");
                HBox buttons = new HBox(ok, close);
                buttons.setSpacing(20);
                VBox pane = new VBox(textField, buttons);
                pane.setPadding(new Insets(60, 80, 60, 80));
                pane.setSpacing(20);
                File repo = new File(AppStartupConstants.PATH_EXPORT);
                File[] files = repo.listFiles();
                ArrayList<String> fileNames = new ArrayList<>();
                for (File f : files) {
                    fileNames.add(f.getName());
                }
                ok.setOnAction(e -> {
                    if (textField.getText() != null && !textField.getText().isEmpty()) {
                        if (fileNames.contains(textField.getText())) {
                            Stage errorStage = new Stage();
                            errorStage.setTitle("The name is already existed");
                            Label label = new Label(textField.getText() + " is already existed. Change the name.");
                            label.setPadding(new Insets(60, 80, 60, 80));
                            Scene errorScene = new Scene(label);
                            errorStage.setScene(errorScene);
                            errorStage.show();
                        } else {
                            promptStage.close();
                            Path path = FileSystems.getDefault().getPath(AppStartupConstants.PATH_EXPORT + "/" + textField.getText());
                            try {
                                Files.createDirectory(path);
                                File newFile = new File(AppStartupConstants.PATH_WORK + "/" + textField.getText() + " Metro.json");
                                String p = newFile.getPath();
                                // RESET THE WORKSPACE
                                app.getWorkspaceComponent().resetWorkspace();

                                // RESET THE DATA
                                app.getDataComponent().resetData();
                                app.getFileComponent().saveData(app.getDataComponent(), newFile.getPath());
                                saved = true;
                                currentWorkFile = newFile;

                                // NOW RELOAD THE WORKSPACE WITH THE RESET DATA
                                //app.getWorkspaceComponent().reloadWorkspace(app.getDataComponent());

                                // MAKE SURE THE WORKSPACE IS ACTIVATED
                                app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());

                                // WORK IS NOT SAVED
                                //saved = false;
                                // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
                                // THE APPROPRIATE CONTROLS
                                app.getGUI().updateToolbarControls(saved);

                                app.getGUI().getWindow().setTitle(props.getProperty(APP_TITLE) + " - " + textField.getText());
                                // TELL THE USER NEW WORK IS UNDERWAY
                                dialog.show(props.getProperty(NEW_COMPLETED_TITLE), props.getProperty(NEW_COMPLETED_MESSAGE));
                            } catch (Exception ex) {
                                dialog.show("Error in creating directory", "Error in creating directory");
                            }
                        }
                    } else {
                        Stage errorStage = new Stage();
                        errorStage.setTitle("No map name");
                        Label label = new Label("You didn't enter the name of the map");
                        Scene errorScene = new Scene(label);
                        label.setPadding(new Insets(60, 80, 60, 80));
                        errorStage.setScene(errorScene);
                        errorStage.show();
                    }
                });
                close.setOnAction(e -> {
                    promptStage.close();
                });
                Scene scene = new Scene(pane);
                promptStage.setTitle("Enter the map name");
                promptStage.setScene(scene);
                promptStage.show();
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG, PROVIDE FEEDBACK
            dialog.show(props.getProperty(NEW_ERROR_TITLE), props.getProperty(NEW_ERROR_MESSAGE));
        }
    }

    /**
     * This method lets the user open a Course saved to a file. It will also
     * make sure data for the current Course is not lost.
     *
     * @param gui The user interface editing the course.
     */
    public void handleLoadRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave();
            }

            // IF THE USER REALLY WANTS TO OPEN A Course
            if (continueToOpen) {
                // GO AHEAD AND PROCEED LOADING A Course
                promptToOpen();
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
        }
    }

    /**
     * This method will save the current course to a file. Note that we already
     * know the name of the file, so we won't need to prompt the user.
     *
     *
     * @param courseToSave The course being edited that is to be saved to a
     * file.
     */
    public void handleSaveRequest() {
        // WE'LL NEED THIS TO GET CUSTOM STUFF
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            saveWork(currentWorkFile);
            // MAYBE WE ALREADY KNOW THE FILE
//            if (currentWorkFile != null) {
//                saveWork(currentWorkFile);
//            } // OTHERWISE WE NEED TO PROMPT THE USER
//            else {
//                // PROMPT THE USER FOR A FILE NAME
//                FileChooser fc = new FileChooser();
//                fc.setInitialDirectory(new File(PATH_WORK));
//                fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
//                fc.getExtensionFilters().addAll(
//                        new ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), props.getProperty(WORK_FILE_EXT)));
//
//                File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
//                if (selectedFile != null) {
//                    saveWork(selectedFile);
//                }
//            }
        } catch (IOException ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(SAVE_ERROR_TITLE), props.getProperty(SAVE_ERROR_MESSAGE));
        }
    }

    public void handleSaveAsRequest() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(PATH_WORK));
            fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
            fc.getExtensionFilters().addAll(
                    new ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), props.getProperty(WORK_FILE_EXT)));

            File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
            if (selectedFile != null) {
                saveWork(selectedFile);
            }
        } catch (IOException ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(SAVE_ERROR_TITLE), props.getProperty(SAVE_ERROR_MESSAGE));
        }

    }

    // HELPER METHOD FOR SAVING WORK
    public void saveWork(File selectedFile) throws IOException {
        // SAVE IT TO A FILE
        app.getFileComponent().saveData(app.getDataComponent(), selectedFile.getPath());

        // MARK IT AS SAVED
        currentWorkFile = selectedFile;
        saved = true;

        // TELL THE USER THE FILE HAS BEEN SAVED
        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        dialog.show(props.getProperty(SAVE_COMPLETED_TITLE), props.getProperty(SAVE_COMPLETED_MESSAGE));

        // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
        // THE APPROPRIATE CONTROLS
        app.getGUI().updateToolbarControls(saved);
    }

    /**
     * This method will exit the application, making sure the user doesn't lose
     * any data first.
     *
     */
    public void handleExitRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToExit = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE
                continueToExit = promptToSaveForExit();
            }

            // IF THE USER REALLY WANTS TO EXIT THE APP
            if (continueToExit) {
                // EXIT THE APPLICATION
                System.exit(0);
            }
        } catch (IOException ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            dialog.show(props.getProperty(SAVE_ERROR_TITLE), props.getProperty(SAVE_ERROR_MESSAGE));
        }
    }

    private boolean promptToSaveForExit() throws IOException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // CHECK TO SEE IF THE CURRENT WORK HAS
        // BEEN SAVED AT LEAST ONCE
        // PROMPT THE USER TO SAVE UNSAVED WORK
        AppYesNoDialogSingleton yesNoDialog = AppYesNoDialogSingleton.getSingleton();
        yesNoDialog.show(props.getProperty(SAVE_UNSAVED_WORK_TITLE), props.getProperty(SAVE_UNSAVED_WORK_MESSAGE));

        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(AppYesNoCancelDialogSingleton.YES)) {
            // SAVE THE DATA FILE
            AppDataComponent dataManager = app.getDataComponent();

            if (currentWorkFile == null) {
                // PROMPT THE USER FOR A FILE NAME
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(PATH_WORK));
                fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
                fc.getExtensionFilters().addAll(
                        new ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), props.getProperty(WORK_FILE_EXT)));

                File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
                if (selectedFile != null) {
                    saveWork(selectedFile);
                    saved = true;
                }
            } else {
                saveWork(currentWorkFile);
                saved = true;
            }
        }
        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
        return true;
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. Note that it could be used
     * in multiple contexts before doing other actions, like creating new work,
     * or opening another file. Note that the user will be presented with 3
     * options: YES, NO, and CANCEL. YES means the user wants to save their work
     * and continue the other action (we return true to denote this), NO means
     * don't save the work but continue with the other action (true is
     * returned), CANCEL means don't save the work and don't continue with the
     * other action (false is returned).
     *
     * @return true if the user presses the YES option to save, true if the user
     * presses the NO option to not save, false if the user presses the CANCEL
     * option to not continue.
     */
    private boolean promptToSave() throws IOException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // CHECK TO SEE IF THE CURRENT WORK HAS
        // BEEN SAVED AT LEAST ONCE
        // PROMPT THE USER TO SAVE UNSAVED WORK
        AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
        yesNoDialog.show(props.getProperty(SAVE_UNSAVED_WORK_TITLE), props.getProperty(SAVE_UNSAVED_WORK_MESSAGE));

        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(AppYesNoCancelDialogSingleton.YES)) {
            // SAVE THE DATA FILE
            AppDataComponent dataManager = app.getDataComponent();

            if (currentWorkFile == null) {
                // PROMPT THE USER FOR A FILE NAME
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(PATH_WORK));
                fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
                fc.getExtensionFilters().addAll(
                        new ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), props.getProperty(WORK_FILE_EXT)));

                File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
                if (selectedFile != null) {
                    saveWork(selectedFile);
                    saved = true;
                }
            } else {
                saveWork(currentWorkFile);
                saved = true;
            }
        } // IF THE USER SAID CANCEL, THEN WE'LL TELL WHOEVER
        // CALLED THIS THAT THE USER IS NOT INTERESTED ANYMORE
        else if (selection.equals(AppYesNoCancelDialogSingleton.CANCEL)) {
            return false;
        }

        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
        return true;
    }

    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen() {
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // AND NOW ASK THE USER FOR THE FILE TO OPEN
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle(props.getProperty(LOAD_WORK_TITLE));
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                // RESET THE WORKSPACE
                app.getWorkspaceComponent().resetWorkspace();

                // RESET THE DATA
                app.getDataComponent().resetData();

                // LOAD THE FILE INTO THE DATA
                app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath(), app);

                // MAKE SURE THE WORKSPACE IS ACTIVATED
                app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());

                // AND MAKE SURE THE FILE BUTTONS ARE PROPERLY ENABLED
                saved = true;
                currentWorkFile = selectedFile;
                app.getGUI().updateToolbarControls(saved);
            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
            }
        }
    }

    /**
     * This mutator method marks the file as not saved, which means that when
     * the user wants to do a file-type operation, we should prompt the user to
     * save current work first. Note that this method should be called any time
     * the course is changed in some way.
     */
    public void markFileAsNotSaved() {
        saved = false;
    }

    /**
     * Accessor method for checking to see if the current work has been saved
     * since it was last edited.
     *
     * @return true if the current work is saved to the file, false otherwise.
     */
    public boolean isSaved() {
        return saved;
    }
}
