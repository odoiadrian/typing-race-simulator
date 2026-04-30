import javax.swing.*; 
import java.awt.*;

public class RaceGUI{
    public static void main(String[] args) {

        JFrame frame = new JFrame("Typing Race Simulator");

        JTextArea display = new JTextArea();
        display.setEditable(false);

        JButton startButton = new JButton("Start Race");



        TypingRace race = new TypingRace(50);
        Typist t1 = new Typist('A', "Adrian", 0.6);
        Typist t2 = new Typist('B', "Ben", 0.5);
        Typist t3 = new Typist('C', "Charlie", 0.4);
        race.addTypist(t1, 1);
        race.addTypist(t2, 2);
        race.addTypist(t3, 3);




        display.setText(race.getRaceState());

        frame.setLayout(new BorderLayout());
        frame.add(display, BorderLayout.CENTER);
        frame.add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {


            Timer timer = new Timer(200, null);
            timer.addActionListener(ev -> {
                race.stepRace();
                display.setText(race.getRaceState());

                if (race.isFinished()) {
                    timer.stop();
                }
            });
            timer.start();
        });



        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}