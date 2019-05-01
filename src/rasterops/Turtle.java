package rasterops;


import imagedata.Image;
import io.vavr.API;
import io.vavr.collection.List;
import org.jetbrains.annotations.NotNull;
import transforms.Point2D;

import static io.vavr.API.$;
import static io.vavr.API.Case;

/**
 * Class representing turtle graphics
 * @param <T>
 */

public class Turtle<T> {
    private static final int VERTICAL_ANGLE = 180;
    private static final int HORIZONTAL_ANGLE = 90;
    private final @NotNull LineRasterizer<T> lineRasterizer;
    private TurtleState currentState;
    private String toDraw;
    private int angle;
    private int lineLength;
    private List<TurtleState> stack;
    private Image<T> image;
    private T value;

    public Turtle(@NotNull LineRasterizer<T> lineRasterizer, @NotNull Image image) {
        this.lineRasterizer = lineRasterizer;
        this.image = image;
    }

    /** Prepares turtle initial state
     * @param startPosition Starting position of turtle
     * @param toDraw        String representation of turtle graphics
     * @param lineLength    length of rendered lines
     * @param angle         turning angle of turtle in degrees
     * @param vertical      if starting angle is vertical then true, else false
     */

    public final void prepare(final int startPosition, final String toDraw, final int lineLength,
                              final int angle, final boolean vertical, final T value) {
        int startAngle = vertical ? VERTICAL_ANGLE : HORIZONTAL_ANGLE;
        currentState = API.Match(startPosition)
                          .of(
                                  //Center
                                  Case($(1), new TurtleState(new Point2D(0, 0), startAngle)),
                                  //Corner
                                  Case($(2), new TurtleState(new Point2D(0.5, 0.5), startAngle)),
                                  //Bottom
                                  Case($(), new TurtleState(new Point2D(-0.2, -0.8), startAngle))
                          );
        this.stack = List.empty();
        this.toDraw = toDraw;
        this.angle = angle;
        this.lineLength = lineLength;
        this.value = value;
    }

    /**
     * Draws L system based on the given string
     * @return image with rendered L-system
     */
    @NotNull
    public final Image startTurtle() {
        for (char symbol : toDraw.toCharArray()) {
            if (symbol == 'F' || symbol == 'G') {
                double nextX = currentState.position.getX() + (lineLength / 100.0 * (Math.cos(Math.toRadians(currentState.angle))));
                double nextY = currentState.position.getY() + (lineLength / 100.0 * (Math.sin(Math.toRadians(currentState.angle))));
                image = lineRasterizer.rasterize(image, currentState.position.getX(), currentState.position.getY(), nextX, nextY, value);
                this.currentState = new TurtleState(new Point2D(nextX, nextY), currentState.angle);
            } else if (symbol == '+') {
                int nextAngle = currentState.angle + angle;
                currentState = new TurtleState(new Point2D(currentState.position.getX(), currentState.position.getY()), nextAngle);
            } else if (symbol == '-') {
                int nextAngle = currentState.angle - angle;
                currentState = new TurtleState(new Point2D(currentState.position.getX(), currentState.position.getY()), nextAngle);
            } else if (symbol == '[') {
                stack = stack.prepend(currentState);
            } else if (symbol == ']') {
                if (!stack.isEmpty()) {
                    currentState = stack.peek();
                    stack = stack.tail();
                }
            }
        }
        return image;
    }

    /**
     * Inner class representing current position and angle of the turtle
     */

    private final class TurtleState {
        public int angle;
        Point2D position;


        private TurtleState(@NotNull final Point2D position, final int angle) {
            this.position = position;
            this.angle = angle;
        }
    }
}
