public class GameConfig {

    // размеры
    static final int CELL_SIZE = 45;
    static final int APPLE_SIZE = 30;

    // движение змейки
    // скорость движения в пикселях за один кадр
    static final int SPEED = 7;
    static final double MAX_TURN_SPEED = 0.08;
    static final int SEGMENT_DISTANCE = 55;
    static final int MAX_SNAKE_SIZE = 15;

    // поведение врага (Enemy)
    static final int ENEMY_SIZE = 50;
    static final int ENEMY_DEFAULT_SPEED = 2;
    static final long ENEMY_DECISION_INTERVAL = 500;   // makeDecision(): "раз в 500мс можно принять решение"
    static final int ENEMY_MISTAKE_DURATION = 800;      // было: private int mistakeDuration = 800;


    private GameConfig() {
    }
}
