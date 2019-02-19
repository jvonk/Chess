import javax.swing.*;
public class Runner {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        frame.add(new Board(800, 800));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
