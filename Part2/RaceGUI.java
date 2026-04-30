import javax.swing.*; 
import java.awt.*;

public class RaceGUI{
    public static void main(String[] args) {

        JFrame frame = new JFrame("Typing Race Simulator");

        JTextArea display = new JTextArea();
        display.setEditable(false);

        JButton startButton = new JButton("Start Race");

        // Test Text
        TypingRace race = new TypingRace(50);
        race.addTypist(new Typist('A', "Adrian", 0.6), 1);
        race.addTypist(new Typist('B', "Ben", 0.5), 2);
        race.addTypist(new Typist('C', "Charlie", 0.4), 3);
        race.startRace();
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


        frame.add(display);

        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}