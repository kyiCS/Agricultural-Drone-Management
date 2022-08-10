package uab.cs420.farm_dashboard;


import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;

public class Controller {
    private static Controller controller = null;
    private final ItemContainer farm = new ItemContainer(
            "farm",
            0,
            new Point2D(0, 0),
            new Point3D(600, 800, 0)
    );
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Button addItemButton;
    @FXML
    private Button addItemConButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField nameTextBox;
    @FXML
    private TextField xCoordTextBox;
    @FXML
    private TextField yCoordTextBox;
    @FXML
    private TextField lengthTextBox;
    @FXML
    private TextField widthTextBox;
    @FXML
    private TextField heightTextBox;
    @FXML
    private TextField priceTextBox;
    @FXML
    private TextField marketValueTextBox;
    @FXML
    private Button saveButton;
    @FXML
    private Pane inputPane;
    @FXML
    private Pane plotPane;
    @FXML
    private Button initDrone;
    @FXML
    private Button scanFarm;
    @FXML
    private Button visitItem;
    @FXML
    private Button returnHome;
    @FXML
    private Label priceLabel;
    @FXML
    private Label marketValueLabel1;
    @FXML
    private Label marketValueLabel2;
    @FXML
    private Label marketValueTextBoxLabel;


    private int numItems = 0;

    private Drone drone;

    private ItemContainer commandCenter;

    private Main main;

    private Controller() {
    }

