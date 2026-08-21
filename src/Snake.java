import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Snake {

    // поля — то, что раньше было в GamePanel
    private ArrayList<Segment> segments;

    private double speed;
    private double angle = 0;

//    private static final int CELL_SIZE = 45;
//    private static final double MAX_TURN_SPEED = 0.08;
//    private static final int SEGMENT_DISTANCE = 55;

    // временный конструктор — полноценно займёмся конструкторами на Дне 2
    public Snake(List<Segment> initialSegments, double speed) {
        this.segments = new ArrayList<>(initialSegments);
        this.speed = speed;
    }

    // геттер — временно даёт GamePanel доступ к списку сегментов
    // (полную инкапсуляцию сделаем на Дне 3)
    public List<Segment> getSegments() {

        // обёртка "только для чтения"
        return Collections.unmodifiableList(segments);

    }

    // овый метод — замена прямого snake.getSegments().get(0) по всему GamePanel
    public Segment getHead(){
        return segments.get(0);
    }

    // тело логики, которая раньше была прямо в GamePanel.checkApple()
    public void grow(){
        Segment last = segments.get(segments.size() - 1);
        segments.add(new Segment(last));
    }

    public void cutTailFrom (int index){


        // защита инварианта: нельзя обрезать голову (index 0) этим методом —
        // за смерть змейки целиком отвечает другая логика (endGame в GamePanel),
        // а не "обрезание хвоста"

        if (index <= 0){
            return;
        }

        while (segments.size() > index){
            segments.remove(segments.size() - 1);
        }

    }

    public int size(){
        return segments.size();
    }



    // тело метода — 1 в 1 из GamePanel.moveSnake(),
    // "cursor" стал параметром, потому что курсор — это данные UI, а не змейки
    public void move(Point cursorTarget) {

        Segment head = segments.get(0);

        double centerX = head.x + GameConfig.CELL_SIZE / 2.0;
        double centerY = head.y + GameConfig.CELL_SIZE / 2.0;

        double targetAngle = Math.atan2(
                cursorTarget.y - centerY,
                cursorTarget.x - centerX
        );

        double difference = targetAngle - angle;

        while (difference > Math.PI) {
            difference -= Math.PI * 2;
        }
        while (difference < -Math.PI) {
            difference += Math.PI * 2;
        }

        double distanceToCursor = Math.sqrt(
                Math.pow(cursorTarget.x - centerX, 2) +
                        Math.pow(cursorTarget.y - centerY, 2)
        );

        if (distanceToCursor > GameConfig.CELL_SIZE) {
            if (Math.abs(difference) < GameConfig.MAX_TURN_SPEED) {
                angle = targetAngle;
            } else {
                angle += Math.signum(difference) * GameConfig.MAX_TURN_SPEED;
            }
        }

        double directionX = Math.cos(angle);
        double directionY = Math.sin(angle);

        head.x += directionX * speed;
        head.y += directionY * speed;

        for (int i = 1; i < segments.size(); i++) {
            Segment previous = segments.get(i - 1);
            Segment current = segments.get(i);

            double dx = previous.x - current.x;
            double dy = previous.y - current.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > GameConfig.SEGMENT_DISTANCE) {
                current.x += dx / distance * (distance - GameConfig.SEGMENT_DISTANCE);
                current.y += dy / distance * (distance - GameConfig.SEGMENT_DISTANCE);
            }
        }
    }

    // тело метода — 1 в 1 из GamePanel.checkBorders(),
    // width/height теперь параметры, а не getWidth()/getHeight() из JPanel
    public void checkBorders(int width, int height) {

        Segment head = segments.get(0);

        if (head.x < 0) {
            head.x = width - GameConfig.CELL_SIZE;
        }
        if (head.x > width - GameConfig.CELL_SIZE) {
            head.x = 0;
        }
        if (head.y < 0) {
            head.y = height - GameConfig.CELL_SIZE;
        }
        if (head.y > height - GameConfig.CELL_SIZE) {
            head.y = 0;
        }
    }
}