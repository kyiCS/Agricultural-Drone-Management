package uab.cs420.farm_dashboard;

public class MarketValueVisitor implements Visitor {
    private float marketValue;

    public MarketValueVisitor() {
        marketValue = 0;
    }

    public float getMarketValue() { return marketValue; }

    public void visit(Component c) { marketValue += c.getTotalMarketValue(); }
}
