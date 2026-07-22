import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Time;
import java.util.Random;
import java.util.ArrayList;


class GameWindow{

    //объявление переменной, которая позже будет хранить ссылку на объект
    private JFrame frame;
    private GamePanel panel;

    static void main() {

        // создаем экземпляр класса
        GameWindow game = new GameWindow();

        game.createWindow();
        game.createPanel();
        game.initializeGame();

    }


    private void createWindow(){

        // создание ссылки на объект
        frame = new JFrame();

        frame.setTitle("Cheat Arcade");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void createPanel(){
        panel = new GamePanel();
        frame.add(panel);
    }

    private void initializeGame(){
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            panel.startGame();
        });
    }


}

class GamePanel extends JPanel{

    // размер игрока
    private static final int CELL_SIZE = 85;
    private static final int APPLE_SIZE = 30;

    // скорость движения в пикселях за один кадр
    private static final int SPEED = 7;

    //?
    private ArrayList<Point> snake = new ArrayList<>();

    // приватный рандомный рандом
    private Random random = new Random();

    // координаты яблока
    private Point apple;

    private String direction = "RIGHT";

    // создаем объект класса Enemy, задаем в параметрах координаты x и y
    // 500      - время реакции в миллисекундах
    // 80       - точность движения (%)
    private Enemy mouse = new Enemy(700, 400, 500, 95);

//    private static final int SEGMENT_DISTANCE = 70;




    // конструктор класса GamePanel
    public GamePanel(){

        // применяется для компонентов, классы swing, например, JPanel являются компонентами
        setFocusable(true);

        // метод, в котором мы создаем объект KeyAdapter и сразу же говорим, что хотим изменит его методы
        addKeyListener(new KeyAdapter(){

            // переопредялем методы анонимного класса KeyAdapter
            @Override
            public void keyPressed(KeyEvent e){

                // У объекта KeyEvent спросить код клавиши
                switch (e.getKeyCode()){

                    case KeyEvent.VK_UP:
                        direction = "UP";
                        break;

                    case KeyEvent.VK_DOWN:
                        direction = "DOWN";
                        break;

                    case KeyEvent.VK_LEFT:
                        direction = "LEFT";
                        break;

                    case KeyEvent.VK_RIGHT:
                        direction = "RIGHT";
                        break;

                }
            }
        });


        snake.add(new Point(300, 300));
        snake.add(new Point(240, 300));
        snake.add(new Point(180, 300));


        // -> это лямбда-выражение, сокращенная запись метода  @Override
        //    public void actionPerformed(ActionEvent e

        Timer timer = new Timer(16, e -> {
            moveSnake();
            checkBorders();
            checkApple();

            // раз в задержку таймера (16) передаем в метод updateTarget Point координаты змейки (v1)
            // Теперь  метод updateTarget сам решает, когда обновлять информацию.
            // Например: reactionTime = 500 значит:  мышь получает координаты только раз в полсекунды.
            mouse.updateTarget(snake.get(0));
            // затем вызываем метод move
            mouse.move();


            // метод класса JPanel, JAVA понимает ЭТО так this.repaint();
            repaint();
        });

        timer.start();
    }


    public void startGame(){
        createApple();
    }


    private void moveSnake(){

        for (int i = snake.size() - 1; i > 0; i--){

            Point previous = snake.get(i - 1);

            snake.set(i, new Point(
                    previous.x,
                    previous.y
            ));

        }


        //?
        Point head = snake.get(0);


        switch (direction){

            //cell size?
            case "UP":
                head.y -= SPEED;
                break;

            case "DOWN":
                head.y += SPEED;
                break;

            case "LEFT":
                head.x -= SPEED;
                break;

            case "RIGHT":
                head.x += SPEED;
                break;
        }

    }

    private void checkBorders(){

        Point head = snake.get(0);

        if (head.x < 0){
            head.x = getWidth() - CELL_SIZE;
        }

        if (head.x > getWidth() - CELL_SIZE){
            head.x = 0;
        }

        if (head.y < 0){
            head.y = getHeight() - CELL_SIZE;
        }

        if (head.y > getHeight() - CELL_SIZE){
            head.y = 0;
        }


    }


    public void createApple(){


        int areaWidth = getWidth() / 2;
        int areaHeight = getHeight() / 2;


        int startX = getWidth() / 4;
        int startY = getHeight() / 4;


        int x = startX + random.nextInt(areaWidth);
        int y = startY + random.nextInt(areaHeight);



        apple = new Point(
                (x / CELL_SIZE) * CELL_SIZE,
                (y / CELL_SIZE) * CELL_SIZE
        );


    }

