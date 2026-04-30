import javax.swing.*; 

public class RaceGUI{
    public static void main(String[] args) {

        JFrame frame = new JFrame("Typing Race Simulator");

        JTextArea display = new JTextArea();
        display.setEditable(false);

        // Test Text
        TypingRace race = new TypingRace(50);
        race.addTypist(new Typist('A', "Adrian", 0.6), 1);
        race.addTypist(new Typist('B', "Ben", 0.5), 2);
        race.addTypist(new Typist('C', "Charlie", 0.4), 3);
        race.startRace();
        display.setText(race.getRaceState());

        frame.add(display);

        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}