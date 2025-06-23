
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.Objects;

public class PlaceNode extends Circle {

    private String name;
    private final Text label;
    private boolean focus;

    // Döpt PlaceNode då Node inte var godkänt av IntelliJ
    public PlaceNode(String name, double x, double y) {
        super(x, y, 10);
        this.name = name;
        this.setFill(Color.BLUE);
        this.setId(name);

        label = new Text(x,y+20,name); // sätter texten på objektet, med -15
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));

        focus = false;
    }

    public String getName() {
        return name;
    }

    public Text getLabel() {
        return label;
    }

    public void addToPane(Pane pane) {
        pane.getChildren().addAll(this, label);
    }

    // sätter fokus när klickad på, ClickHandler i pathfinder, ville inte samarbeta här
    public void setFocus(boolean hasFocus) {
        this.focus = hasFocus;
        if (hasFocus) {
            this.setFill(Color.RED);
        } else {
            this.setFill(Color.BLUE);
        }
    }

    public boolean hasFocus(){
        return focus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PlaceNode other = (PlaceNode) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
