package uab.cs420.farm_dashboard;

import javafx.animation.SequentialTransition;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.io.IOException;

public interface Animatable {
    public SequentialTransition goToPoint(Point2D point) throws IOException;
    public SequentialTransition goToItem(Component component) throws IOException;
    public SequentialTransition goToHome() throws IOException;
    public SequentialTransition scanFarm(Pane canvas) throws IOException;
}
