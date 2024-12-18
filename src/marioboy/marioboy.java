package marioboy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Image;
import java.util.Comparator;

public class marioboy extends JFrame {

    private GamePanel gamePanel;
    private Image idleImage;
    private Image runImage;
    private Image jumpImage;
    private Image fallImage;
    private Image dashImage;

    // PROPIEDADES DEL PJ
    static int pj_x = 40;
    static int pj_y = 560;
    static int inicio_pj_x =40;
    static int inicio_pj_y = 0;
    int pj_ancho = 32;
    int pj_alto = 32;
    int velMov = 5;
    
    // PROPIEDADES JUEGO
    int ypiso = 900;
    boolean rPressed = false, spacePressed = false, sPressed = false, aPressed = false, dPressed = false;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    // Propiedades del dash
    int dashVel = 20;
    int dashDuration = 10;
    int dashTicks = 0;
    int dashCooldown = 500;
    int dashCooldownTicks = 0;
    boolean hasDashed = false;
    boolean canDashInAir = true;
    
 // Propiedades del salto
    boolean jumping = false;
    boolean onGround =false;
    int jumpVelocity = 0;
    int gravity = 2 ;
    int initialJumpVelocity = -28;
   int fallVelocity = 0;
    int maxFallVelocity = 10;
    // Niveles y muertes
    public static int nivel = 1;
    public static int muertes = 0;
    int width = 900;
    int height = width/2;

    // Temporizador
    Timer timer;
    
    // Lista objeto y textos
    private List<Objeto> objetos;
    private List<ObjetoDecorador> objetosDecorativos; 
    private List<Texto> textos;
    
    private Random random = new Random();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    marioboy frame = new marioboy();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
   

    public marioboy() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        gamePanel = new GamePanel();
        gamePanel.setLayout(new BorderLayout(0, 0));
        setContentPane(gamePanel);

        // Establecer un tamaño inicial
        setSize(900, 700
        	); // Por ejemplo, ancho 900 y alto 450
        objetos = new ArrayList<>();
        objetosDecorativos = new ArrayList<>();
        inicializarObjetos(getWidth(), getHeight());
        textos = new ArrayList<>();
        inicializarTextos(getWidth(), getHeight());
        
      
      

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (isGameOver && rPressed == true) {
                    nivel = 1;
                    muertes = 0;
                    isGameOver = false;
                    inicializarObjetos( width,  height);
                    inicializarTextos( width,  height);
                    return;
                }
               
                if (key == KeyEvent.VK_S) sPressed = true;
                if (key == KeyEvent.VK_A) aPressed = true;
                if (key == KeyEvent.VK_D) dPressed = true;
                if (key == KeyEvent.VK_R) {        
                	rPressed = true;
                	pj_y = inicio_pj_y;
                	pj_x = inicio_pj_x;
                }
                if (key == KeyEvent.VK_ESCAPE) {
                    isPaused = !isPaused; 
                    return;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_SPACE) spacePressed = false;
                if (key == KeyEvent.VK_S) sPressed = false;
                if (key == KeyEvent.VK_A) aPressed = false;
                if (key == KeyEvent.VK_D) dPressed = false;

