/**
 * @author Andreas Hagenstam
 */
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertCollection {

    public static boolean unsavedChangesAlert(boolean changed){
        if (changed) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            alert.setHeaderText(null);
            alert.setContentText("Unsaved changes. Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        }
        return true;
    }

    public static void showAlertError(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } //showAlert

    public static void showTwoNodesError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Two places must be selected");
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    } //showTwoNodesError

    public static void showConnectionExistsError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("These nodes already have a connection");
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    } // showConnectionExistsError

    public static void fileError(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    } //fileError

    public static void noConnectionExistsError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("These nodes dont have a connection");
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    } // noConnectionExistsError

}
