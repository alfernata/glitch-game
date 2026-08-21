import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Game {

//    // размер игрока
//    private static final int CELL_SIZE = 45;
//    private static final int APPLE_SIZE = 30;

    // скорость движения в пикселях за один кадр
//    private static final int SPEED = 7;
//
//    private static final int MAX_SNAKE_SIZE = 15;

    // Snake сам делает defensive copy внутри конструктора,
    // поэтому оборачивать в new ArrayList<>(...) здесь больше не нужно.
    // "new" стоит только перед Snake — Arrays.asList(...) без new
    private Snake snake = new Snake(Arrays.asList(
            new Segment(300, 300),
            new Segment(240, 300),
            new Segment(180, 300)
    ), GameConfig.SPEED);

    private Apple apple;

    // создаем объект класса Enemy, задаем в параметрах координаты x и y
    // 500      - время реакции в миллисекундах
    // 20       - вероятность ошибки (%)
    private Enemy enemy = new Enemy(700, 400, 500, 20);

    private java.util.Random random = new java.util.Random();

    private boolean gameOver = false;


    public void createApple(int panelWidth, int panelHeight) {

        int areaWidth = panelWidth / 2;
        int areaHeight = panelHeight / 2;

        int startX = panelWidth / 4;
        int startY = panelHeight / 4;

        int x = startX + random.nextInt(areaWidth);
        int y = startY + random.nextInt(areaHeight);

        apple = new Apple(
                (x / GameConfig.CELL_SIZE) * GameConfig.CELL_SIZE,
                (y / GameConfig.CELL_SIZE) * GameConfig.CELL_SIZE
        );
    }


    private void checkApple(){

        if (apple == null){
            return;
        }

        // создаем область головы змейки
        Rectangle snakeArea = new Rectangle(
                (int) snake.getHead().x,
                (int) snake.getHead().y,
                GameConfig.CELL_SIZE,
                GameConfig.CELL_SIZE
        );

        // было: apple.x + 15, apple.y + 15
        Rectangle appleArea = new Rectangle(
                apple.getX() + 15,
                apple.getY() + 15,
                30,
                30
        );

        if (snakeArea.intersects(appleArea)) {

            // создаем новый сегмент

            if (snake.size() < GameConfig.MAX_SNAKE_SIZE) {
                snake.grow();
            }

            if (snake.size() == GameConfig.MAX_SNAKE_SIZE) {
                System.out.println("WIN");
                System.exit(0);
            }

            createApple(lastPanelWidth, lastPanelHeight);

        }


    }

    private void checkEnemyCollision(){

        Rectangle enemyArea = new Rectangle(
                enemy.getX(),
                enemy.getY(),
                enemy.getSize(),
                enemy.getSize()
        );

        var segments = snake.getSegments();

        for (int i = 0; i < segments.size(); i++) {

            Segment segment = segments.get(i);

            Rectangle segmentArea = new Rectangle(
                    (int) segment.x,
                    (int) segment.y,
                    GameConfig.CELL_SIZE,
                    GameConfig.CELL_SIZE
            );

            if (enemyArea.intersects(segmentArea)) {

                if (i == 0) {
                    endGame();
                    return;
                } else {
                    if (i <= 2) {
                        endGame();
                    }
                    snake.cutTailFrom(i);
                    return;
                }
            }
        }
    }

    private void endGame() {

        if (gameOver) {
            return;
        }

        gameOver = true;

        System.out.println("GAME OVER");

        System.exit(0);
    }

    public void render(Graphics g, int panelWidth, int panelHeight){

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, panelWidth, panelHeight);

        g.setColor(Color.RED);
        for (Segment segment : snake.getSegments()) {
            g.fillRect(
                    (int) segment.x,
                    (int) segment.y,
                    GameConfig.CELL_SIZE,
                    GameConfig.CELL_SIZE
            );
        }

        if (apple != null) {
            g.setColor(Color.GREEN);
            g.fillRect(
                    apple.getX(),
                    apple.getY(),
                    GameConfig.APPLE_SIZE,
                    GameConfig.APPLE_SIZE
            );
        }

        g.setColor(Color.YELLOW);
        g.fillOval(
                enemy.getX(),
                enemy.getY(),
                enemy.getSize(),
                enemy.getSize()
        );
    }

    private int lastPanelWidth;
    private int lastPanelHeight;


    public void update(Point cursor, int panelWidth, int panelHeight){

        if (gameOver) {
            return;
        }

        this.lastPanelWidth = panelWidth;
        this.lastPanelHeight = panelHeight;

        snake.move(cursor);
        snake.checkBorders(panelWidth, panelHeight);
        checkApple();

        enemy.updateTarget(new Point(
                (int) snake.getHead().x,
                (int) snake.getHead().y
        ));

        enemy.move();

        checkEnemyCollision();


    }

    }



