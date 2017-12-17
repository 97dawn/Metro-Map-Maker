package MMM;

import java.util.Locale;
import MMM.data.MMMData;
import MMM.file.MMMFiles;
import MMM.gui.MMMWorkspace;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import djf.settings.AppStartupConstants;
import djf.ui.AppMessageDialogSingleton;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 * This class serves as the application class for our MMMApp program. Note that
 * much of its behavior is inherited from AppTemplate, as defined in the Desktop
 * Java Framework. This app starts by loading all the app-specific messages like
 * icon files and tooltips and other settings, then the full User Interface is
 * loaded using those settings. Note that this is a JavaFX application.
 *
 * @author Doeun Kim
 * @version 1.0
 */
public class MMMApp extends AppTemplate {

    /**
     * This hook method must initialize all three components in the proper order
     * ensuring proper dependencies are respected, meaning all proper objects
     * are already constructed when they are needed for use, since some may need
     * others for initialization.
     */
    @Override
    public void buildAppComponentsHook() {
        constructWelcomePage();
    }

    public void constructWelcomePage() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Stage stage = new Stage();
        Label recentWork = new Label("Recent Work");
        recentWork.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 15));
        FlowPane welcomeLeft = new FlowPane(Orientation.VERTICAL, recentWork);
        VBox welcomeRight = new VBox();
        File work = new File(AppStartupConstants.PATH_WORK);
        File[] dirs = work.listFiles();
        // sort buttons based on the modified time
        for (int i = 0; i < dirs.length; i++) {
            for (int j = i + 1; j < dirs.length; j++) {
                if (dirs[i].lastModified() < dirs[j].lastModified()) {
                    File temp = dirs[i];
                    dirs[i] = dirs[j];
                    dirs[j] = temp;
                }
            }
        }
        if (dirs != null) {
            for (File f : dirs) {
                String fileName = f.getName().replaceAll(" Metro.json", "");
                Button openFile = new Button(fileName);
                openFile.setOnAction(e -> {
                    stage.close();
                    fileComponent = new MMMFiles();
                    dataComponent = new MMMData(this);
                    workspaceComponent = new MMMWorkspace(this);
                    try {
                        this.getWorkspaceComponent().resetWorkspace();
                        this.getDataComponent().resetData();
                        this.getFileComponent().loadData(this.getDataComponent(), f.getAbsolutePath(), this);
                        this.getWorkspaceComponent().activateWorkspace(this.getGUI().getAppPane());
                        this.getGUI().updateToolbarControls(true);
                        this.getGUI().getWindow().setTitle(props.getProperty(APP_TITLE) + " - " + openFile.getText());
                        this.getGUI().getFileController().setCurrentWorkFile(f);
                        ((MMMWorkspace) workspaceComponent).getExport().setDisable(false);
                        ((MMMWorkspace) workspaceComponent).getSaveAs().setDisable(false);
                        this.getGUI().getWindow().show();
                    } catch (Exception ex) {
                        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                        dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
                    }
                });
                welcomeLeft.getChildren().add(openFile);
            }
        }
        ImageView logo = new ImageView(new Image(AppStartupConstants.FILE_PROTOCOL + AppStartupConstants.PATH_IMAGES + "MMMlogo.png"));
        Button buttonForOpenMainPage = new Button("Create New Metro Map");
        buttonForOpenMainPage.setOnAction(e -> {
            ArrayList<String> fileNames = new ArrayList<>();
            for (File f : dirs) {
                fileNames.add(f.getName().replace(" Metro.json", ""));
            }
            promptNameDialog(fileNames, stage);

        });
        welcomeRight.getChildren().addAll(logo, buttonForOpenMainPage);
        welcomeRight.setSpacing(100);
        welcomeRight.setPadding(new Insets(50, 50, 50, 50));
        welcomeRight.setAlignment(Pos.CENTER);
        welcomeLeft.setPadding(new Insets(50, 50, 50, 50));
        welcomeLeft.setVgap(10);
        welcomeRight.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        welcomeLeft.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        BorderPane welcome = new BorderPane();
        welcome.setLeft(welcomeLeft);
        welcome.setRight(welcomeRight);
        Scene scene = new Scene(welcome);
        stage.setScene(scene);
        stage.setTitle("Welcome to the Metro Map Maker");
        stage.setOnCloseRequest(eh -> {
            stage.close();
            fileComponent = new MMMFiles();
            dataComponent = new MMMData(this);
            workspaceComponent = new MMMWorkspace(this);
            this.getGUI().getWindow().show();
            ((MMMWorkspace)workspaceComponent).getExport().setDisable(true);
            ((MMMWorkspace)workspaceComponent).getSaveAs().setDisable(true);
        });
        stage.show();
    }

    private void promptNameDialog(ArrayList<String> fileNames, Stage stage) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Stage promptStage = new Stage();
        TextField textField = new TextField();
        Button ok = new Button("OK");
        Button close = new Button("Close");
        HBox buttons = new HBox(ok, close);
        buttons.setSpacing(20);
        VBox pane = new VBox(textField, buttons);
        pane.setPadding(new Insets(60, 80, 60, 80));
        pane.setSpacing(20);
        ok.setOnAction(e -> {
            if (textField.getText() != null && !textField.getText().isEmpty()) {
                if (fileNames.contains(textField.getText())) {
                    Stage errorStage = new Stage();
                    errorStage.setTitle("Existing map name");
                    Label label = new Label(textField.getText() + " already exists. Change the name.");
                    Scene errorScene = new Scene(label);
                    label.setPadding(new Insets(60, 80, 60, 80));
                    errorStage.setScene(errorScene);
                    errorStage.show();
                } else { //CREATE NEW FILE
                    stage.close();
                    promptStage.close();
                    try {
                        fileComponent = new MMMFiles();
                        dataComponent = new MMMData(this);
                        workspaceComponent = new MMMWorkspace(this);
                        Path path = FileSystems.getDefault().getPath(AppStartupConstants.PATH_EXPORT + "/" + textField.getText());

                        Files.createDirectory(path);
                        File newFile = new File(AppStartupConstants.PATH_WORK + "/" + textField.getText() + " Metro.json");
                        this.getWorkspaceComponent().resetWorkspace();
                        // RESET THE DATA
                        this.getDataComponent().resetData();
                        // NOW RELOAD THE WORKSPACE WITH THE RESET DATA
                       // this.getWorkspaceComponent().reloadWorkspace(this.getDataComponent());
                        ((MMMWorkspace) workspaceComponent).getExport().setDisable(false);
                        ((MMMWorkspace) workspaceComponent).getSaveAs().setDisable(false);
                        // MAKE SURE THE WORKSPACE IS ACTIVATED
                        this.getWorkspaceComponent().activateWorkspace(this.getGUI().getAppPane());
                       // this.getGUI().getFileController().markAsEdited(gui);
                        this.getFileComponent().saveData(dataComponent, newFile.getPath());
                        this.getGUI().getFileController().setSaved();
                        this.getGUI().getFileController().setCurrentWorkFile(newFile);
                        this.getGUI().getWindow().setTitle(props.getProperty(APP_TITLE) + " - " + textField.getText());

                        this.getGUI().getWindow().show();
                    } catch (Exception ex) {
                        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
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

    /**
     * This is where program execution begins. Since this is a JavaFX app it
     * will simply call launch, which gets JavaFX rolling, resulting in sending
     * the properly initialized Stage (i.e. window) to the start method
     * inherited from AppTemplate, defined in the Desktop Java Framework.
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }
}
