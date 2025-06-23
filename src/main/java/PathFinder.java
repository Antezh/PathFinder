// PROG2 VT2024, Inlämningsuppgift, del 2
// Grupp 100
// Andreas Hagenstam anha3549

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.shape.Line;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
/**
 * PathFinder är huvudklassen för applikationen. Den ansvarar för att starta JavaFX-appen.
 */
public class PathFinder extends Application {
    // Padding för scenens bredd
    private static final int STAGE_WIDTH_PADDING = 18;
    // Fönster och UI-komponenter
    private Stage primaryStage;
    private ImageView imageView;
    private boolean changed = false; // Flagga för att hålla koll på om något ändrats
    private Pane center; // Huvudpane där noder och linjer ritas

    // Grafstruktur och markerade noder
    private ListGraph<PlaceNode> graph;
    private List<PlaceNode> focusedNodes;

    private File file;

    // Knappreferenser
    private Button findPathBtn;
    private Button showConnectionBtn;
    private Button newPlaceBtn;
    private Button newConnectionBtn;
    private Button changeConnectionBtn;

    private FlowPane controls; // Rad med kontrollknappar
    private VBox vbox; // Innehåller meny och kontrollfält

    // Startmetod – bygger hela gränssnittet och initierar programmet
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);

        BorderPane rootLayout = new BorderPane();
        rootLayout.setMinWidth(600);
        center = new Pane();
        center.setId("outputArea");
        rootLayout.setCenter(center);
        imageView = new ImageView();

        graph = new ListGraph<>();
        focusedNodes = new ArrayList<>();

        menuSetup(rootLayout); // Skapa menyer (File)
        controlButtonsSetup(rootLayout); // Lägg till knappar

        Scene scene = new Scene(rootLayout);
        primaryStage.setTitle("PathFinder");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new ExitHandler()); // Hanterar stängning
        primaryStage.show();
    } // start

    // Skapar menyraden och lägger till File-menyn
    private void menuSetup(BorderPane rootLayout) {
        vbox = new VBox();
        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu"); // id för menuBar
        vbox.getChildren().add(menuBar);
        rootLayout.setTop(vbox);

        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile"); //id för fileMenu
        menuBar.getMenus().add(fileMenu);
        menuItems(fileMenu);
    } //menuSetup

    // Skapar och placerar alla kontrollknappar i gränssnittet
    private void controlButtonsSetup(BorderPane rootLayout) {
        controls = new FlowPane();
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
        controls.setHgap(20);
        vbox.getChildren().add(controls);

        findPathBtn = buttonCreator("Find Path","btnFindPath", new FindPathHandler());
        showConnectionBtn = buttonCreator("Show Connection","btnShowConnection", new ShowConnectionHandler());
        newPlaceBtn = buttonCreator("New Place", "btnNewPlace", new NewPlaceButtonHandler());
        newConnectionBtn = buttonCreator("New Connection","btnNewConnection", new NewConnectionHandler());
        changeConnectionBtn = buttonCreator("Change Connection","btnChangeConnection", new ChangeConnectionHandler());

        controls.getChildren().addAll(findPathBtn, showConnectionBtn, newPlaceBtn, newConnectionBtn, changeConnectionBtn);
    } // controlButtonsSetup

    // Hjälpmetod för att skapa knappar med ID och händelsehanterare
    private Button buttonCreator(String buttonName,String buttonId, EventHandler<ActionEvent> handler){
        Button button = new Button(buttonName);
        button.setId(buttonId);
        button.setDisable(true);
        button.setOnAction(handler);
        return button;
    } //buttonCreator

    // Skapar alla menyval i File-menyn
    private void menuItems(Menu fileMenu) {
        MenuItem newMapItem = new MenuItem("New Map");
        newMapItem.setId("menuNewMap"); //id för newMap
        fileMenu.getItems().add(newMapItem);
        newMapItem.setOnAction(new NewMapItemHandler());

        MenuItem openItem = new MenuItem("Open");
        openItem.setId("menuOpenFile"); //id för openItem
        fileMenu.getItems().add(openItem);
        openItem.setOnAction(new OpenItemHandler());

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setId("menuSaveFile"); //id för saveItem
        fileMenu.getItems().add(saveItem);
        saveItem.setOnAction(new SaveItemHandler());

        MenuItem saveImageItem = new MenuItem("Save Image");
        saveImageItem.setId("menuSaveImage"); //id för saveImageItem
        fileMenu.getItems().add(saveImageItem);
        saveImageItem.setOnAction(new SaveImageHandler());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setId("menuExit"); //id för exitItem
        fileMenu.getItems().add(exitItem);
        exitItem.setOnAction(new ExitItemHandler());
    } //menuItems

    // Anropas när ny karta laddas – rensar allt och aktiverar knappar
    private void startupMethod(String text) {
        graph = new ListGraph<>();
        center.getChildren().clear();

        Image image = new Image(text);
        imageView.setImage(image);

        // Aktiverar alla funktionella knappar
        findPathBtn.setDisable(false);
        showConnectionBtn.setDisable(false);
        newPlaceBtn.setDisable(false);
        changeConnectionBtn.setDisable(false);
        newConnectionBtn.setDisable(false);

        center.getChildren().add(imageView);

        primaryStage.setWidth(image.getWidth() + STAGE_WIDTH_PADDING);
        primaryStage.setHeight(image.getHeight() + vbox.getHeight() + controls.getHeight());
    } //startUpMethod

    // Skapar en ny platsnod och lägger till den i graf och pane
    private PlaceNode createPlaceNode(String name, double x, double y) {
        PlaceNode newNode = new PlaceNode(name, x, y);
        newNode.setId(name);
        graph.add(newNode);
        newNode.getLabel().setText(name);
        newNode.addToPane(center);
        newNode.setOnMouseClicked(new NodeClickHandler(newNode, focusedNodes));
        return newNode;
    } //createPlaceNode

    // Skapar en linje mellan två noder (för att rita upp kopplingar visuellt)
    private Line getLine(PlaceNode markedNodeA, PlaceNode markedNodeB) {
        Line connectionLine = new Line();
        connectionLine.setStartX(markedNodeA.getCenterX());
        connectionLine.setStartY(markedNodeA.getCenterY());
        connectionLine.setEndX(markedNodeB.getCenterX());
        connectionLine.setEndY(markedNodeB.getCenterY());
        connectionLine.setStroke(Color.BLACK);
        connectionLine.setStrokeWidth(3);
        connectionLine.setDisable(true);
        return connectionLine;
    } //hjälpmetod för att rita ut linjen

    // Rensar alla markerade (fokuserade) noder
    private void clearMarkedNodes() {
        if (!focusedNodes.isEmpty()) {
            for (PlaceNode node : focusedNodes) {
                node.setFocus(false);
            }
            focusedNodes.clear();
        }
    } // clearMarkedNodes

    // Returnerar true om det redan finns en kant mellan två noder
    private boolean edgeExistsBetween(PlaceNode from, PlaceNode to) {
        return graph.getEdgeBetween(from, to) != null;
    } //edgeExistsBetween

    // -------------------
    // Händelsehanterare för knappar
    // -------------------

    // Visar kortaste vägen mellan två markerade noder
    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (focusedNodes.size() < 2) {
                AlertCollection.showTwoNodesError();
                return;
            }  if (!graph.pathExists(focusedNodes.get(0), focusedNodes.get(1))) {
                AlertCollection.noConnectionExistsError();
                return;
            }
            List<Edge<PlaceNode>> path = graph.getPath(focusedNodes.get(0), focusedNodes.get(1));

            StringBuilder pathString = new StringBuilder();
            int totalWeight = 0;
            for (Edge<PlaceNode> edge : path) {
                PlaceNode toNode = edge.getDestination();
                String edgeName = edge.getName();
                int edgeWeight = edge.getWeight();

                pathString.append("to ")
                        .append(toNode.getName())
                        .append(" by ")
                        .append(edgeName)
                        .append(" takes ")
                        .append(edgeWeight)
                        .append("\n");

                totalWeight += edgeWeight;
            }
            pathString.append("Total ").append(totalWeight);

            findPathDialog(pathString);
        }

        // Visar vägen i en popup-ruta
        private void findPathDialog(StringBuilder pathString) {
            Alert pathDialog = new Alert(Alert.AlertType.INFORMATION);
            pathDialog.setTitle("Message");
            pathDialog.setHeaderText("The Path from " + focusedNodes.get(0).getName() + " to " + focusedNodes.get(1).getName() + ":");
            TextArea textArea = new TextArea(pathString.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            pathDialog.getDialogPane().setContent(textArea);
            pathDialog.getButtonTypes().setAll(ButtonType.OK);
            pathDialog.showAndWait();
        }
    } // FindPathHandler

    // Visar information om kopplingen mellan två markerade noder
    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (focusedNodes.size() < 2) {
                AlertCollection.showTwoNodesError();
                return;
            }
            if (focusedNodes.get(0) == null || focusedNodes.get(1) == null) {
                AlertCollection.noConnectionExistsError();
                return;
            }
            if (graph == null) {
                AlertCollection.noConnectionExistsError();
                return;
            } if (!edgeExistsBetween(focusedNodes.get(0), focusedNodes.get(1))) {
                AlertCollection.noConnectionExistsError();
                return;
            }
            String connectionName = graph.getEdgeBetween(focusedNodes.get(0), focusedNodes.get(1)).getName();
            int connectionTime = graph.getEdgeBetween(focusedNodes.get(0), focusedNodes.get(1)).getWeight();
            NewConnectionDialog form = new NewConnectionDialog(false, false);
            form.setName(connectionName);
            form.setTime(connectionTime);
            form.setHeaderText("Connection from " + focusedNodes.get(0).getName() + " to " + focusedNodes.get(1).getName());
            form.showAndWait();
        }
    } //ShowConnectionHandler

    // Tillåter användaren att skapa en ny plats genom att klicka på kartan
    class NewPlaceButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            clearMarkedNodes();

            newPlaceBtn.setDisable(true);

            center.setOnMouseClicked(mouseEvent -> {

                double x = mouseEvent.getX();
                double y = mouseEvent.getY();

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("New place");
                dialog.setHeaderText(null);
                dialog.setContentText("Name of place:");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent() && !result.get().trim().isEmpty()) {
                    String name = result.get().trim();

                    createPlaceNode(name, x, y);

                    changed = true;
                }
                newPlaceBtn.setDisable(false);
                center.setOnMouseClicked(null);
                center.setCursor(Cursor.DEFAULT);
            });
            center.setCursor(Cursor.CROSSHAIR);
        }
    } // NewPlaceButtonHandler

    // Skapar en ny koppling (kant) mellan två markerade noder
    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (focusedNodes.size() < 2) {
                AlertCollection.showTwoNodesError();
                return;
            }
            if (edgeExistsBetween(focusedNodes.get(0), focusedNodes.get(1))) {
                AlertCollection.showConnectionExistsError();
                return;
            }
            if (!graph.getNodes().contains(focusedNodes.get(0)) || !graph.getNodes().contains(focusedNodes.get(1))) {
                AlertCollection.showAlertError("Error", "Node does not exist in graph error");
                return;
            }
            try {
                NewConnectionDialog form = new NewConnectionDialog(true, true);
                form.setHeaderText("Connection from " + focusedNodes.get(0).getName() + " to " + focusedNodes.get(1).getName());
                Optional<ButtonType> result = form.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return;
                }
                String name = form.getName();
                if (name.strip().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Empty name!");
                    alert.showAndWait();
                    return;
                }
                int time = form.getTime();
                graph.connect(focusedNodes.get(0), focusedNodes.get(1), name, time);

                //Linje kod
                Line connectionLine = getLine(focusedNodes.get(0), focusedNodes.get(1));
                center.getChildren().add(connectionLine);

                changed = true;

            } catch (NumberFormatException e) {
                AlertCollection.showAlertError("Error", "Non-numerical input");
            } //catch
        } //handle
    } // NewConnectionHandler

    // Ändrar vikten på en befintlig koppling mellan två noder
    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (focusedNodes.size() < 2) {
                AlertCollection.showTwoNodesError();
                return;
            }
            if (graph.getEdgeBetween(focusedNodes.get(0), focusedNodes.get(1)) == null) {
                AlertCollection.noConnectionExistsError();
                return;
            }
            try {
                String connectionName = graph.getEdgeBetween(focusedNodes.get(0), focusedNodes.get(1)).getName();
                NewConnectionDialog form = new NewConnectionDialog(false, true);
                form.setName(connectionName);
                form.setHeaderText("Connection from " + focusedNodes.get(0) + " to " + focusedNodes.get(1)); // ändrade till id
                form.showAndWait().ifPresent(result -> {
                    int newTime = form.getTime();
                    graph.setConnectionWeight(focusedNodes.get(0), focusedNodes.get(1), newTime);

                    focusedNodes.get(0).setFocus(true);
                    focusedNodes.get(1).setFocus(true);
                    changed = true;
                });
            } catch (NumberFormatException e) {
                AlertCollection.showAlertError("Error", "Non-numerical input");
            } //catch
        } //handle
    } //ChangeConnectionHandler

    // Hanterare för menyvalen
    class NewMapItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (!AlertCollection.unsavedChangesAlert(changed)) {
                return;
            }
            graph = new ListGraph<>(); // Skapar grafen
            startupMethod("file:europa.gif");

            changed = true;
        } //handle
    } // NewMapItemHandler

    class OpenItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (!AlertCollection.unsavedChangesAlert(changed)) {
                return;
            }
            center.getChildren().clear();
            graph = new ListGraph<>();
            try {
                file = new File("europa.graph");

                if (!file.exists()) {
                    AlertCollection.fileError("Graph file does not exist");
                    return;
                }
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    clearMarkedNodes();

                    String imagePath = br.readLine();

                    startupMethod(imagePath);

                    String line;
                    Map<String, PlaceNode> nodesMap = new HashMap<>();
                    line = br.readLine();

                    if (line != null) {
                        String[] nodeSplit = line.split(";");
                        for (int i = 0; i < nodeSplit.length; i += 3) {
                            String nodeName = nodeSplit[i];
                            double x = Double.parseDouble(nodeSplit[i + 1]);
                            double y = Double.parseDouble(nodeSplit[i + 2]);

                            PlaceNode newPlaceNode = createPlaceNode(nodeName, x, y);
                            nodesMap.put(nodeName, newPlaceNode);
                        }
                    }

                    while ((line = br.readLine()) != null) {
                        String[] connectionSplit = line.split(";");
                        if (connectionSplit.length == 4) {
                            String nodeFrom = connectionSplit[0];
                            String nodeTo = connectionSplit[1];
                            String connectionName = connectionSplit[2];
                            int weight = Integer.parseInt(connectionSplit[3]);

                            PlaceNode fromNode = nodesMap.get(nodeFrom);
                            PlaceNode toNode = nodesMap.get(nodeTo);

                            if (fromNode != null && toNode != null) {

                                if (graph.getEdgeBetween(fromNode, toNode) == null) {

                                    graph.connect(fromNode, toNode, connectionName, weight);

                                    Line connectionLine = getLine(fromNode, toNode);
                                    center.getChildren().add(connectionLine);
                                } // if edge is null
                            } // if the node is not null
                        } else {
                            AlertCollection.fileError("Connection nodes not found!");
                        } //else
                    } // While
                    changed = false;
                } catch (IOException e) {
                    AlertCollection.fileError("IO Exception!" + e.getMessage());
                }// catch
            } catch (Exception e) {
                AlertCollection.fileError("Cant access file \"europa.graph\"" + e.getMessage());
            }
        } // handle
    } // OpenItemHandler

    class SaveItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            file = new File("europa.graph");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("file:europa.gif\n");

                for (PlaceNode node : graph.getNodes()) {
                    bw.write(node.getName() + ";" + node.getCenterX() + ";" + node.getCenterY() + ";"); //inga mellanslag förens sista
                } //for
                bw.write("\n");

                for (PlaceNode node : graph.getNodes()) {
                    for (Edge<PlaceNode> edge : graph.getEdgesFrom(node)) {
                        PlaceNode dest = edge.getDestination();
                        String connectionName = edge.getName();
                        int time = edge.getWeight();

                        bw.write(node.getName() + ";" + dest.getName() + ";" + connectionName + ";" + time + "\n");
                    } // nested-for
                } // for

                changed = false;
            } catch (IOException e) {
                AlertCollection.fileError("IO Exception!" + e.getMessage());
            } //catch
        } //handle
    } // SaveItemHandler

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                WritableImage image = center.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO-Error " + e.getMessage());
                alert.showAndWait();
            } //catch
        } //handle
    } // SaveImageHandler

    class ExitItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        } //handle
    } // ExitItemHandler

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent windowEvent) {
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning!");
                alert.setHeaderText(null);
                alert.setContentText("Unsaved changes. Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().equals(ButtonType.CANCEL)) {
                    windowEvent.consume();
                } // if
            } // if (changed)
        } //handle
    } // ExitHandler
} //PathFinder
