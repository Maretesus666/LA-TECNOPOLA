package prueba.minijuegos;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class atrapa extends JFrame {
    private JLabel[] imagenes;
    private boolean[] atrapadas;
    private ImageIcon imagenIcon, canonIcon, proyectilIcon;
    private JLabel background, canon;
    private int[] dx, dy;
    private Timer movimientoTimer;
    private ArrayList<Proyectil> proyectiles;
    private Timer disparoTimer;
    private int canonY;
    private boolean movingUp = false;
    private boolean movingDown = false;
    private Timer canonMovementTimer;
    private Timer cooldownTimer;
    private boolean canFire = true;

    public atrapa() {
        setTitle("Minijuego de Disparar Imagenes");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        

        // Cargar im�genes
        ImageIcon fondoIcon = new ImageIcon(getClass().getResource("/imagen/panelatrapa.png"));
        imagenIcon = new ImageIcon(getClass().getResource("/imagen/plotkeys.png"));
        canonIcon = new ImageIcon(getClass().getResource("/imagen/canon.png"));
        proyectilIcon = new ImageIcon(getClass().getResource("/imagen/bala.png"));

        // Configurar fondo
        background = new JLabel(fondoIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondoIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(null);
        setContentPane(background);

        // Configurar cañón
        canon = new JLabel(canonIcon);
        canon.setBounds(10, getHeight() / 2 - canonIcon.getIconHeight() / 2, 
                        canonIcon.getIconWidth(), canonIcon.getIconHeight());
        background.add(canon);
        canonY = canon.getY();

        // Crear imágenes objetivo
        int cantidad = 5;
        imagenes = new JLabel[cantidad];
        atrapadas = new boolean[cantidad];
        dx = new int[cantidad];
        dy = new int[cantidad];

        Random random = new Random();

        for (int i = 0; i < cantidad; i++) {
            imagenes[i] = new JLabel(imagenIcon);
            imagenes[i].setBounds(random.nextInt(getWidth() - 200 - imagenIcon.getIconWidth()) + 200, 
                                  random.nextInt(getHeight() - imagenIcon.getIconHeight()), 
                                  imagenIcon.getIconWidth(), imagenIcon.getIconHeight());
            background.add(imagenes[i]);

            dx[i] = random.nextInt(5) + 1;
            dy[i] = random.nextInt(5) + 1;
        }

        // Inicializar proyectiles
        proyectiles = new ArrayList<>();

        // Configurar timers
        movimientoTimer = new Timer(30, e -> moverImagenes());
        movimientoTimer.start();

        disparoTimer = new Timer(20, e -> moverProyectiles());
        disparoTimer.start();

        canonMovementTimer = new Timer(16, e -> moverCanon());
        canonMovementTimer.start();

        cooldownTimer = new Timer(500, e -> canFire = true);
        cooldownTimer.setRepeats(false);

        // Configurar controles
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        movingUp = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        movingDown = true;
                        break;
                    case KeyEvent.VK_SPACE:
                        dispararProyectil();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        movingUp = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        movingDown = false;
                        break;
                }
            }
        });

        setFocusable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void moverCanon() {
        int dy = 0;
        if (movingUp) dy -= 5;
        if (movingDown) dy += 5;

        canonY = Math.max(0, Math.min(getHeight() - canon.getHeight(), canonY + dy));
        canon.setLocation(canon.getX(), canonY);
    }

    private void dispararProyectil() {
        if (canFire) {
            Proyectil p = new Proyectil(canon.getX() + canon.getWidth(), 
                                        canon.getY() + canon.getHeight() / 2 - proyectilIcon.getIconHeight() / 2);
            proyectiles.add(p);
            background.add(p);

            canFire = false;
            cooldownTimer.restart();
        }
    }

    private void moverImagenes() {
        for (int i = 0; i < imagenes.length; i++) {
            if (!atrapadas[i]) {
                int x = imagenes[i].getX() + dx[i];
                int y = imagenes[i].getY() + dy[i];

                if (x < 200 || x > getWidth() - imagenIcon.getIconWidth()) {
                    dx[i] = -dx[i];
                    x = Math.max(200, Math.min(x, getWidth() - imagenIcon.getIconWidth()));
                }

                if (y < 0 || y > getHeight() - imagenIcon.getIconHeight()) {
                    dy[i] = -dy[i];
                    y = Math.max(0, Math.min(y, getHeight() - imagenIcon.getIconHeight()));
                }

                imagenes[i].setLocation(x, y);
            }
        }
    }

    private void moverProyectiles() {
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            p.mover();

            if (p.getX() > getWidth()) {
                background.remove(p);
                proyectiles.remove(i);
            } else {
                for (int j = 0; j < imagenes.length; j++) {
                    if (!atrapadas[j] && p.getBounds().intersects(imagenes[j].getBounds())) {
                        atrapadas[j] = true;
                        imagenes[j].setVisible(false);
                        background.remove(p);
                        proyectiles.remove(i);
                        verificarEstado();
                        break;
                    }
                }
            }
        }
        background.repaint();
    }

    private void verificarEstado() {
        boolean todasatrapadas = true;
        for (boolean atrapada : atrapadas) {
            if (!atrapada) {
                todasatrapadas = false;
                break;
            }
        }
        if (todasatrapadas) {
            movimientoTimer.stop();
            disparoTimer.stop();
            canonMovementTimer.stop();
           dispose();
        }
    }
    

    private class Proyectil extends JLabel {
        private static final int VELOCIDAD = 10;

        public Proyectil(int x, int y) {
            super(proyectilIcon);
            setBounds(x, y, proyectilIcon.getIconWidth(), proyectilIcon.getIconHeight());
        }

        public void mover() {
            setLocation(getX() + VELOCIDAD, getY());
        }
    }
 
}