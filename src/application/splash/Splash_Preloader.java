package application.splash;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Splash_Preloader extends Preloader {
    private Stage stage;
    private ProgressBar progress = new ProgressBar();
    private Label lblStatus = new Label();

    @Override
    public void start(Stage splashStage) throws Exception {
        this.stage = splashStage;
        stage.initStyle(StageStyle.TRANSPARENT); // is also undecorated
        BorderPane root = new BorderPane();
        root.setId("splash");
        Scene scene = new Scene(root, 300, 300);
        scene.getStylesheets().addAll(
                this.getClass().getResource("splash.css").toExternalForm());
        stage.setScene(scene);

        root.setCenter(lblStatus);
        root.setBottom(progress);
        stage.show();
    }

    /**
     * Simple progress-bar notifications; values from 0 to 1
     */
    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        progress.setProgress(pn.getProgress());
    }

    /**
     * Provide more detail progress reports
     * @param info Details can be passed in the parameter; the parameter
     *            implements the PreloaderNotification interface. The
     *            application must create a class containing the information it
     *            needs
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof InfoToSplashScreen) {
            InfoToSplashScreen myInfo = (InfoToSplashScreen) info;
            lblStatus.setText(myInfo.getDetails());
        }
    }

    /**
     * Respond to standard state changes of the main program. Typically, a
     * splash screen will hide itself when the main application starts
     * @param evt The event that is being reported; see possibilities in the
     *            StateChangeNotification documentation
     */
    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }

    /**
     * If your program may report an error during initialization
     * @param info Details can be passed in the ErrorNotification object
     * @return true if the error has been displayed; false if Java needs to
     *         display its own error dialog
     */
    @Override
    public boolean handleErrorNotification(Preloader.ErrorNotification info) {
        lblStatus.setText(info.getDetails());
        return true;
    }

    /**
     * This class implements the PreloaderNotification interface, which is used
     * to pass application-notifications to the splash screen
     */
    public static class InfoToSplashScreen implements PreloaderNotification {
        private String details;

        public InfoToSplashScreen(String details) {
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }
}

