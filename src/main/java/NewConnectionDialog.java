/**
 * @author Andreas Hagenstam
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

// föreläsning 16
public class NewConnectionDialog extends Alert {

    private TextField nameField = new TextField();
    private TextField timeField = new TextField();

    public NewConnectionDialog(boolean editableName, boolean editableTime) {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.addRow(0, new Label("Name"), nameField);
        nameField.setEditable(editableName);
        grid.addRow(1, new Label("Time"), timeField);
        timeField.setEditable(editableTime);
        getDialogPane().setContent(grid);
        grid.setAlignment(Pos.CENTER);
        setTitle("Connection");
    }

    public String getName() {
        return nameField.getText();
    }

    public void setName(String name){
        nameField.setText(name);
    }

    public int getTime() {
        return Integer.parseInt(timeField.getText());
    }

    public void setTime(int time){
        timeField.setText(String.valueOf(time));
    }
}
