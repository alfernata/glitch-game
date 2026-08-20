import javax.swing.*;
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


