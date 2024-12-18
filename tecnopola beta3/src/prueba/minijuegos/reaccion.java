package prueba.minijuegos; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class reaccion extends JFrame {
    private JProgressBar barraProgreso;
    private JLabel instruccionLabel, resultadoLabel;
    private Timer timer;
    private int progreso = 0;
    private int zonaSeguraInicio;
    private int zonaSeguraFin;
    private boolean juegoActivo = true;
    private Random random = new Random();

    public reaccion() {
        setTitle("carga");
        setSize(450, 190);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(50, 50, 50)); // Fondo gris oscuro
        setResizable(false);
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        add(barraProgreso, BorderLayout.CENTER);
        instruccionLabel = new JLabel("ESPACIO");
        add(instruccionLabel, BorderLayout.NORTH);
        resultadoLabel = new JLabel("dfsdf ");
        add(resultadoLabel, BorderLayout.SOUTH);

        establecerZonaSegura();

        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (juegoActivo) {
                    progreso = (progreso + 1) % 101;
                    barraProgreso.setValue(progreso);
                    if (progreso >= zonaSeguraInicio && progreso <= zonaSeguraFin) {
                        barraProgreso.setForeground(Color.GREEN);
                    } else {
                        barraProgreso.setForeground(Color.RED);
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && juegoActivo) {
                    verificarResultado();
                }
            }
        });

        setFocusable(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            reiniciarJuego();
        }
    }

    private void establecerZonaSegura() {
        zonaSeguraInicio = random.nextInt(81); // 0-80
        zonaSeguraFin = Math.min(zonaSeguraInicio + 20, 100);
    }

    private void verificarResultado() {
        if (progreso >= zonaSeguraInicio && progreso <= zonaSeguraFin) {
            juegoActivo = false;
            timer.stop();
            dispose();
        } else {
            resultadoLabel.setText("Fallaste. Inténtalo de nuevo.");
            progreso = 0;
            establecerZonaSegura();
        }
    }

    private void reiniciarJuego() {
        progreso = 0;
        establecerZonaSegura();
        juegoActivo = true;
        timer.start();
    }

    public void iniciarJuego() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            reaccion juego = new reaccion();
            juego.iniciarJuego();
        });
    }
}
