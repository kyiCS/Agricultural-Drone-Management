package uab.cs420.farm_dashboard;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.TreeItem;


public class Item extends Component {
    private float marketValue;

    Item(String name, float price, Point2D position, Point3D size) {
        super(name, price, position, size);
    }

    public void setMarketValue(float marketValue){ this.marketValue = marketValue;}

    @Override
    public Float getMarketValue(){ return this.marketValue;}

    public float getTotalMarketValue() { return this.marketValue; }

    public Item findByTreeItem(TreeItem<String> treeItem) {
        if (this.getTreeItem() == treeItem) {
            return this;
        }
        return null;
    }
}
