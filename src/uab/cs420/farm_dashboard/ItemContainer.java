package uab.cs420.farm_dashboard;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

public class ItemContainer extends Component {
    private ArrayList<Component> items = new ArrayList<>();


    ItemContainer(String name, float price, Point2D position, Point3D size) {
        super(name, price, position, size);
    }

    public void addItem(Component item) {
        this.items.add(item);
        item.setParent(this);
        this.getTreeItem().getChildren().add(item.getTreeItem());
    }

    public void clearItems() {
        items.clear();
    }

    @Override
    public Float getMarketValue(){ return new Float(0.0);}

    public void removeItem(Component item) {
        items.remove(item);
        this.getTreeItem().getChildren().remove(item.getTreeItem());
    }

    public float getTotalPrice() {
        float price = super.getPrice();
        for (Component i : items)
            price += i.getPrice();
        return price;
    }

    public float getTotalMarketValue() {
        float marketValue = getMarketValue();
        for (Component i : items)
            marketValue += i.getMarketValue();
        return marketValue;
    }

    public Component findByTreeItem(TreeItem<String> treeItem) {
        if (this.getTreeItem() == treeItem) {
            return this;
        }
        for (Component i : this.items) {
            Component found = i.findByTreeItem(treeItem);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void drawRepresentation(Pane canvas) {
        super.drawRepresentation(canvas);
        for (Component i : this.items) i.drawRepresentation(canvas);
    }

    public void eraseRepresentation(Pane canvas) {
        super.eraseRepresentation(canvas);
        for (Component i : this.items) i.eraseRepresentation(canvas);
    }
}
