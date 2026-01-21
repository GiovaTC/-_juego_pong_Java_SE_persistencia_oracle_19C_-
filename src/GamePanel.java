import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private int ballX = 390, ballY = 240;
    private int ballDX = 4, ballDY = 4;

    private int paddle1Y = 200;
    private int paddle2Y = 200;

    private int score1 = 0;
    private int score2 = 0;

    public GamePanel() {
        timer = new Timer(10, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.fillOval(ballX, ballY, 20, 20);

        g.fillRect(20, paddle1Y, 10, 80);
        g.fillRect(760, paddle2Y, 10, 80);

        g.drawString("Jugador 1: " + score1, 50, 20);
        g.drawString("Jugador 2: " + score2, 650, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ballX += ballDX;
        ballY += ballDY;

        if (ballY <= 0 || ballY >= getHeight() - 20) {
            ballDY *= -1;
        }

        if (ballX <= 30 && ballY >= paddle1Y && ballY <= paddle1Y + 80) {
            ballDX *= -1;
        }

        if (ballX >= 740 && ballY >= paddle2Y && ballY <= paddle2Y + 80) {
            ballDX *= -1;
        }

        if (ballX < 0) {
            score2++;
            resetBall();
        }

        if (ballX > getWidth()) {
            score1++;
            resetBall();
        }

        if (score1 == 5 || score2 == 5) {
            timer.stop();
            saveMatch();
            JOptionPane.showMessageDialog(this,
                    "Ganador: " + (score1 > score2 ? "Jugador 1" : "Jugador 2"));
        }

        repaint();
    }

    private void resetBall() {
        ballX = 390;
        ballY = 240;
        ballDX *= -1;
    }

    private void saveMatch() {
        try (Connection conn = OracleConnection.getConnection()) {
            String sql = "INSERT INTO PARTIDA_PONG (JUGADOR_1, JUGADOR_2, PUNTAJE_1, PUNTAJE_2, GANADOR) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "Jugador 1");
            ps.setString(2, "Jugador 2");
            ps.setInt(3, score1);
            ps.setInt(4, score2);
            ps.setString(5, score1 > score2 ? "Jugador 1" : "Jugador 2");

            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) paddle1Y -= 15;
        if (e.getKeyCode() == KeyEvent.VK_S) paddle1Y += 15;
        if (e.getKeyCode() == KeyEvent.VK_UP) paddle2Y -= 15;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) paddle2Y += 15;
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}   