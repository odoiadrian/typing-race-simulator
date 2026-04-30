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
        Typist t3 = 
        race.addTypist(, 1);
        race.addTypist(, 2);
        race.addTypist(new Typist('C', "Charlie", 0.4), 3);

        double initialAccuracy1 = race.seat1Typist.getAccuracy();
        double initialAccuracy2 = race.seat2Typist.getAccuracy();
        double initialAccuracy3 = race.seat3Typist.getAccuracy();
        double winnerAccuracy = 0;

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
                    display.append("\n\nRace Finished!");
                }
            });
            timer.start();
        });



        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}