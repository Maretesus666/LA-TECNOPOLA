package prueba.minijuegos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class botones extends JFrame {

    private JButton[] botones;
    private boolean[] estados;
    private ImageIcon onIcon;
    private ImageIcon offIcon;
    private JLabel background;

    public botones() {
        setTitle("Minijuego de Interruptores");
        setSize(500, 400);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        

        // Cargar imágenes
        onIcon = new ImageIcon(getClass().getResource("/imagen/ON.png"));
        offIcon = new ImageIcon(getClass().getResource("/imagen/OFF.png"));

        // Crear fondo
        ImageIcon fondoIcon = new ImageIcon(getClass().getResource("/imagen/panel.png"));
        background = new JLabel(fondoIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondoIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setBounds(0, 0, 400, 300);
        setContentPane(background);
        background.setLayout(null); // Establecer el layout del fondo a null

        // Crear interruptores
        botones = new JButton[3];
        estados = new boolean[3];
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            estados[i] = random.nextBoolean();
            botones[i] = new JButton(estados[i] ? onIcon : offIcon);
            botones[i].setBounds(0, 0, onIcon.getIconWidth(), onIcon.getIconHeight());
            int index = i;
            botones[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    estados[index] = !estados[index];
                    botones[index].setIcon(estados[index] ? onIcon : offIcon);
                    verificarEstado();
                }
            });
            // Eliminar brillo y otros efectos al pasar el ratón
            botones[i].setBorderPainted(false);
            botones[i].setFocusPainted(false);
            botones[i].setContentAreaFilled(false);

            background.add(botones[i]);
        }

       
        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
centrarBotones();
        setVisible(true);
    }

    private void centrarBotones() {
        int panelWidth = getWidth();
        int buttonWidth = botones[0].getWidth();
        int totalWidth = buttonWidth * 3 + 20 * 2; // Espaciado entre botones

        int startX = (panelWidth - totalWidth) / 2 -panelWidth/100;
        int y = getHeight() / 2 - botones[0].getHeight() / 2 -20;

        for (int i = 0; i < botones.length; i++) {
            botones[i].setLocation(startX + i * (buttonWidth + 20), y);
        }
    }

    private void verificarEstado() {
        for (boolean estado : estados) {
            if (!estado) {
                return;
            }
        }
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            botones juego = new botones();
            juego.setVisible(true);
        });
    }


}