                if (key == KeyEvent.VK_R) rPressed = false;
            }
        });
        setFocusable(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                int size = Math.min(width, height);
                setSize(size, size);
            }
        });
    
    }
    private void actualizarDimensiones() {
        int width = gamePanel.getWidth();
        int height = gamePanel.getHeight();

        setSize(900, width/2); // Por ejemplo, ancho 900 y alto 450
        // Verifica si las dimensiones son válidas
        if (width > 0 && height > 0) {
            // Actualiza objetos y textos según el nuevo tamaño
            inicializarObjetos(width, height);
            inicializarTextos(width, height);
            gamePanel.repaint(); // Fuerza un repaint para reflejar los cambios
        }
        
    }
    void inicializarObjetos(int width, int height) {
        objetos.clear();
        objetosDecorativos.clear();
        
        // Usa `width` y `height` en lugar de las variables estáticas
        if (nivel == 10) {
            // Tu código existente
        } else if (nivel == 1) {
            objetosDecorativos.add(new ObjetoDecorador(0, 0, width, height, "/imagen/cocina1.png", 0));
            objetosDecorativos.add(new ObjetoDecorador(width/6,height/6,200,60, "/imagen/cubo.png", 0));
        }
    }

     
    
    void inicializarTextos(int width, int height) {
        textos.clear();

         if (nivel == 1) {
        } else if (nivel == 2) {
            textos.add(new Texto("TUTORIAL", getWidth() / 2 - 90, 60, 30, Color.BLACK));
            textos.add(new Texto("R reiniciar nivel", getWidth() / 2 - 85, 100, 20, Color.BLACK));       
        } else if (nivel == 3) {
            textos.add(new Texto("TUTORIAL", getWidth() / 2 - 90, 60, 30, Color.BLACK));
            textos.add(new Texto("N dash", getWidth() / 2 - 50, 100, 20, Color.BLACK));
        } else if (nivel == 4) {
        	textos.add(new Texto("TUTORIAL", getWidth() / 2 - 90, 60, 30, Color.WHITE));
            textos.add(new Texto("Las plataformas se mueven", getWidth() / 2 - 150, 100, 20, Color.WHITE));
        } else if (nivel == 5) {
        	textos.add(new Texto("TUTORIAL", getWidth() / 2 - 90, 60, 30, Color.WHITE));
            textos.add(new Texto("Los PINCHOS te matan", getWidth() / 2 - 150, 100, 20, Color.WHITE));
        }
        else if (nivel == 11) {

        	textos.add(new Texto("FIN", getWidth() / 2 - 65, 30, 30, Color.white));
        	textos.add(new Texto("Creditos", getWidth() / 2 - 90, 60, 30, Color.CYAN));
            textos.add(new Texto("MAGALLANES", getWidth() / 2 - 100, 100, 20, Color.PINK));
            textos.add(new Texto("BELLINI", getWidth() / 2 - 75, 200, 20, Color.ORANGE));
            textos.add(new Texto("COLOMBO", getWidth() / 2 - 85, 300, 20, Color.BLUE));
            textos.add(new Texto("MARATEA", getWidth() / 2 - 85, 400, 20, Color.RED));
            textos.add(new Texto("LAMENSA", getWidth() / 2 - 85, 500, 20, Color.BLACK));

            textos.add(new Texto("Gracias por todo", getWidth() / 2 + 250, 550, 20, Color.WHITE));
            textos.add(new Texto("Apreta R para voler a jugar", getWidth() / 2 + 200, 575, 20, Color.YELLOW));
        }
    }


 
   
    

    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            objetosDecorativos.sort(Comparator.comparingInt(ObjetoDecorador::getZIndex));
            for (ObjetoDecorador objetoDecorador : objetosDecorativos) {
                objetoDecorador.dibujar(g2d);
            }

            dibujarObjeto(g2d, 40,10 , 60, 32, Color.BLACK);
            g2d.setColor(Color.white);
            g2d.drawString("Nivel: " + nivel + height, 40, 20);
            Image currentImage = idleImage; // Por defecto, estado idle

          

          
            inicializarTextos( width,  height);
            for (Objeto objeto : objetos) {
                objeto.dibujar(g2d);
            }
            for (Objeto objeto : objetos) {
                objeto.dibujar(g2d);
                if (objeto instanceof ObjetoMovil) {
                    ((ObjetoMovil) objeto).mover();
                }
            }
            for (Texto texto : textos) {
                texto.dibujar(g2d);
            }
            
      

            if (marioboy.this.isPaused) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                g2d.drawString("PAUSADO", getWidth() / 2 - 100, getHeight() / 2);
            }

       
        }

        
        }
        public void dibujarObjeto(Graphics2D g2d, int x, int y, int ancho, int alto, Color color) {
            g2d.setColor(color);
            g2d.fillRect(x, y, ancho, alto);
        }
        
    }

   


      