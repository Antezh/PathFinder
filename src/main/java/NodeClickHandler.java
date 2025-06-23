import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;


public class NodeClickHandler implements EventHandler<MouseEvent> {
    private PlaceNode node;
    private List<PlaceNode> focusedNodes;

    public NodeClickHandler(PlaceNode node, List<PlaceNode> focusedNodes) {
        this.node = node;
        this.focusedNodes = focusedNodes;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (node.hasFocus()){
            node.setFocus(false);
            focusedNodes.remove(node);
        } else {
            if (focusedNodes.size() < 2){
                node.setFocus(true);
                focusedNodes.add(node);
            }
        }
    }
}
