import javax.swing.*; 
public class RaceGUI{
    public static void main(String[] args) {

        JFrame frame = new JFrame("Typing Race Simulator");

        JTextArea display = new JTextArea();
        display.setEditable(false);

        // Test Text
        display.setText(race.getRaceState());

        frame.add(display);

        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}