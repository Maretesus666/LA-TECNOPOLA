package marioboy;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

public class Menu extends JFrame {
	private Clip clip;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private marioboy gamePanel;
    private OptionsPanel optionsPanel;
    private CreditsPanel creditsPanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Menu frame = new Menu();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
    }
    

    public Menu() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1000,777);
        Cursor customCursor = createCustomCursor("/imagen/facun.jpg", "CustomCursor", 320);
        setCursor(customCursor);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel();
        gamePanel = new marioboy();
        optionsPanel = new OptionsPanel();
        creditsPanel = new CreditsPanel();
        menuPanel.setCursor(customCursor);
        gamePanel.setCursor(customCursor);
        optionsPanel.setCursor(customCursor);
        creditsPanel.setCursor(customCursor);
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel.getContentPane(), "GAME");
        mainPanel.add(optionsPanel, "OPTIONS");
        mainPanel.add(creditsPanel, "CREDITS");

        cardLayout.show(mainPanel, "MENU");

        setContentPane(mainPanel);

        // Listener para redimensionar elementos internamente
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        playMusicLoop("/audio/cronica.wav");
    }
    private void playMusicLoop(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    private Cursor createCustomCursor(String imagePath, String cursorName, int cursorSize) {
        try {
            BufferedImage cursorImage = ImageIO.read(getClass().getResource(imagePath));
            Image scaledImage = cursorImage.getScaledInstance(cursorSize, cursorSize, Image.SCALE_SMOOTH);
            BufferedImage scaledBufferedImage = new BufferedImage(cursorSize, cursorSize, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2d = scaledBufferedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            return Toolkit.getDefaultToolkit().createCustomCursor(scaledBufferedImage, new Point(0, 0), cursorName);
        } catch (IOException e) {
            e.printStackTrace();
            return Cursor.getDefaultCursor();
        }
    }
    private JButton createButton(Image image, ActionListener listener) {
        JButton button = new JButton(new ImageIcon(image));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(listener);
        
        // Cambiar el cursor al pasar sobre el botón
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(getCursor()); // Volver al cursor personalizado
            }
        });
        
        return button;
    }
    private void resizeComponents() {
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;

        menuPanel.resizeComponents(width, height);
        optionsPanel.resizeComponents(width, height);
        creditsPanel.resizeComponents(width, height);
    }

    class MenuPanel extends JPanel {
        private Image backgroundImage;
        private Image playButtonImage;
        private Image optionsButtonImage;
        private Image creditsButtonImage;
        private JButton playButton;
        private JButton optionsButton;
        private JButton creditsButton;

        public MenuPanel() {
            setLayout(null);
            backgroundImage = new ImageIcon(getClass().getResource("/imagen/fondomenu.png")).getImage();
            playButtonImage = new ImageIcon(getClass().getResource("/imagen/JUGAR.png")).getImage();
            optionsButtonImage = new ImageIcon(getClass().getResource("/imagen/AJUSTES.png")).getImage();
            creditsButtonImage = new ImageIcon(getClass().getResource("/imagen/cubo.png")).getImage();

            playButton = createButton(playButtonImage, e -> cardLayout.show(mainPanel, "GAME"));
            optionsButton = createButton(optionsButtonImage, e -> cardLayout.show(mainPanel, "OPTIONS"));
            creditsButton = createButton(creditsButtonImage, e -> cardLayout.show(mainPanel, "CREDITS"));

            add(playButton);
            add(optionsButton);
            add(creditsButton);
        }

        private JButton createButton(Image image, ActionListener listener) {
            JButton button = new JButton(new ImageIcon(image));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(listener);
            return button;
        }

        public void resizeComponents(int panelWidth, int panelHeight) {
            // Calculate the background image size while maintaining the 4:3 aspect ratio
            int bgWidth = panelWidth;
            int bgHeight = (int) (bgWidth * 3 / 4);
            if (bgHeight > panelHeight) {
                bgHeight = panelHeight;
                bgWidth = (int) (bgHeight * 4 / 3);
            }

            // Calculate button size and position based on background image size
            int buttonWidth = (int) (bgWidth * 0.2);
            int buttonHeight = (int) (buttonWidth * 3 / 7);

            int buttonX = (bgWidth - buttonWidth) / 2;
            int playButtonY = (int) (bgHeight * 0.3);
            int optionsButtonY = playButtonY + buttonHeight + 20;
            int creditsButtonY = optionsButtonY + buttonHeight + 20;

            playButton.setBounds(buttonX, playButtonY, buttonWidth, buttonHeight);
            playButton.setIcon(new ImageIcon(playButtonImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));

            optionsButton.setBounds(buttonX, optionsButtonY, buttonWidth, buttonHeight);
            optionsButton.setIcon(new ImageIcon(optionsButtonImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));

            creditsButton.setBounds(buttonX, creditsButtonY, buttonWidth, buttonHeight);
            creditsButton.setIcon(new ImageIcon(creditsButtonImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            // Establecer el fondo negro
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Calculate background image size while maintaining the 4:3 aspect ratio
            int bgWidth = panelWidth;
            int bgHeight = (int) (bgWidth * 3 / 4);
            if (bgHeight > panelHeight) {
                bgHeight = panelHeight;
                bgWidth = (int) (bgHeight * 4 / 3);
            }

            g.drawImage(backgroundImage, 0, 0, bgWidth , bgHeight, this);

            // Resize components based on background image size
            resizeComponents(bgWidth, bgHeight);
        }
    }

    class OptionsPanel extends JPanel {
        private Image backgroundImage;
        private JButton backButton;

        public OptionsPanel() {
            setLayout(null);
            backgroundImage = new ImageIcon(getClass().getResource("/imagen/platap.png")).getImage();

            backButton = new JButton(new ImageIcon(getClass().getResource("/imagen/puerta.png")));
            backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
            backButton.setBorderPainted(false);
            backButton.setContentAreaFilled(false);
            add(backButton);
        }

        public void resizeComponents(int panelWidth, int panelHeight) {
            // Calculate the background image size while maintaining the 4:3 aspect ratio
            int bgWidth = panelWidth;
            int bgHeight = (int) (bgWidth * 3 / 4);
            if (bgHeight > panelHeight) {
                bgHeight = panelHeight;
                bgWidth = (int) (bgHeight * 4 / 3);
            }

            // Calculate button size and position based on background image size
            int buttonWidth = (int) (bgWidth * 0.1);
            int buttonHeight = (int) (buttonWidth * 3 / 4);

            int buttonX = (bgWidth - buttonWidth) / 2;
            int buttonY = bgHeight - buttonHeight - 20;

            backButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
            backButton.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/imagen/puerta.png")).getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            // Calculate background image size while maintaining the 4:3 aspect ratio
            int bgWidth = panelWidth;
            int bgHeight = (int) (bgWidth * 3 / 4);
            if (bgHeight > panelHeight) {
                bgHeight = panelHeight;
                bgWidth = (int) (bgHeight * 4 / 3);
            }

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(backgroundImage, 0, 0, bgWidth, bgHeight, this);

            // Resize components based on background image size
            resizeComponents(bgWidth, bgHeight);
        }
    }

    class CreditsPanel extends JPanel {
        private Image backgroundImage;
        private JButton backButton;

        public CreditsPanel() {
            setLayout(null);
            backgroundImage = new ImageIcon(getClass().getResource("/imagen/POLLOLEVITA.png")).getImage();

            backButton = new JButton(new ImageIcon(getClass().getResource("/imagen/ptarena.png")));
            backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
            backButton.setBorderPainted(false);
            backButton.setContentAreaFilled(false);
            add(backButton);
        }

        public void resizeComponents(int panelWidth, int panelHeight) {
            // Calculate the background image size while maintaining the 4:3 aspect ratio
            int bgWidth = panelWidth;
            int bgHeight = (int) (bgWidth * 3 / 4);
            if (bgHeight > panelHeight) {
                bgHeight = panelHeight;
                bgWidth = (int) (bgHeight * 4 / 3);
            }

            // Calculate button size and position based on background image size
            int buttonWidth = (int) (bgWidth * 0.1);
            int buttonHeight = (int) (buttonWidth * 3 / 4);

            int buttonX = (bgWidth - buttonWidth) / 2;
            int buttonY = bgHeight - buttonHeight - 20;

            backButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
            backButton.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/imagen/ptarena.png")).getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            // Calculate background image size while maintaining the 4:3 aspect ratio
            int bgWidth = panelWidth;
            int bgHeight = (int) (bgWidth * 3 / 4);
            if (bgHeight > panelHeight) {
                bgHeight = panelHeight;
                bgWidth = (int) (bgHeight * 4 / 3);
            }

            g.drawImage(backgroundImage, 0, 0, bgWidth, bgHeight, this);

            // Resize components based on background image size
            resizeComponents(bgWidth, bgHeight);
        }
    }
}