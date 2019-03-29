package rasterops;


import imagedata.Image;
import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import transforms.Point2D;

import java.util.concurrent.TimeUnit;

import static io.vavr.API.$;
import static io.vavr.API.Case;

public class Turtle<T> {

    // Vertical starting angle in degrees
    private static final int VERTICAL_ANGLE = 180;
    // Horizontal starting angle in degrees
    private static final int HORIZONTAL_ANGLE = 90;
    // Distance of initial turtle point from border
    private static final int DISTANCE = 15;
    final @NotNull LineRasterizer<T> lineRasterizer;
    private TurtleState currentState;
    private String toDraw;
    private int angle;
    private int lineLength;
    private Thread drawingThread;
    private List<TurtleState> stack;
    private Image<T> image;
    private T value;

    public Turtle(@NotNull LineRasterizer<T> lineRasterizer, @NotNull Image image) {
        this.lineRasterizer = lineRasterizer;
        this.image = image;
    }

    /**
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
                                  Case($(1), new TurtleState(new Point2D(image.getWidth() / 2, image.getHeight() / 2), startAngle)),
                                  //Corner
                                  Case($(2), new TurtleState(new Point2D(DISTANCE, image.getHeight()), startAngle)),
                                  //Bottom
                                  Case($(), new TurtleState(new Point2D(image.getWidth() / 2, image.getHeight()), startAngle))
                          );
        this.stack = List.empty();
        this.toDraw = toDraw;
        this.angle = angle;
        this.lineLength = lineLength;
        this.value = value;
    }

    @NotNull
    public final Image startTurtle(final int delay) {
        if (drawingThread != null) {
            try {
                drawingThread.interrupt();
                drawingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drawingThread = new Thread(() -> {
            try {
                for (char symbol : toDraw.toCharArray()) {
                    if (symbol == 'F' || symbol == 'G') {
                        double nextX = currentState.position.getX() + (lineLength * Math.sin(Math.toRadians(currentState.angle)));
                        double nextY = currentState.position.getY() + (lineLength * Math.cos(Math.toRadians(currentState.angle)));
                        image = lineRasterizer.rasterize(image, currentState.position.getX(), currentState.position.getY(), nextX, nextY, value);
                        currentState = new TurtleState(new Point2D(nextX, nextY), currentState.angle);
                        TimeUnit.MILLISECONDS.sleep(delay);
                    } else if (symbol == '+') {
                        int nextAngle = currentState.angle + angle;
                        currentState = new TurtleState(new Point2D(currentState.position.getX(), currentState.position.getY()), nextAngle);
                    } else if (symbol == '-') {
                        int nextAngle = currentState.angle - angle;
                        currentState = new TurtleState(new Point2D(currentState.position.getX(), currentState.position.getY()), nextAngle);
                    } else if (symbol == '[') {
                        stack.push(currentState);
                    } else if (symbol == ']') {
                        if (!stack.isEmpty()) {
                            currentState = stack.peek();
                            stack.pop();
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        drawingThread.start();
        return image;
    }

    private final class TurtleState {
        Point2D position;
        public int angle;


        private TurtleState(@NotNull final Point2D position, final int angle) {
            this.position = position;
            this.angle = angle;
        }
    }
}
