import java.awt.*;
import java.util.Random;

class Enemy {


    // Позиция
    private Point position;


    // Последняя известная позиция змейки
    private Point target;

    // Движение
    private int speed = GameConfig.ENEMY_DEFAULT_SPEED;
    private static final int SIZE = GameConfig.ENEMY_SIZE;

    // Как часто мышь получает новую информацию
    private int reactionTime;

    // Когда последний раз мышь получила координаты
    private long lastUpdateTime;


    // AI мыши


    // Шанс ошибки при принятии решения
    private int mistakeChance;


    // Когда мышь снова будет принимать решение
    private long nextDecisionTime;


    // Состояние ошибки
    // Направление ошибочного движения
    private Point mistakeDirection;

    // Когда закончится ошибка
    private long mistakeEndTime;

    // Сколько длится ошибка
    private int mistakeDuration = GameConfig.ENEMY_MISTAKE_DURATION;

    private Random random = new Random();


    // Конструктор
    public Enemy(
            int x,
            int y,
            int reactionTime,
            int mistakeChance
    ) {

        position = new Point(x, y);
        target = new Point(x, y);

        this.reactionTime = reactionTime;
        this.mistakeChance = mistakeChance;

        mistakeDirection = new Point(0, 0);
        lastUpdateTime = 0;
        nextDecisionTime = 0;
        mistakeEndTime = 0;


    }


    // Получение информации о змейке
    public void updateTarget(Point snakeHead) {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateTime >= reactionTime) {

            target = new Point(
                    snakeHead
            );

            lastUpdateTime = currentTime;

        }

    }


    // Основное движение
    public void move() {

        long currentTime = System.currentTimeMillis();


        /*
        Состояние ошибки.
        Если мышь уже ошиблась,
        она НЕ принимает новые решения.
        Она просто выполняет старое решение.
        */


        if (currentTime < mistakeEndTime) {

            position.x += mistakeDirection.x * speed;
            position.y += mistakeDirection.y * speed;

            return;

        }


        /*
        Ошибка закончилась.
        Проверяем:
        может ли мышь принять новое решение?
        */


        if (currentTime >= nextDecisionTime) {
            makeDecision();
        }


        // Если решения нет -
        // продолжаем нормальное движение
        moveToTarget();

    }


    // Принятие решения
    private void makeDecision() {

        long currentTime = System.currentTimeMillis();


        // запрещаем принимать решение
        // слишком часто

        nextDecisionTime = currentTime + GameConfig.ENEMY_DECISION_INTERVAL;

        int chance = random.nextInt(100);

        if (chance < mistakeChance) {
            createMistake();

        }
    }


    // Создание ошибки
    private void createMistake() {

        int direction = random.nextInt(4);


        switch (direction) {

            case 0:
                mistakeDirection = new Point(1, 0);
                break;

            case 1:
                mistakeDirection = new Point(-1, 0);
                break;

            case 2:
                mistakeDirection = new Point(0, 1);
                break;

            case 3:
                mistakeDirection = new Point(0, -1);
                break;
        }

        mistakeEndTime = System.currentTimeMillis() + mistakeDuration;

    }

    // Нормальное движение к цели
    private void moveToTarget() {


        if (position.x < target.x) {
            position.x += speed;
        }

        if (position.x > target.x) {
            position.x -= speed;
        }


        if (position.y < target.y) {
            position.y += speed;
        }

        if (position.y > target.y) {
            position.y -= speed;
        }

    }


    // Для рисования
    public int getX() {
        return position.x;
    }


    public int getY() {
        return position.y;
    }


    public int getSize() {
        return SIZE;
    }

}
