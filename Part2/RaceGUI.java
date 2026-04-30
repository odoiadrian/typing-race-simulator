import javax.swing.*; 
public class RaceGUI{
    public static void main(String[] args) {

        JFrame frame = new JFrame("Typing Race Simulator");

        JTextArea display = new JTextArea();
        display.setEditable(false);

        frame.add(display);
    }
}