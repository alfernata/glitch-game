import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


class GameWindow{

    //объявление переменной, которая позже будет хранить ссылку на объект
    private JFrame frame;
    private GamePanel panel;

    static void main() {

        // создаем экземпляр класса
        GameWindow game = new GameWindow();

        game.createWindow();
//        game.createPanel();
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

        CharacterSelectPanel selectPanel = new CharacterSelectPanel(frame);
        frame.add(selectPanel);

//        SwingUtilities.invokeLater(() -> {
//            panel.startGame();
//        });
    }


class CharacterSelectPanel extends JPanel{

        private JFrame frame;

        private CatInfo selectedCat;


        public CharacterSelectPanel (JFrame frame){

            this.frame = frame;

            setBackground(Color.BLACK);

            setLayout(new GridLayout(1, 4, 20, 20));

            createCards();
        }

        private void createCards(){

            for (String name : CatDatabase.cats.keySet()){

                CatInfo cat = CatDatabase.cats.get(name);
                JPanel card = createCard(cat);

                add(card);

            }

        }

        private JPanel createCard(CatInfo cat){

            JPanel card = new JPanel(){

                @Override
                protected void paintComponent(Graphics g){

                    super.paintComponent(g);


                    Graphics2D g2 =
                            (Graphics2D)g;


                    g2.setColor(
                            new Color(45,45,45)
                    );


                    g2.fillRoundRect(
                            10,
                            50,
                            getWidth() - 40,
                            getHeight() - 200,
                            30,
                            30
                    );


                }

            };


            card.setLayout(null);


            card.setOpaque(false);

            card.setPreferredSize(
                    new Dimension(
                            240,
                            650
                    )
            );



            // белый прямоугольник

            JPanel image =
                    new JPanel();


            image.setBackground(
                    Color.WHITE
            );


            image.setBounds(
                    50,
                    100,
                    500,
                    700
            );


            card.add(image);



            // текст

            JLabel info =
                    new JLabel(
                            "<html>" +

                                    "NAME:<br>" +
                                    cat.name +
                                    "<br><br>" +

                                    "TYPE:<br>" +
                                    cat.type +
                                    "<br><br>" +

                                    "THREAT:<br>" +
                                    cat.threatLevel +
                                    "<br><br>" +

                                    "FUNCTION:<br>" +
                                    cat.primaryFunction +
                                    "<br><br>" +

                                    "STATUS:<br>" +
                                    cat.status +

                                    "</html>"
                    );



            info.setForeground(
                    Color.WHITE
            );


            info.setFont(
                    new Font(
                            "Monospaced",
                            Font.PLAIN,
                            18
                    )
            );


            info.setBounds(
                    100,
                    800,
                    200,
                    400
            );


            card.add(info);



            card.addMouseListener(
                    new MouseAdapter(){


                        @Override
                        public void mouseClicked(MouseEvent e){


                            selectedCat = cat;


                            System.out.println(
                                    "SELECTED ENTITY:"
                            );


                            System.out.println(
                                    cat.getDescription()
                            );


                            startGame();


                        }

                    }
            );



            return card;

        }


        private void startGame(){

            frame.getContentPane().removeAll();

            GamePanel panel = new GamePanel();

            frame.add(panel);
            frame.revalidate();
            frame.repaint();

            panel.startGame();

        }


}


}

class CatInfo{

    String name;
    String type;
    String threatLevel;
    String primaryFunction;
    String status;

    public CatInfo(
            String name,
            String type,
            String threatLevel,
            String primaryFunction,
            String status
    ){

        this.name = name;
        this.type = type;
        this.threatLevel = threatLevel;
        this.primaryFunction = primaryFunction;
        this.status = status;

    }

    public String getDescription(){
        return
                "TYPE: " + type + "\n" +
                "THREAT LEVEL: " + threatLevel + "\n" +
                "PRIMARY FUNCTION: " + primaryFunction + "\n" +
                "STATUS: " + status;
    }


}

class CatDatabase{
    static Map<String, CatInfo> cats = new HashMap<>();

    static {

        cats.put("NIGEL",
                new CatInfo(
                        "NIGEL",
                        "MECHANICAL ENTITY",
                        "UNKNOWN",
                        "TARGET INTERCEPTION",
                        "ACTIVE"
                ));

        cats.put("WILLY",
                new CatInfo(
                        "WILLY",
                        "VOID ENTITY",
                        "UNKNOWN",
                        "REALITY DISTORTION",
                        "ACTIVE"
                ));

        cats.put("ARISTOTLE",
                new CatInfo(
                        "ARISTOTLE",
                        "PSIONIC ENTITY",
                        "UNKNOWN",
                        "MIND CONTROL",
                        "ACTIVE"
                ));

        cats.put("VICTOR",
                new CatInfo(
                        "VICTOR",
                        "CHAOS ENTITY",
                        "CRITICAL",
                        "RULE CORRUPTION",
                        "UNSTABLE"
                ));

    }



}

class Segment{

    double x;
    double y;

    Segment (double x, double y){
        this.x = x;
        this.y = y;
    }

    Segment (Segment other){
        this.x = other.x;
        this.y = other.y;
    }

}


class GamePanel extends JPanel{

    // размер игрока
    private static final int CELL_SIZE = 45;
    private static final int APPLE_SIZE = 30;

    // скорость движения в пикселях за один кадр
    private static final int SPEED = 7;

    //?
    private ArrayList<Segment> snake = new ArrayList<>();

    // приватный рандомный рандом
    private Random random = new Random();

    // координаты яблока
    private Point apple;