    public static Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
        }

        return controller;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * when the fxml file is loaded.
     */
    @FXML
    public void initialize() {
        this.farm.setSize(new Point3D(plotPane.getWidth(), plotPane.getHeight(), 0));
        treeView.setRoot(this.farm.getTreeItem());
        inputPane.setVisible(false);
    }

    private Component getSelectedItem() {
        TreeItem<String> selectedNode = treeView.getSelectionModel().getSelectedItem();
        if (selectedNode == null) {
            selectedNode = treeView.getRoot();
        }
        return this.farm.findByTreeItem(selectedNode);
    }

    @FXML
    private void addItem() {
        Component selectedComponent = this.getSelectedItem();
        ItemContainer selectedItemContainer;
        if (selectedComponent instanceof ItemContainer)
            selectedItemContainer = (ItemContainer) selectedComponent;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error Creating Item");
            alert.setContentText("Cannot add an Item to an Item.");
            alert.show();
            return;
        }

        Item newItem = new Item(
                "New_Item" + this.numItems,
                0,
                new Point2D(0, 0),
                new Point3D(50, 50, 0)
        );
        this.numItems++;
        selectedItemContainer.addItem(newItem);
        selectedItemContainer.getTreeItem().setExpanded(true);

        newItem.drawRepresentation(this.plotPane);
    }


    @FXML
    void addItemContainer() {
        Component selectedComponent = this.getSelectedItem();
        ItemContainer selectedItemContainer;
        if (selectedComponent instanceof ItemContainer)
            selectedItemContainer = (ItemContainer) selectedComponent;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error Creating Item");
            alert.setContentText("Cannot add an Item Container to an Item.");
            alert.show();
            return;
        }

        ItemContainer newItemContainer = new ItemContainer(
                "New_Item_Container" + this.numItems,
                0,
                new Point2D(0, 0),
                new Point3D(75, 75, 0)
        );
        this.numItems++;
        selectedItemContainer.addItem(newItemContainer);
        selectedItemContainer.getTreeItem().setExpanded(true);

        newItemContainer.drawRepresentation(plotPane);
    }

    @FXML
    void deleteSelected() {
        Component selectedComponent = this.getSelectedItem();
        ItemContainer parent = selectedComponent.getParent();
        if (parent == null) return;
        parent.removeItem(selectedComponent);
        selectedComponent.eraseRepresentation(plotPane);
        showSelectedItem();
    }

    @FXML
    void save() {
        Component selectedComponent = this.getSelectedItem();
        double x, y, l, w, h;
        float price, marketValue = 0;
        if (selectedComponent.getParent() == null) {
            return;
        }
        try {
            x = Double.parseDouble(xCoordTextBox.getText());
            y = Double.parseDouble(yCoordTextBox.getText());
            l = Double.parseDouble(lengthTextBox.getText());
            w = Double.parseDouble(widthTextBox.getText());
            h = Double.parseDouble(heightTextBox.getText());
            price = Float.parseFloat(priceTextBox.getText());
            if(selectedComponent instanceof Item)
                marketValue = Float.parseFloat(marketValueTextBox.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error Saving");
            alert.setContentText("Please enter the correct value types for each text field.");
            alert.show();
            return;
        }
        selectedComponent.getTreeItem().setValue(nameTextBox.getText());
        selectedComponent.setName(nameTextBox.getText());
        selectedComponent.setPosition(x, y);
        selectedComponent.setSize(l, w, h);
        selectedComponent.setPrice(price);
        selectedComponent.eraseRepresentation(plotPane);
        selectedComponent.drawRepresentation(plotPane);
        Item selectedItem;
        if(selectedComponent instanceof Item){
            selectedItem = (Item) selectedComponent;
            selectedItem.setMarketValue(marketValue);
            marketValueTextBoxLabel.setText(Float.toString(marketValue));
        }
        priceTextBox.setText(Float.toString(selectedComponent.getPrice()));
        priceLabel.setText(Float.toString(selectedComponent.getTotalPrice()));

    }

    @FXML
    void cancel() {
        treeView.getSelectionModel().clearSelection();
        inputPane.setVisible(false);
    }

    @FXML
    void showSelectedItem() {
        Component selectedComponent = this.getSelectedItem();
        PricingVisitor priceVis = new PricingVisitor();
        MarketValueVisitor marketValVis = new MarketValueVisitor();
        selectedComponent.accept(marketValVis);
        selectedComponent.accept(priceVis);
        if (selectedComponent.getParent() == null) {
            inputPane.setVisible(false);
        } else {
            inputPane.setVisible(true);
            nameTextBox.setText(selectedComponent.getName());
            xCoordTextBox.setText(Double.toString(selectedComponent.getPosition().getX()));
            yCoordTextBox.setText(Double.toString(selectedComponent.getPosition().getY()));
            lengthTextBox.setText(Double.toString(selectedComponent.getSize().getX()));
            widthTextBox.setText(Double.toString(selectedComponent.getSize().getY()));
            heightTextBox.setText(Double.toString(selectedComponent.getSize().getZ()));
            priceTextBox.setText(Double.toString(selectedComponent.getPrice()));
            priceLabel.setText(Float.toString(priceVis.getPrice()));
            marketValueTextBoxLabel.setText(Float.toString(marketValVis.getMarketValue()));
            final boolean isItem = (selectedComponent instanceof Item);
            marketValueTextBox.setVisible(isItem);
            marketValueLabel1.setVisible(isItem);
            if(isItem)
                marketValueTextBox.setText(Float.toString(selectedComponent.getMarketValue()));
        }
    }

    @FXML
    void initializeDrone() {
        //Init from selected item
        Component selectedComponent = this.getSelectedItem();
        ItemContainer selectedItemContainer;
        if (selectedComponent instanceof ItemContainer)
            selectedItemContainer = (ItemContainer) selectedComponent;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error initializing drone");
            alert.setContentText("Please select a valid Item Container.");
            alert.show();
            return;
        }

        //Generate Command Center, add to Tree, set default position
        commandCenter = new ItemContainer(
                "Command Center",
                0,
                new Point2D(0, 0),
                new Point3D(100, 100, 0)
        );
        selectedItemContainer.addItem(commandCenter);
        selectedItemContainer.getTreeItem().setExpanded(true);
        commandCenter.drawRepresentation(this.plotPane);

        //Setup Drone (keep up with it individually)
        selectedItemContainer = commandCenter;
        drone = new Drone(
                "drone",
                0,
                new Point2D(0, 0),
                new Point3D(50, 50, 0),
                commandCenter
        );
        try {
            drone.setPhysicalDrone(new TelloDrone());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        drone.getRepresentation().boundsInParentProperty();
        drone.drawRepresentation(this.plotPane);

        selectedItemContainer.addItem(drone);
        selectedItemContainer.getTreeItem().setExpanded(true);

        initDrone.setDisable(true);
        setDroneCommandsEnabled(true);
    }

    void setDroneCommandsEnabled(boolean enabled) {
       scanFarm.setDisable(!enabled);
       visitItem.setDisable(!enabled);
       returnHome.setDisable(!enabled);
    }

    @FXML
    void runFarmScan() throws IOException {
        setDroneCommandsEnabled(false);
        SequentialTransition t = drone.scanFarm(plotPane);
        t.setOnFinished(e -> setDroneCommandsEnabled(true));
        t.play();
    }

    @FXML
    void goToItem() throws IOException {
        Component selectedComponent = this.getSelectedItem();
        if (selectedComponent == farm) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Drone Movement Error");
            alert.setContentText("Drone cannot visit the Farm.");
            alert.show();
            return;
        }
        setDroneCommandsEnabled(false);
        SequentialTransition t = drone.goToItem(selectedComponent);
        t.setOnFinished(e -> setDroneCommandsEnabled(true));
        t.play();
    }

    @FXML
    void goBackHome() throws IOException {
        setDroneCommandsEnabled(false);
        SequentialTransition t = drone.goToHome();
        t.setOnFinished(e -> setDroneCommandsEnabled(true));
        t.play();
    }
}

