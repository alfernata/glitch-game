import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class GamePanel extends JPanel {

    // размер игрока
    private static final int CELL_SIZE = 45;
    private static final int APPLE_SIZE = 30;

    // скорость движения в пикселях за один кадр
    private static final int SPEED = 7;

    private static final int MAX_SNAKE_SIZE = 15;

    // Snake сам делает defensive copy внутри конструктора,
    // поэтому оборачивать в new ArrayList<>(...) здесь больше не нужно.
    // "new" стоит только перед Snake — Arrays.asList(...) без new
    private Snake snake = new Snake(Arrays.asList(
            new Segment(300, 300),
            new Segment(240, 300),
            new Segment(180, 300)
    ), SPEED);

    // приватный рандомный рандом
    private Random random = new Random();

    // было: private Point apple;
    private Apple apple;

    // положение курсора игрока
    private Point cursor = new Point();

    private Timer timer;

    private boolean gameOver = false;

    // создаем объект класса Enemy, задаем в параметрах координаты x и y
    // 500      - время реакции в миллисекундах
    // 20       - вероятность ошибки (%)
    private Enemy mouse = new Enemy(700, 400, 500, 20);


    // конструктор класса GamePanel
    public GamePanel() {

        // применяется для компонентов, классы swing, например, JPanel являются компонентами
        setFocusable(true);

        // управление курсором
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                cursor.setLocation(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                cursor.setLocation(e.getPoint());
            }

        });

        // -> это лямбда-выражение, сокращенная запись метода @Override
        //    public void actionPerformed(ActionEvent e
        timer = new Timer(16, e -> {

            snake.move(cursor);
            snake.checkBorders(getWidth(), getHeight());
            checkApple();

            // раз в задержку таймера (16) передаем в метод updateTarget Point координаты змейки (v1)
            // reactionTime = 500 значит: мышь получает координаты только раз в полсекунды
            mouse.updateTarget(new Point(
                    (int) snake.getHead().x,
                    (int) snake.getHead().y
            ));

            // затем вызываем метод move
            mouse.move();

            checkEnemyCollision();

            // метод класса JPanel, JAVA понимает ЭТО так this.repaint();
            repaint();
        });

        timer.start();
    }


    public void startGame() {

        createApple();
        cursor.setLocation(
                snake.getHead().x,
                snake.getHead().y);

    }


    public void createApple() {

        int areaWidth = getWidth() / 2;
        int areaHeight = getHeight() / 2;

        int startX = getWidth() / 4;
        int startY = getHeight() / 4;

        int x = startX + random.nextInt(areaWidth);
        int y = startY + random.nextInt(areaHeight);

        // было: apple = new Point(...)
        apple = new Apple(
                (x / CELL_SIZE) * CELL_SIZE,
                (y / CELL_SIZE) * CELL_SIZE
        );

    }

    private void checkApple() {

        if (apple == null) {
            return;
        }

        // создаем область головы змейки
        Rectangle snakeArea = new Rectangle(
                (int) snake.getHead().x,
                (int) snake.getHead().y,
                CELL_SIZE,
                CELL_SIZE
        );

        // было: apple.x + 15, apple.y + 15
        Rectangle appleArea = new Rectangle(
                apple.getX() + 15,
                apple.getY() + 15,
                30,
                30
        );

        // проверяем пересечение областей
        if (snakeArea.intersects(appleArea)) {

            // создаем новый сегмент

            if (snake.size() < MAX_SNAKE_SIZE){
                snake.grow();
            }


//            if (snake.getSegments().size() < MAX_SNAKE_SIZE) {
//                snake.getSegments().add(new Segment(
//                        snake.getSegments().get(snake.getSegments().size() - 1)
//                ));
//            }
            if (snake.size() == MAX_SNAKE_SIZE) {
                System.out.println("WIN");
                System.exit(0);
            }

            // новое яблоко
            createApple();
        }
    }


    private void checkEnemyCollision() {

        Rectangle mouseArea = new Rectangle(
                mouse.getX(),
                mouse.getY(),
                mouse.getSize(),
                mouse.getSize()
        );

//        List<Segment> segments = snake.getSegments();

        // getSegments() теперь read-only — используем только для чтения
        var segments = snake.getSegments();

        for (int i = 0; i < segments.size(); i++) {

            Segment segment = segments.get(i);

            Rectangle segmentArea = new Rectangle(
                    (int) segment.x,
                    (int) segment.y,
                    CELL_SIZE,
                    CELL_SIZE
            );

            if (mouseArea.intersects(segmentArea)) {

                if (i == 0) {
                    endGame();
                    return;
                } else {
                    if (i <= 2) {
                        endGame();
                    }

//                    while (segments.size() > i) {
//                        segments.remove(i);
//                    }
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


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // фон
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // игрок
        g.setColor(Color.RED);

        // змейка
        for (Segment segment : snake.getSegments()) {
            g.fillRect(
                    (int) segment.x,
                    (int) segment.y,
                    CELL_SIZE,
                    CELL_SIZE
            );
        }

        // яблоко
        if (apple != null) {
            g.setColor(Color.GREEN);
            // было: apple.x, apple.y
            g.fillRect(
                    apple.getX(),
                    apple.getY(),
                    APPLE_SIZE,
                    APPLE_SIZE
            );
        }

        g.setColor(Color.YELLOW);
        // через return передаем данные о точках x, y и size мышки
        g.fillOval(
                mouse.getX(),
                mouse.getY(),
                mouse.getSize(),
                mouse.getSize()
        );
    }
}