    // положение курсора игрока
    private Point cursor = new Point();

    private double directionX = 1;
    private double directionY = 0;

    private static final double TURN_SPEED = 0.18;

    private double angle = 0;
    private static final double MAX_TURN_SPEED = 0.08;

    private static final int SEGMENT_DISTANCE = 55;

    private static final int MAX_SNAKE_SIZE = 15;

    private Timer timer;

    private boolean gameOver = false;



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



        snake.add(new Segment(300, 300));
        snake.add(new Segment(240, 300));
        snake.add(new Segment(180, 300));


        // -> это лямбда-выражение, сокращенная запись метода  @Override
        //    public void actionPerformed(ActionEvent e

        timer = new Timer(16, e -> {
            moveSnake();
            checkBorders();
            checkApple();

            // раз в задержку таймера (16) передаем в метод updateTarget Point координаты змейки (v1)
            // Теперь  метод updateTarget сам решает, когда обновлять информацию.
            // Например: reactionTime = 500 значит:  мышь получает координаты только раз в полсекунды.
            mouse.updateTarget(new Point(
                    (int) snake.get(0).x,
                    (int) snake.get(0).y
            ));
            // затем вызываем метод move
            mouse.move();

            checkEnemyCollision();


            // метод класса JPanel, JAVA понимает ЭТО так this.repaint();
            repaint();
        });

        timer.start();
    }


    public void startGame(){

        createApple();
        cursor.setLocation(
                snake.get(0).x,
                snake.get(0).y);

    }


    private void moveSnake(){

        // получаем голову змейки
        Segment head = snake.get(0);

        // находим координаты центра головы
        double centerX = head.x + CELL_SIZE / 2.0;
        double centerY = head.y + CELL_SIZE / 2.0;


        // считаем вектор - от курсора до головы, затем через atan2 считаем угол
        double targetAngle = Math.atan2(
                cursor.y - centerY,
                cursor.x - centerX
        );


        // рассчитываем необходимый поворот головы змейки до курсора
        double difference = targetAngle - angle;



        // нормализуем через число Пи. например: сейчас 350, нужно 10.
        // мы говорим считать НЕ 10 - 350, а от 350 до 10 (351, 352...360, 0, 1 ... 10)

        while (difference > Math.PI){
            difference -= Math.PI * 2;
        }

        while (difference < -Math.PI){
            difference += Math.PI * 2;
        }

        // проверяем расстояние до курсора через теорему пифагора

        double distanceToCursor =
                Math.sqrt(
                        Math.pow (cursor.x-centerX,2) +
                                Math.pow (cursor.y-centerY,2)
                );


        // поворачиваемся только если курсор дальше, чем клетка от центра головы
        if (distanceToCursor > CELL_SIZE){

            // если разница в крусоре и угле змейки небольшая (считаем разницу в модуле и сравниваем с радианой)
            // то приравниваем угол змейки к углу курсора
            if (Math.abs(difference) < MAX_TURN_SPEED){

                angle = targetAngle;

            }

            // иначе разворачиваем, задавая скорость
            else {

                angle += Math.signum(difference)
                        * MAX_TURN_SPEED;

            }

        }



        // перевод угла обратно в вектор
        double directionX = Math.cos(angle);
        double directionY = Math.sin(angle);

        // двигаем голову
        head.x += directionX * SPEED;
        head.y += directionY * SPEED;


        // подтягиваем хвост за предыдущим сегментом через теорему пифагора
        for (int i = 1; i < snake.size(); i++){


            Segment previous = snake.get(i-1);
            Segment current = snake.get(i);



            double dx = previous.x - current.x;
            double dy = previous.y - current.y;


            double distance =
                    Math.sqrt(dx*dx + dy*dy);



            if (distance > SEGMENT_DISTANCE){


                current.x +=
                        dx / distance *
                                (distance - SEGMENT_DISTANCE);


                current.y +=
                        dy / distance *
                                (distance - SEGMENT_DISTANCE);

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

        Segment head = snake.get(0);

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
                (int) snake.get(0).x,
                (int) snake.get(0).y,
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
            if (snake.size() < MAX_SNAKE_SIZE){
                snake.add(new Segment(
                        snake.get(snake.size() - 1)
                ));

            }
            if (snake.size() == MAX_SNAKE_SIZE){
                System.out.println("WIN");
                System.exit(0);
            }





            // новое яблоко
            createApple();

        }

    }


    private void checkEnemyCollision(){

        Rectangle mouseArea = new Rectangle(
                mouse.getX(),
                mouse.getY(),
                mouse.getSize(),
                mouse.getSize()
        );

        for (int i = 0; i < snake.size(); i++){

            Segment segment = snake.get(i);

            Rectangle segmentArea = new Rectangle(
                    (int) segment.x,
                    (int) segment.y,
                    CELL_SIZE,
                    CELL_SIZE
            );

            if (mouseArea.intersects(segmentArea)){

                if (i == 0){
//                    System.out.println("GAME OVER");
                    endGame();
                    return;
                }

                else {
                    if (i <= 2){
//                        System.out.println("GAME OVER");
                        endGame();
                    }

                    while (snake.size() > i){
                        snake.remove(i);
                    }
                    return;
                }
            }
        }

    }

    private void endGame(){

        if (gameOver){
            return;
        }

        gameOver = true;

        System.out.println("GAME OVER");

        System.exit(0);

//        timer.stop();

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

        for (Segment segment : snake){
            g.fillRect(
                    (int) segment.x,
                    (int) segment.y,
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
    private int speed = 2;
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


