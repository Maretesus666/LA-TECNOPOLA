package prueba.minijuegos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AdivinaElNumero extends JFrame {
    private int numeroAleatorio;
    private long tiempoInicio;
    private JTextField entradaNumero;
    private JTextArea leaderboardArea;
    private JButton verificarButton;
    private JButton cerrarButton;
    private JLabel mensajeLabel;
    private List<Long> tiempos;

    public AdivinaElNumero() {
        setTitle("Adivina el Número");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // No cerrar la ventana hasta que se adivine el número
        setLayout(new FlowLayout());

        // Centrando la ventana en la pantalla
        setLocationRelativeTo(null);

        // Deshabilitar el redimensionado de la ventana
        setResizable(false);

        // Hacer la ventana siempre encima de otras
        setAlwaysOnTop(true);

        // Crear los elementos de la interfaz
        entradaNumero = new JTextField(10);
        verificarButton = new JButton("Verificar");
        cerrarButton = new JButton("Cerrar");
        cerrarButton.setEnabled(false);  // Este botón estará deshabilitado al inicio
        mensajeLabel = new JLabel("Adivina el número entre 1 y 100:");
        leaderboardArea = new JTextArea(10, 30);
        leaderboardArea.setEditable(false);

        add(mensajeLabel);
        add(entradaNumero);
        add(verificarButton);
        add(cerrarButton);
        add(new JScrollPane(leaderboardArea));

        // Acciones de los botones
        verificarButton.addActionListener(new VerificarAction());
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Cierra la aplicación cuando se hace clic en "Cerrar"
            }
        });

        // Cargar el leaderboard
        tiempos = cargarLeaderboard();
        actualizarLeaderboard();

        numeroAleatorio = generarNumeroAleatorio();
        tiempoInicio = System.currentTimeMillis(); // Iniciar el cronómetro

        // Evitar que el usuario minimice la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                // No hacer nada cuando se intenta minimizar la ventana
            }
        });
    }

    private int generarNumeroAleatorio() {
        Random random = new Random();
        return random.nextInt(100) + 1; // Número entre 1 y 100
    }

    private List<Long> cargarLeaderboard() {
        List<Long> tiempos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leaderboard.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                tiempos.add(Long.parseLong(linea));
            }
        } catch (IOException e) {
            // Si no se puede leer el archivo, se devuelve una lista vacía
        }
        return tiempos;
    }

    private void guardarLeaderboard() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("leaderboard.txt"))) {
            for (Long tiempo : tiempos) {
                bw.write(tiempo.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualizarLeaderboard() {
        Collections.sort(tiempos);
        leaderboardArea.setText("Leaderboard:\n");
        for (Long tiempo : tiempos) {
            leaderboardArea.append(tiempo + " ms\n");
        }
    }

    private class VerificarAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int numeroIngresado = Integer.parseInt(entradaNumero.getText());
                long tiempoFinal = System.currentTimeMillis();
                long tiempoTotal = tiempoFinal - tiempoInicio;

                if (numeroIngresado < 1 || numeroIngresado > 100) {
                    mensajeLabel.setText("Por favor, ingresa un número entre 1 y 100.");
                } else if (numeroIngresado < numeroAleatorio) {
                    mensajeLabel.setText("Demasiado bajo. Intenta de nuevo.");
                } else if (numeroIngresado > numeroAleatorio) {
                    mensajeLabel.setText("Demasiado alto. Intenta de nuevo.");
                } else {
                    mensajeLabel.setText("¡Correcto! Tardaste " + tiempoTotal + " ms.");
                    tiempos.add(tiempoTotal);
                    guardarLeaderboard();
                    actualizarLeaderboard();
                    numeroAleatorio = generarNumeroAleatorio(); // Reiniciar el juego
                    tiempoInicio = System.currentTimeMillis(); // Reiniciar el cronómetro
                    // Cambiar a la opción de cerrar el juego
                    verificarButton.setEnabled(false); // Desactivar el botón de verificar
                    cerrarButton.setEnabled(true);   // Habilitar el botón de cerrar
                }
            } catch (NumberFormatException ex) {
                mensajeLabel.setText("Por favor, ingresa un número válido.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdivinaElNumero juego = new AdivinaElNumero();
            juego.setVisible(true);
        });
    }
}