    private void checkApple(){


        if(apple == null){
            return;
        }

        // создаем область головы змейки
        Rectangle snakeArea = new Rectangle(
                snake.get(0).x,
                snake.get(0).y,
                CELL_SIZE,
                CELL_SIZE
        );



        // создаем область яблока
        Rectangle appleArea = new Rectangle(
                apple.x + 15,
                apple.y + 15,
                30,
                30
        );



        // проверяем пересечение областей

        if(snakeArea.intersects(appleArea)){


            // создаем новый сегмент
            snake.add(new Point(
                    snake.get(snake.size() - 1)
            ));




            // новое яблоко
            createApple();

        }

    }



    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        //фон
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // игрок
        g.setColor(Color.RED);

        // змейка

        for (Point segment : snake){
            g.fillRect(
                    segment.x,
                    segment.y,
                    CELL_SIZE,
                    CELL_SIZE
            );
        }

//        for (int i = 0; i < snake.size(); i++){
//            g.fillRect(
//                    snake.get(i).x,
//                    snake.get(i).y,
//                    CELL_SIZE,
//                    CELL_SIZE
//            );
//        }

        // яблоко

        if (apple != null){
            g.setColor(Color.GREEN);
            g.fillRect(
                    apple.x,
                    apple.y,
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


class Enemy{

    // Текущая позиция мыши
    private Point position;

    // Это НЕ настоящая позиция змейки.
    // Это координата, которую мышь запомнила в последний момент обновления информации.
    private Point target;

    // скорость мыши
    private int speed = 3;

    // размер круга мыши
    private static final int SIZE = 50;

    // Через сколько миллисекунд мышь получает новую информацию
    private int reactionTime;

    // Насколько хорошо мышь принимает решения.
    private int accuracy;

    // Храним момент последнего получения информации
    private long lastUpdateTime;

    private Random random = new Random();

    private Point mistakeDirection;

    private long mistakeEndTime;

    private int mistakeDuration = 250;



    public Enemy(int x, int y, int reactionTime, int accuracy){

        position = new Point(x, y);
        target = new Point(x, y);
        this.reactionTime = reactionTime;
        this.accuracy = accuracy;

        lastUpdateTime = 0;

        mistakeDirection = new Point(0,0);
    }

    public void updateTarget(Point snakeHead){
//        target = new Point(snakeHead);

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateTime >= reactionTime){
            target = new Point(snakeHead);

            lastUpdateTime = currentTime;
        }

    }

    public void move(){

        long currentTime = System.currentTimeMillis();

        if (currentTime < mistakeEndTime){

            position.x += mistakeDirection.x * speed;
            position.y += mistakeDirection.y * speed;

            return;
        }


        boolean mistake = random.nextInt(100) >= accuracy;

        if (mistake){
            createMistake();
            return;
        }

        moveToTarget();

//        if (position.x < target.x){
//            position.x += speed;
//        }
//
//        if (position.x > target.x){
//            position.x -= speed;
//        }
//
//        if (position.y < target.y){
//            position.y += speed;
//        }
//
//        if (position.y > target.y){
//            position.y -= speed;
//        }

    }


    private void createMistake(){

        int direction = random.nextInt(4);

        switch (direction){

            case 0:

                mistakeDirection = new Point(1,0);
                break;

            case 1:

                mistakeDirection = new Point(-1,0);
                break;

            case 2:

                mistakeDirection = new Point(0,1);
                break;

            case 3:

                mistakeDirection = new Point(0,-1);
                break;

        }

        mistakeEndTime = System.currentTimeMillis() + mistakeDuration;

    }

    private void moveToTarget(){

        if (position.x < target.x){
            position.x += speed;
        }

        if (position.x > target.x){
            position.x -= speed;
        }

        if (position.y < target.y){
            position.y += speed;
        }

        if (position.y > target.y){
            position.y -= speed;
        }

    }


    private void randomMove(){

        int direction = random.nextInt(4);

        switch (direction){

            case 0:
                position.x += speed;
                break;

            case 1:
                position.x -= speed;


            case 2:
                position.y += speed;
                break;

            case 3:
                position.y -= speed;
        }


    }

    public int getX(){
        return position.x;
    }

    public int getY(){
        return position.y;
    }

    public int getSize(){
        return SIZE;
    }


}




// в конструктор Enemy передаются значения присваиваемые при создании объекта класс
// без конструктора пришлось бы делать так:

//Enemy mouse = new Enemy();
//mouse.position = new Point(700,400);

// <Point> - обобщение (generics)
// for each - если нужен каждый объект: все враги, все сегменты змейки и т.д.
//this — ссылка на текущий объект. Используется для обращения к его полям и методам,
// а также для устранения неоднозначности между полями объекта и параметрами методов.
// к теме об обращении к статическим и нестатическим методам


//Статические (static) методы принадлежат классу, а не объекту. Их можно вызвать без создания объекта.
// Например: static int sum(int a, int b) {
//    return a + b;
//}


//Нестатические методы принадлежат объекту. Для их вызова нужно сначала создать экземпляр класса.
// пример:
//Player player = new Player();
//player.move();
//class Player {
//
//    void move() {
//        System.out.println("Игрок движется");
//    }
//}


