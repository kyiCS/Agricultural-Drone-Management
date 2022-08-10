package uab.cs420.farm_dashboard;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public abstract class Component {
    private String name;
    private float price;
    protected Point2D position;
    private Point3D size;
    private Node representation;
    private Tooltip tooltip;
    private TreeItem<String> treeItem;
    private ItemContainer parent = null;


    Component(String name, float price, Point2D position, Point3D size) {
        this.name = name;
        this.price = price;
        this.position = position;
        this.size = size;
        this.setTreeItem(new TreeItem<String>(name));

        //Modified default rectangle to take potential default position and not be transparent
        Rectangle r = new Rectangle(position.getX(), position.getY(), size.getX(), size.getY());
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.RED);
        r.setStrokeType(StrokeType.INSIDE);
        r.setStrokeWidth(5);
        representation = r;

        tooltip = new Tooltip(name);
        Tooltip.install(representation, tooltip);
    }

    Component(String name, float price, Point2D position, Point3D size, Node representation) {
        this(name, price, position, size);
        this.representation = representation;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        tooltip.setText(name);
    }

    public ItemContainer getParent() {
        return this.parent;
    }

    public void setParent(ItemContainer itemC) {
        this.parent = itemC;
    }

    public abstract Float getMarketValue();

    public abstract Component findByTreeItem(TreeItem<String> treeItem);

    public TreeItem<String> getTreeItem() {
        return this.treeItem;
    }

    public void setTreeItem(TreeItem<String> treeItem) { this.treeItem = treeItem; }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTotalPrice() { return this.price; }

    public abstract float getTotalMarketValue();

    public Point2D getPosition() {
        return this.position;
    }

    public void setPosition(Point2D point) {
        this.representation.setLayoutX(point.getX());
        this.representation.setLayoutY(point.getY());
        this.position = point;
    }

    public void moveRepresentation(Point2D point){

    }

    public void setPosition(double x, double y) {
        this.setPosition(new Point2D(x, y));
    }

    public Point3D getSize() {
        return this.size;
    }

    public void setSize(Point3D dimensions) {
        if (this.size.equals(dimensions)) return;
        this.size = dimensions;
        if (this.representation instanceof Rectangle) {
            Rectangle s = (Rectangle) this.representation;
            s.setWidth(this.size.getX());
            s.setHeight(this.size.getY()); // Item has 3D size, rectangle is only 2D
        }
    }

    public void setSize(double width, double height, double length) {
        this.setSize(new Point3D(width, height, length));
    }

    public Point2D getCenter() {
        return new Point2D(
                this.position.getX() + this.size.getX() / 2,
                this.position.getY() + this.size.getY() / 2
        );
    }

    public void setCenter(Point2D point) {
        this.setPosition(new Point2D(
                (point.getX() - this.size.getX()) / 2,
                (point.getY() - this.size.getY()) / 2
        ));
    }

    public Node getRepresentation() {
        return this.representation;
    }

    // The Component's size and position will be set based on the Node's relative to its parent
    public void setRepresentation(Node representation) {
        this.representation = representation;
        Bounds localBounds = representation.getBoundsInParent();
        this.setPosition(localBounds.getMinX(), localBounds.getMinY());
        this.setSize(localBounds.getWidth(), localBounds.getHeight(), localBounds.getDepth());
    }

    public void drawRepresentation(Pane canvas) {
        canvas.getChildren().add(this.representation);
    }

    public void eraseRepresentation(Pane canvas) {
        canvas.getChildren().remove(this.representation);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
