import javax.swing.JFrame;

public class PongGame extends JFrame {

    public PongGame() {
        setTitle("Juego Pong - java se");
        setSize(800,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new PongGame().setVisible(true);
    }
}