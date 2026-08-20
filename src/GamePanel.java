import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

class GamePanel extends JPanel {

    // GamePanel больше не хранит ни Snake, ни Apple, ни Enemy —
    // вся игровая модель теперь внутри одного объекта Game
    private Game game = new Game();

    private Point cursor = new Point();

    private Timer timer;

    public GamePanel() {

        setFocusable(true);

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

        // было: moveSnake(), checkBorders(), checkApple(), mouse.updateTarget()/move(),
        //       checkEnemyCollision() — всё по отдельности прямо тут
        // стало: одна делегирующая команда игровой модели
        timer = new Timer(16, e -> {
            game.update(cursor, getWidth(), getHeight());
            repaint();
        });

        timer.start();
    }

    public void startGame() {
        game.createApple(getWidth(), getHeight());
        cursor.setLocation(getWidth() / 2, getHeight() / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // было: весь код рисования фона/змейки/яблока/врага прямо тут
        // стало: делегируем в Game
        game.render(g, getWidth(), getHeight());
    }
}