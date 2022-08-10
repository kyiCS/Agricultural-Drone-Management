package uab.cs420.farm_dashboard;

class PricingVisitor implements Visitor{
	private float price;
	
	public PricingVisitor() {
		price = 0;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void visit(Component c) {
		price += c.getTotalPrice();
	}
}
