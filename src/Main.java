import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.util.Random;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;




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
    private static final int CELL_SIZE = 45;
    private static final int APPLE_SIZE = 30;

    // скорость движения в пикселях за один кадр
    private static final int SPEED = 7;

    //?
    private ArrayList<Point> snake = new ArrayList<>();

    // приватный рандомный рандом
    private Random random = new Random();

    // координаты яблока
    private Point apple;

    // положение курсора игрока
    private Point cursor = new Point();

    private static final int SEGMENT_DISTANCE = 55;

//    private String direction = "RIGHT";

    // создаем объект класса Enemy, задаем в параметрах координаты x и y
    // 500      - время реакции в миллисекундах
    // 80       - точность движения (%)
    private Enemy mouse = new Enemy(700, 400, 500, 20);

//    private static final int SEGMENT_DISTANCE = 70;




    // конструктор класса GamePanel
    public GamePanel(){

        // применяется для компонентов, классы swing, например, JPanel являются компонентами
        setFocusable(true);

        // метод, в котором мы создаем объект KeyAdapter и сразу же говорим, что хотим изменит его методы
//        addKeyListener(new KeyAdapter(){
//
//            // переопредялем методы анонимного класса KeyAdapter
//            @Override
//            public void keyPressed(KeyEvent e){
//
//                // У объекта KeyEvent спросить код клавиши
//                switch (e.getKeyCode()){
//
//                    case KeyEvent.VK_UP:
//                        direction = "UP";
//                        break;
//
//                    case KeyEvent.VK_DOWN:
//                        direction = "DOWN";
//                        break;
//
//                    case KeyEvent.VK_LEFT:
//                        direction = "LEFT";
//                        break;
//
//                    case KeyEvent.VK_RIGHT:
//                        direction = "RIGHT";
//                        break;
//
//                }
//            }
//        });



        //управление курсором

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e){
                cursor.setLocation(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e){
                cursor.setLocation(e.getPoint());
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
        cursor.setLocation(snake.get(0));

    }


    private void moveSnake(){


        Point head = snake.get(0);

        double dx = cursor.x - head.x;
        double dy = cursor.y - head.y;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > SPEED){

            head.x += (int)(dx / distance * SPEED);
            head.y += (int)(dy / distance * SPEED);

        }

        for (int i = 1; i < snake.size(); i++){


            Point previous = snake.get(i - 1);
            Point current = snake.get(i);

            dx = previous.x - current.x;
            dy = previous.y - current.y;

            distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > SEGMENT_DISTANCE){

                current.x += (int) (dx / distance * (distance - SEGMENT_DISTANCE));
                current.y += (int) (dy / distance * (distance - SEGMENT_DISTANCE));

            }

        }



    /**    for (int i = snake.size() - 1; i > 0; i--){

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
        } **/

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



// мышь получает информацию не постоянно - reactionTime
// запоминание последнего местонахождения змейки
// Разделение кадров и принятия решений - Game loop 60 FPS и AI thinking через makeDecision();
// ошибка это состояние, которое длится определенное время
// Вероятность ошибки - mistakeChance
// Два режима поведения мыши - NORMAL (moveToTarget()) и MISTAKE (mistakeDirection)


// position - где мышь сейчас
// target - последняя известная позиция змейки
// reactionTime - как часто получает информацию
// mistakeChance - вероятность ошибиться
// nextDecisionTime - когда можно снова подумать
// mistakeDirection - куда ошибочно идёт
// mistakeEndTime - когда закончится ошибка



// Лёгкая мышь
// reactionTime = 1200;
// mistakeChance = 40;
// mistakeDuration = 1200;
// speed = 2;


// Средняя мышь
//reactionTime = 700;
//mistakeChance = 20;
//mistakeDuration = 700;
//speed = 3;


// Сложная мышь
//reactionTime = 200;
//mistakeChance = 5;
//mistakeDuration = 300;
//speed = 5;




class Enemy {


    // Позиция
    private Point position;


    // Последняя известная позиция змейки
    private Point target;

    // Движение
    private int speed = 3;
    private static final int SIZE = 50;

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
    private int mistakeDuration = 800;

    private Random random = new Random();


    // Конструктор
    public Enemy(
            int x,
            int y,
            int reactionTime,
            int mistakeChance
    ){

        position = new Point(x,y);
        target = new Point(x,y);

        this.reactionTime = reactionTime;
        this.mistakeChance = mistakeChance;

        mistakeDirection = new Point(0,0);
        lastUpdateTime = 0;
        nextDecisionTime = 0;
        mistakeEndTime = 0;



    }



    // Получение информации о змейке
    public void updateTarget(Point snakeHead){

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateTime >= reactionTime){

            target = new Point(
                    snakeHead
            );

            lastUpdateTime = currentTime;

        }

    }


    // Основное движение
    public void move(){

        long currentTime = System.currentTimeMillis();


        /*
        Состояние ошибки.
        Если мышь уже ошиблась,
        она НЕ принимает новые решения.
        Она просто выполняет старое решение.
        */


        if(currentTime < mistakeEndTime){

            position.x += mistakeDirection.x * speed;
            position.y += mistakeDirection.y * speed;

            return;

        }


        /*
        Ошибка закончилась.
        Проверяем:
        может ли мышь принять новое решение?
        */


        if (currentTime >= nextDecisionTime){
            makeDecision();
        }


        // Если решения нет -
        // продолжаем нормальное движение
        moveToTarget();

    }


    // Принятие решения
    private void makeDecision(){

        long currentTime = System.currentTimeMillis();


        // запрещаем принимать решение
        // слишком часто

        nextDecisionTime = currentTime + 500;

        int chance = random.nextInt(100);

        if (chance < mistakeChance){
            createMistake();

        }
    }


    // Создание ошибки
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

    // Нормальное движение к цели
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


    // Для рисования
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


