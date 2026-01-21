# -_juego_pong_Java_SE_persistencia_oracle_19C_- :. 
Juego Pong en Java SE con Persistencia en Oracle 19c .  

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/1152d3b5-7c9d-4e20-8c98-04f138c66ad3" />            

<img width="2548" height="1079" alt="image" src="https://github.com/user-attachments/assets/5b0fbc33-e8e3-4d6d-88ef-639e670dae65" />            

<img width="2543" height="1077" alt="image" src="https://github.com/user-attachments/assets/7a1bcbcc-958b-437c-9f42-11e15d15271f" />            

A continuación presento una solución completa, profesional y ejecutable para un juego tipo Pong en Java, desarrollada para IntelliJ IDEA, que cumple con los siguientes requisitos:

* Ventana Java SE (Swing)
* Una bola y dos paletas (palitos) en lados opuestos
* Control de paletas por teclado
* Sistema de puntuación
* Determinación del ganador
* Persistencia del resultado del partido en Oracle Database 19c mediante JDBC.                                    

1. Arquitectura de la Solucion:
```
Tecnologías Utilizadas
Java SE 8+
Swing (JFrame, JPanel, Timer)
JDBC
Oracle Database 19c
IntelliJ IDEA .  

Componentes del Sistema
┌──────────────────────┐
│   PongGame (JFrame)  │
│  ┌────────────────┐ │
│  │ GamePanel      │ │
│  │  - Bola        │ │
│  │  - Paletas     │ │
│  │  - Puntaje     │ │
│  └────────────────┘ │
└───────────▲──────────┘
            │
            │ JDBC
┌───────────┴──────────┐
│ Oracle Database 19c  │
│ Tabla: PARTIDA_PONG │
└──────────────────────┘
```
2. Script SQL – Oracle Database 19c:
Ejecutar previamente en Oracle:
```
CREATE TABLE PARTIDA_PONG (
    ID_PARTIDA NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    FECHA      TIMESTAMP DEFAULT SYSTIMESTAMP,
    JUGADOR_1  VARCHAR2(50),
    JUGADOR_2  VARCHAR2(50),
    PUNTAJE_1  NUMBER,
    PUNTAJE_2  NUMBER,
    GANADOR    VARCHAR2(50)
);
```
3. Clase de Conexión a Oracle:
```
import java.sql.Connection;
import java.sql.DriverManager;

public class OracleConnection {

    public static Connection getConnection() throws Exception {
        String url = "jdbc:oracle:thin:@localhost:1521/ORCLPDB1";
        String user = "USUARIO";
        String password = "PASSWORD";

        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(url, user, password);
    }
}
```
4. Ventana Principal (PongGame):
```
import javax.swing.JFrame;

public class PongGame extends JFrame {

    public PongGame() {
        setTitle("Juego Pong - Java SE");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new PongGame().setVisible(true);
    }
}
```

5. Panel del Juego (GamePanel):
```
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
```

6. Controles del Juego:
* Jugador	Teclas
* Jugador 1	W / S
* Jugador 2	↑ / ↓  

7. Resultados Almacenados en Oracle
Cada partida registrada incluye:

* Fecha y hora de la partida
* Puntaje de ambos jugadores
* Jugador ganador

Consulta de ejemplo:
```
SELECT * FROM PARTIDA_PONG;
```
8. Conclusión
Esta solución:

* Implementa un juego Pong funcional en Java SE
* Utiliza Swing para la interfaz gráfica
* Maneja eventos de teclado y animación
* Registra los resultados del partido en Oracle Database 19c
* Es totalmente compatible con IntelliJ IDEA :. / .
