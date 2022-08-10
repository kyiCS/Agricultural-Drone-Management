package uab.cs420.farm_dashboard;

import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.IOException;


public class Drone extends Item implements Animatable {

    private final ItemContainer droneCommandCenter;

    private FlightControllable physicalDrone;

    Drone(String name, float price, Point2D position, Point3D size, ItemContainer commander) {
        super(name, price, position, size);
        ImageView image = new ImageView(new Image("src_drone.png"));
        image.setFitHeight(size.getY());
        image.setFitWidth(size.getX());
        image.setX(position.getX());
        image.setY(position.getY());
        super.setRepresentation(image);
        this.droneCommandCenter = commander;
    }

    public void setPhysicalDrone(FlightControllable drone) {
        this.physicalDrone = drone;
    }

    public FlightControllable getPhysicalDrone() {
        return this.physicalDrone;
    }

    public ItemContainer getCommander() {
        return droneCommandCenter;
    }

    public void setSize(Point3D dimensions) {
        super.setSize(dimensions);
        ImageView image = (ImageView) this.getRepresentation();
        image.setFitWidth(dimensions.getX());
        image.setFitHeight(dimensions.getY());
    }


    public void setJustPosition(Point2D point) {
        this.position = point;
    }

    public void setSize(double x, double y, double z) {
        this.setSize(new Point3D(x, y, z));
    }

    //Generate farm scan animation. Drone moves to top left corner of screen,
    // moves across the width of the farm and back 8 times, and returns home
    public SequentialTransition scanFarm(Pane canvas) throws IOException {
        SequentialTransition sequence = new SequentialTransition();
        final int sections = 8;
        final double increment = (canvas.getHeight() - (this.getSize().getY() * 2)) / sections;
        final double width = canvas.getWidth() - (this.getSize().getX() * 2);

        //Move to top left corner of plot
        sequence.getChildren().add(this.goToPoint(new Point2D(50, 50)));

        //Face right
        sequence.getChildren().add(this.rotatetoDegree(0, .25));

        //Generate each movement across and 90 degree rotations
        int direction = 1;
        for (int i = 0; i < sections; i++) {
            sequence.getChildren().add(this.translate(new Point2D(width * direction, 0)));
            this.physicalDrone.flyForward((int) width);

            sequence.getChildren().add(this.rotatebyDegree(90 * direction, .25));
            this.physicalDrone.turnCCW(90 * direction);

            sequence.getChildren().add(this.translate(new Point2D(0, increment)));
            this.physicalDrone.flyForward((int) increment);

            sequence.getChildren().add(this.rotatebyDegree(90 * direction, .25));
            this.physicalDrone.turnCCW(90 * direction);

            direction = -direction;
        }

        // Final translation across farm, no rotation
        sequence.getChildren().add(this.translate(new Point2D(width, 0)));
        this.physicalDrone.flyForward((int) width);

        // Before accurate return home journey, calculate position of drone at end of farm scan
        this.setJustPosition(new Point2D(width + 25, (increment * 8) + 25));

        // Go home
        sequence.getChildren().add(this.goToHome());
        return sequence;
    }

    //Generate animation to translate center of drone on Point
    //Input: Point2D representing coordinate to move to
    public SequentialTransition goToPoint(Point2D point) throws IOException {
        SequentialTransition sequence = new SequentialTransition();

        //Calculate x and y necessary to move given point minus current center of drone
        double sx = point.getX() - this.getCenter().getX();
        double sy = point.getY() - this.getCenter().getY();
        Point2D togo = new Point2D(sx, sy);

        //Calculate angle for drone to face towards point
        double angle = (Math.atan2(sy, sx) * 180 / Math.PI);

        //Rotate drone to face point
        if (!(angle % 360 == 0)) {
            sequence.getChildren().add(this.rotatetoDegree((int) angle, .5));
            this.physicalDrone.turnCW(-(int) (angle - this.physicalDrone.getAttitudeYaw()));
        }

        //Travel to Point
        sequence.getChildren().add(this.translate(togo));
        this.physicalDrone.flyForward((int) Math.hypot(togo.getX(), togo.getY()));

        //Set current position of drone to be accurate (top left corner of drone, *not* its center)
        this.setJustPosition(new Point2D(point.getX() - this.getSize().getX() / 2,
        								 point.getY() - this.getSize().getY() / 2));

        return sequence;
    }

    //Generate animation to go to Item, rotate 360 degrees, hover in place, and return home
    //Input: Component to visit
    public SequentialTransition goToItem(Component toVisit) throws IOException {
        SequentialTransition sequence = this.goToPoint(toVisit.getCenter());
        sequence.getChildren().add(this.rotatebyDegree(360, 2));
//        sequence.getChildren().add(this.rotatebyDegree(0, 2));
        sequence.getChildren().add(this.goToHome());
    	return sequence;
    }

    //Generate animation to translate home, given Command Center
    public SequentialTransition goToHome() throws IOException {
        return this.goToPoint(this.droneCommandCenter.getCenter());
    }

    //Generate a translation animation given an amount x and y to move
    //Input: Point2D containing pixels X and Y to move
    private TranslateTransition translate(Point2D point) {
        TranslateTransition forward = new TranslateTransition();
        forward.setByX(point.getX());
        forward.setByY(point.getY());
        forward.setDuration(Duration.seconds(.5));
        forward.setNode(this.getRepresentation());
        return forward;
    }

    //Rotate clockwise by a given degree
    //Input: Degree, duration in seconds
    private RotateTransition rotatebyDegree(int degrees, double secs) throws IOException {
        RotateTransition rotation = new RotateTransition();
        rotation.setAxis(Rotate.Z_AXIS);
        rotation.setByAngle(degrees);
        rotation.setDuration(Duration.seconds(secs));
        rotation.setNode(this.getRepresentation());

        return rotation;
    }

    //Rotate to face a given degree
    //Input: Degree, duration in seconds
    private RotateTransition rotatetoDegree(int degrees, double secs) throws IOException {
        RotateTransition rotation = new RotateTransition();
        rotation.setAxis(Rotate.Z_AXIS);
        rotation.setToAngle(degrees);
        rotation.setDuration(Duration.seconds(secs));
        rotation.setNode(this.getRepresentation());

        return rotation;
    }
}
