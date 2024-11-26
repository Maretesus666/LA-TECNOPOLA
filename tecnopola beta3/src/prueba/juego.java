package prueba;

import javax.imageio.ImageIO;
import javax.swing.*;

import prueba.Menu.MenuPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class juego extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton botonX;
    private JButton botonOK;
    private JButton botonInfo;
    private JButton botonContinuar;
    private JButton botonVolver;    // Bot�n para regresar al men� principal
    private JLabel mFinal;          // Mensaje "TE MATARON"
    private JLabel contadorLabel;
    private JLabel temporizadorLabel;
    private JLabel diaLabel;
    private ArrayList<String> lista1, lista2, lista3, lista4;
    private ArrayList<String> listaPersonajes = new ArrayList<>();
    private ArrayList<String> listaNombresPJ = new ArrayList<>();
    private ArrayList<String> listaApellidosPJ = new ArrayList<>();
    private String nombrePJ = "Oficial";
    private String apellidoPJ = "Banana";
    private Random random;
    private int contadorBien = 0, contadorMal = 0, dinero = 0, dia = 1, valorxp = 1; //Valor por persona
    private Timer timer;
    private int totaltiempo = 1, tiempoRestante = totaltiempo; // 2 minutos
    private Image botonOK_img, botonX_img, botonINFO_img, fondoOriginal, fondoTransicion, fondoMuerte, fondoreal, pantalla;
    private JLabel bienLabel, malLabel, dineroGanadoLabel, dineroTotalLabel, diaLabeltr; //Labels de trsn
    private JLabel personajeLabel;
    private String rutaImagenPersonaje = "/imagen/IMAGENES Y GIF/pj/polimono.png"; // Cambiar por la ruta correcta


    public juego() {
        setTitle("JUEGO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 777);
        setLocationRelativeTo(null);
        
        Cursor customCursor = createCustomCursor("/imagen/cursor.png", "CustomCursor", 32);
        setCursor(customCursor);
        
        // Fondo y botones
        fondoOriginal = new ImageIcon(getClass().getResource("/imagen/fondojuego.png")).getImage();
        fondoTransicion = new ImageIcon(getClass().getResource("/imagen/IMAGENES MENU/pixil-frame-0.png")).getImage();
        fondoMuerte = new ImageIcon(getClass().getResource("/imagen/IMAGENES Y GIF/pantallamuerte.png")).getImage();
        fondoreal = fondoOriginal;
        
        pantalla = new ImageIcon(getClass().getResource("/imagen/pantalla.png")).getImage();
        botonOK_img = new ImageIcon(getClass().getResource("/imagen/boton si.png")).getImage();
        botonX_img = new ImageIcon(getClass().getResource("/imagen/boton no.png")).getImage();
        botonINFO_img = new ImageIcon(getClass().getResource("/imagen/BOTONINFO.png")).getImage();
        
        // Panel con fondoOriginal personalizado
        PanelConFondo panel = new PanelConFondo();
        panel.setLayout(null);
        setContentPane(panel);

        // Fuente personalizada
        Font fuentePersonalizada = cargarFuentePersonalizada("recursos/fonts/Pixelon-OGALo.ttf", 24f);

        personajeLabel = new JLabel();
        personajeLabel.setIcon(new ImageIcon(getClass().getResource(rutaImagenPersonaje)));
        panel.add(personajeLabel);

        
        // Area de texto
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(fuentePersonalizada.deriveFont(20f));
        scrollPane = new JScrollPane(textArea);
        add(scrollPane);
                
        // Botones configurados
        botonX = crearBotonConImagen(botonX_img);
        panel.add(botonX);

        botonOK = crearBotonConImagen(botonOK_img);
        panel.add(botonOK);

        botonInfo = crearBotonConImagen(botonINFO_img);
        panel.add(botonInfo);
        
        // Inicializar el mensaje "TE MATARON"
        mFinal = new JLabel("TE MATARON", SwingConstants.CENTER);
        mFinal.setFont(new Font("Arial", Font.BOLD, 40));
        mFinal.setForeground(Color.RED);
        mFinal.setBounds(100, 200, 600, 50); // Ajustar posici�n y tama�o seg�n dise�o
        mFinal.setVisible(false);
        add(mFinal);

        // Inicializar el bot�n "Volver"
        botonVolver = new JButton("CERRAR");
        botonVolver.setBounds(250, 300, 200, 50);
        botonVolver.setFont(new Font("Arial", Font.PLAIN, 20));
        botonVolver.setVisible(false);
        botonVolver.addActionListener(e -> {
            // L�gica para ir al men� principal
            this.dispose(); // Cierra la ventana actual
        });
        add(botonVolver);

        
        botonContinuar = crearBotonConImagen(botonOK_img); // Usamos la misma imagen que el botón OK
        botonContinuar.setVisible(false); // Inicialmente oculto
        panel.add(botonContinuar);
        botonContinuar.addActionListener(e -> {
            // Restaurar el estado del día
            fondoreal = fondoOriginal;
            bienLabel.setVisible(false);
            malLabel.setVisible(false);
            dineroGanadoLabel.setVisible(false);
            dineroTotalLabel.setVisible(false);
            botonContinuar.setVisible(false);
            diaLabeltr.setVisible(false);
            reiniciarDia();
        });
        //Etiquetas de transicion
        bienLabel = crearEtiquetaTransicion();
        malLabel = crearEtiquetaTransicion();
        dineroGanadoLabel = crearEtiquetaTransicion();
        dineroTotalLabel = crearEtiquetaTransicion();
        diaLabeltr = crearEtiquetaTransicion();

        panel.add(bienLabel);
        panel.add(malLabel);
        panel.add(dineroGanadoLabel);
        panel.add(dineroTotalLabel);
        panel.add(diaLabeltr);

        // Ubicacion inicial de etiquetas
        bienLabel.setVisible(false);
        malLabel.setVisible(false);
        dineroGanadoLabel.setVisible(false);
        dineroTotalLabel.setVisible(false);

        // Contador y etiquetas
        contadorLabel = new JLabel("Bien: 0   Mal: 0");
        contadorLabel.setFont(fuentePersonalizada.deriveFont(20f));
        contadorLabel.setForeground(Color.WHITE);
        panel.add(contadorLabel);

        temporizadorLabel = new JLabel("Tiempo: 00:10");
        temporizadorLabel.setFont(fuentePersonalizada.deriveFont(20f));
        panel.add(temporizadorLabel);

        diaLabel = new JLabel("Dia: 1");
        diaLabel.setFont(fuentePersonalizada.deriveFont(20f));
        diaLabel.setForeground(Color.WHITE);
        panel.add(diaLabel);

        // Inicializaci�n l�gica
        lista1 = new ArrayList<>();
        lista2 = new ArrayList<>();
        lista3 = new ArrayList<>();
        lista4 = new ArrayList<>();
        cargarListasDesdeArchivo("recursos/listas.txt");
        random = new Random();
        iniciarTemporizador(tiempoRestante);
        
        cargarPersonajesDesdeArchivo("recursos/lista_pj.txt");  // Cargar los personajes desde el archivo

        // Accion del boton X
        botonX.addActionListener(e -> {
        	int indiceAleatorio = random.nextInt(listaPersonajes.size()); 

            // Obtener la ruta de la imagen
            String rutaImagenAleatoria = listaPersonajes.get(indiceAleatorio);
            nombrePJ = listaNombresPJ.get(indiceAleatorio);  // Nombre del personaje
            apellidoPJ = listaApellidosPJ.get(indiceAleatorio);  // Apellido del personaje

            // Actualizar la imagen del personaje
            ImageIcon imagenPersonaje = new ImageIcon(getClass().getResource(rutaImagenAleatoria));
            Image imagenEscalada = imagenPersonaje.getImage().getScaledInstance(personajeLabel.getWidth(),
                                                                                personajeLabel.getHeight(),
                                                                                Image.SCALE_SMOOTH);
            personajeLabel.setIcon(new ImageIcon(imagenEscalada));
        	
            // Verificar si debe aparecer un minijuego
            verificarMinijuego();

            // Continuar con la lógica normal
            boolean esFalso = esFalsoEnPantalla();
            if (esFalso) {
                contadorBien++;
            } else {
                contadorMal++;
            }
            actualizarContadores();
            mostrarTextosAleatorios();
        });

        //Accion del boton OK
        botonOK.addActionListener(e -> {
            // Elegir aleatoriamente un personaje de la lista
            int indiceAleatorio = random.nextInt(listaPersonajes.size()); 

            // Obtener la ruta de la imagen
            String rutaImagenAleatoria = listaPersonajes.get(indiceAleatorio);
            nombrePJ = listaNombresPJ.get(indiceAleatorio);  // Nombre del personaje
            apellidoPJ = listaApellidosPJ.get(indiceAleatorio);  // Apellido del personaje

            // Actualizar la imagen del personaje
            ImageIcon imagenPersonaje = new ImageIcon(getClass().getResource(rutaImagenAleatoria));
            Image imagenEscalada = imagenPersonaje.getImage().getScaledInstance(personajeLabel.getWidth(),
                                                                                personajeLabel.getHeight(),
                                                                                Image.SCALE_SMOOTH);
            
            personajeLabel.setIcon(new ImageIcon(imagenEscalada));

            // Verificar si debe aparecer un minijuego
            verificarMinijuego();

            // Continuar con la lógica normal
            boolean esFalso = esFalsoEnPantalla();
            if (esFalso) {
                contadorMal++;
            } else {
                contadorBien++;
            }
            actualizarContadores();
            mostrarTextosAleatorios();
        });


        // Accion para el boton de informacion
        botonInfo.addActionListener(e -> {
            Info infoVentana = new Info(lista1, lista2, lista3, lista4);
            infoVentana.setVisible(true);
        });

        
        // Listener de redimensionamiento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();

                scrollPane.setBounds((int) (width * 0.5), (int) (height * 0.1), (int) (width * 0.47), (int) (height * 0.6));
             // Configuración del personajeLabel (imagen)
                personajeLabel.setBounds((int) (width * 0.5 - scrollPane.getWidth()), // A la izquierda del textArea
                						(int) (scrollPane.getY() + scrollPane.getHeight() * 0.12), // Desplazar hacia abajo un 10% de la altura de scrollPane
                						(int) (scrollPane.getWidth() * 1), // Mismo ancho reducido
                						(int) (scrollPane.getHeight() * 0.952)); // Mismo alto reducido
                
                // Reducir el tamaño de la imagen (por ejemplo, 90% del tamaño original)
                int nuevaAnchura = (int) (personajeLabel.getWidth() * 1); // 90% del tamaño original
                int nuevaAltura = (int) (personajeLabel.getHeight() * 1); // 90% del tamaño original

                // Escalado de la imagen para que ocupe el 90% del espacio del personajeLabel
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(rutaImagenPersonaje));
                Image imagenEscalada = originalIcon.getImage().getScaledInstance(nuevaAnchura,
                                                                               nuevaAltura,
                                                                               Image.SCALE_SMOOTH);

                // Asigna la imagen escalada al JLabel
                personajeLabel.setIcon(new ImageIcon(imagenEscalada));
                
                int buttonWidth = (int) (width * 0.2);
                int buttonHeight = (int) (height * 0.1);

                botonX.setBounds((width / 2 - buttonWidth - 110), (int) (height * 0.8), buttonWidth, buttonHeight);
                botonOK.setBounds((width / 2 + 110), (int) (height * 0.8), buttonWidth, buttonHeight);

                int infoButtonSize = buttonHeight;
                botonInfo.setBounds(width - infoButtonSize - 60, (int) (height * 0.8), infoButtonSize, infoButtonSize);

                // Posicion del boton "Continuar"
                botonContinuar.setBounds((width / 2 - buttonWidth / 2), (int) (height * 0.8), buttonWidth, buttonHeight);

                contadorLabel.setBounds(width - 200, 10, 180, 30);
                temporizadorLabel.setBounds(10, 10, 180, 30);
                diaLabel.setBounds(10, 40, 180, 30);
               
            }
        });

        // Mostrar valores iniciales
        mostrarTextosAleatorios();
    }

    // Clase para manejar el fondo
    private class PanelConFondo extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(fondoreal, 0, 0, getWidth(), getHeight(), this);
            repaint();
        }
    }

    // Crear bot�n con imagen personalizada
    private JButton crearBotonConImagen(Image imagen) {
        JButton boton = new JButton(new ImageIcon(imagen)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            }
        };
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        return boton;
    }
    
    private void iniciarTemporizador(int segundos) {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tiempoRestante > 0) {
                    tiempoRestante--;
                    int minutos = tiempoRestante / 60;
                    int segundos = tiempoRestante % 60;
                    temporizadorLabel.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
                    temporizadorLabel.setForeground(Color.WHITE);
                } else {
                    timer.stop();
                    mostrarMensajeFinal();
                }
            }
        });
        timer.start();
    }

 // Metodo para crear etiquetas de transicion
    private JLabel crearEtiquetaTransicion() {
        Font fuentePersonalizada = cargarFuentePersonalizada("recursos/fonts/Pixelon-OGALo.ttf", 24f);
        JLabel label = new JLabel();
        label.setFont(fuentePersonalizada.deriveFont(28f));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(100, 100, 800, 40); // Ubicación provisional, será ajustada
        return label;
    }

    // Mostrar "modo transición"
    private void mostrarModoTransicion() {
    	fondoreal = fondoTransicion; 
        repaint(); 
        
        bienLabel.setText("Bien: " + contadorBien);
        malLabel.setText("Mal: " + contadorMal);
        int dineroGanado = (contadorBien * valorxp) - contadorMal;
        dineroGanadoLabel.setText("Dinero ganado: " + dineroGanado);
        dineroTotalLabel.setText("Dinero total: " + dinero);
        diaLabeltr.setText("DIA: " + dia);


        // Reajustar las ubicaciones de las etiquetas
        int centerX = getWidth() / 2 - 400;
        bienLabel.setBounds(centerX, 200, 800, 40);
        malLabel.setBounds(centerX, 260, 800, 40);
        dineroGanadoLabel.setBounds(centerX, 320, 800, 40);
        dineroTotalLabel.setBounds(centerX, 380, 800, 40);
        diaLabeltr.setBounds(centerX, 460, 800, 40);

        bienLabel.setVisible(true);
        malLabel.setVisible(true);
        dineroGanadoLabel.setVisible(true);
        dineroTotalLabel.setVisible(true);
        diaLabeltr.setVisible(true);

        botonContinuar.setVisible(true);
        repaint();
    }

    private void mostrarModoDerrota() {
        // Ocultar todos los elementos de la pantalla
        textArea.setVisible(false);
        botonOK.setVisible(false);
        botonX.setVisible(false);
        personajeLabel.setVisible(false);

        // Cambiar la imagen de fondo
        fondoreal = fondoMuerte;

        // Mostrar el mensaje "TE MATARON"
        mFinal.setVisible(true);

        // Mostrar el bot�n "Volver al Men�"
        botonVolver.setVisible(true);
    }

    
    // Mostrar mensaje final actualizado
    private void mostrarMensajeFinal() {
        timer.stop();
        

        // Actualizar el dinero
        int dineroGanado = (contadorBien * 1) - contadorMal;
        dinero += dineroGanado;

        // Verificar estado del juego
        if (contadorMal > 3) {
        	textArea.setVisible(false);
            scrollPane.setVisible(false);
            botonX.setVisible(false);
            botonOK.setVisible(false);
            botonInfo.setVisible(false);
            contadorLabel.setVisible(false);
            temporizadorLabel.setVisible(false);
            diaLabel.setVisible(false);
            personajeLabel.setVisible(false);
            mostrarModoDerrota(); // Llamar al modo derrota si hay m�s de 3 errores
        } else if (dia == 5) {
             regresarAlMenu();
        } else {
        	// Ocultar todos los componentes
            textArea.setVisible(false);
            scrollPane.setVisible(false);
            botonX.setVisible(false);
            botonOK.setVisible(false);
            botonInfo.setVisible(false);
            contadorLabel.setVisible(false);
            temporizadorLabel.setVisible(false);
            diaLabel.setVisible(false);
            personajeLabel.setVisible(false); // También ocultamos el personaje
            
            // Actualizar el panel
            getContentPane().validate();
            getContentPane().repaint();
            
            // Modo transicion
            mostrarModoTransicion();
        }
    }

    private void reiniciarDia() {
        // Detener el temporizador si está corriendo
        if (timer != null) {
            timer.stop();
        }

        // Reiniciar los valores del juego
        contadorBien = 0;
        contadorMal = 0;
        tiempoRestante = totaltiempo; // Reiniciar el tiempo
        dia++;
        diaLabel.setText("Dia: " + dia);

        // Limpiar el area de texto y contadores
        contadorLabel.setText("Bien: 0   Mal: 0");
        textArea.setText("");
        
        // Reiniciar valores por defecto
        rutaImagenPersonaje = "/imagen/IMAGENES Y GIF/pj/polimono.png";
        nombrePJ = "Oficial";
        apellidoPJ = "Banana";

     // Volver a cargar la imagen inicial con dimensiones ajustadas
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(rutaImagenPersonaje));
        Image imagenEscalada = originalIcon.getImage().getScaledInstance(personajeLabel.getWidth(),
                                                                         personajeLabel.getHeight(),
                                                                         Image.SCALE_SMOOTH);
        personajeLabel.setIcon(new ImageIcon(imagenEscalada));
        personajeLabel.setVisible(true); // Hacer visible nuevamente el personaje

        // Reiniciar la logica del juego
        mostrarTextosAleatorios();
        iniciarTemporizador(totaltiempo); // Reiniciar el temporizador

        // Restaurar visibilidad de los componentes
        textArea.setVisible(true);
        scrollPane.setVisible(true);
        botonX.setVisible(true);
        botonOK.setVisible(true);
        botonInfo.setVisible(true);
        contadorLabel.setVisible(true);
        temporizadorLabel.setVisible(true);
        diaLabel.setVisible(true);

        // Ocultar el botón "Continuar"
        botonContinuar.setVisible(false);
    }

    private void regresarAlMenu() {
        // Configurar la paleta de colores
        UIManager.put("OptionPane.background", new Color(0, 0, 0)); // Fondo negro
        UIManager.put("Panel.background", new Color(0, 0, 128)); // Azul oscuro
        UIManager.put("OptionPane.messageForeground", new Color(173, 216, 230)); // Azul claro para el texto

        // Configurar el botón
        UIManager.put("Button.background", new Color(224, 255, 255)); // Celeste crema
        UIManager.put("Button.foreground", Color.BLACK); // Texto negro en el botón

        // Crear un JOptionPane personalizado sin icono
        JOptionPane pane = new JOptionPane(
                "Regresando al menú...",
                JOptionPane.PLAIN_MESSAGE, // Tipo de mensaje sin icono
                JOptionPane.DEFAULT_OPTION
        );

        // Crear un diálogo con el JOptionPane
        JDialog dialog = pane.createDialog(this, "Mensaje");
        dialog.setVisible(true);


        // Ocultar la ventana actual y mostrar el menú
        this.setVisible(false); // Ocultar la ventana actual
        new Menu().setVisible(true); // Mostrar el menú
    }


    private Font cargarFuentePersonalizada(String ruta, float tamano) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(ruta));
            return font.deriveFont(tamano);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 20);  // Si no se carga la fuente personalizada, usar una por defecto
        }
    }

    private void cargarPersonajesDesdeArchivo(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" - ");
                if (partes.length == 3) {
                    listaPersonajes.add(partes[0].trim());  // Ruta de la imagen
                    listaNombresPJ.add(partes[1].trim());  // Nombre del personaje
                    listaApellidosPJ.add(partes[2].trim()); // Apellido del personaje
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarListasDesdeArchivo(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int listaActual = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.equals("---")) {
                    listaActual++;
                } else {
                    switch (listaActual) {
                        case 0:
                            lista1.add(linea);
                            break;
                        case 1:
                            lista2.add(linea);
                            break;
                        case 2:
                            lista3.add(linea);
                            break;
                        case 3:
                            lista4.add(linea);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarTextosAleatorios() {
        // Selección de textos con la lógica de "falsos"
        String textoAleatorio1 = seleccionarTextoConRestriccion(lista1);
        String textoAleatorio2 = seleccionarTextoConRestriccion(lista2);
        String textoAleatorio3 = seleccionarTextoConRestriccion(lista3);
        String textoAleatorio4 = seleccionarTextoConRestriccion(lista4);

        // Mostrar los textos seleccionados en el área de texto, incluyendo nombrePJ y apellidoPJ
        String textoPersonaje = "Nombre: " + nombrePJ + "\n" + "Apellido: " + apellidoPJ + "\n";

        // Mostrar los textos seleccionados en el área de texto
        textArea.setText(textoPersonaje +
                         "Ciudad: " + textoAleatorio1 + "\n" +
                         "Provincia: " + textoAleatorio2 + "\n" + 
                         "Banana favorita: " + textoAleatorio3 + "\n" + 
                         "Equipo favorito: " + textoAleatorio4);
    }

    private String seleccionarTextoConRestriccion(ArrayList<String> lista) {
        // Generar un número aleatorio entre 1 y 100 para determinar si debe ser falso o no
        int probabilidad = random.nextInt(100) + 1;
        if (probabilidad <= 10) { // 10% de probabilidad para seleccionar un falso
            return seleccionarFalso(lista);
        } else {
            return seleccionarNoFalso(lista);
        }
    }

    private String seleccionarFalso(ArrayList<String> lista) {
        // Seleccionar un valor "falso" de los índices 7, 8, 9
        int indiceFalso = random.nextInt(3) + 7; // Aleatorio entre 7 y 9
        return lista.get(indiceFalso).split(":")[0]; // Solo el texto antes del ":"
    }

    private String seleccionarNoFalso(ArrayList<String> lista) {
        // Crear una lista de índices no falsos (0-6 y >=10)
        ArrayList<Integer> indicesNoFalsos = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if (i < 7 || i >= 10) {
                indicesNoFalsos.add(i);
            }
        }

        // Seleccionar aleatoriamente uno de los índices no falsos
        int indiceNoFalso = indicesNoFalsos.get(random.nextInt(indicesNoFalsos.size()));
        return lista.get(indiceNoFalso).split(":")[0]; // Solo el texto antes del ":"
    }

    private void actualizarContadores() {
        contadorLabel.setText("Bien: " + contadorBien + "   Mal: " + contadorMal);
    }

    private boolean esFalsoEnPantalla() {
        try {
            // Obtener los textos visibles en el área de texto
            String[] textosVisibles = textArea.getText().split("\n");

            // Filtrar solo los textos relevantes
            String[] textosRelevantes = new String[4];
            textosRelevantes[0] = textosVisibles[2].replace("Ciudad: ", "").trim();       // Línea 3 (índice 2)
            textosRelevantes[1] = textosVisibles[3].replace("Lista 2: ", "").trim();     // Línea 4 (índice 3)
            textosRelevantes[2] = textosVisibles[4].replace("Banana favorita: ", "").trim(); // Línea 5 (índice 4)
            textosRelevantes[3] = textosVisibles[5].replace("Lista 4: ", "").trim();     // Línea 6 (índice 5)

            // Verificar si alguno de esos textos corresponde a un "falso" en las listas
            return contieneTextoFalso(lista1, textosRelevantes) ||
                   contieneTextoFalso(lista2, textosRelevantes) ||
                   contieneTextoFalso(lista3, textosRelevantes) ||
                   contieneTextoFalso(lista4, textosRelevantes);
        } catch (IndexOutOfBoundsException e) {
            // Si los índices no coinciden con los textos esperados
            e.printStackTrace();
            return false;
        }
    }

    private boolean contieneTextoFalso(ArrayList<String> lista, String[] ultimosTextos) {
        // Verificar si la lista tiene al menos 10 elementos
        if (lista.size() < 10) {
            return false;
        }

        // Obtener los textos "falsos" de los índices 7, 8 y 9
        String texto7 = lista.get(7).split(":")[0];
        String texto8 = lista.get(8).split(":")[0];
        String texto9 = lista.get(9).split(":")[0];

        // Imprimir los textos "falsos" que estamos buscando
        System.out.println("Buscando falsos: " + texto7 + ", " + texto8 + ", " + texto9);

        // Verificar si alguno de esos textos está en los últimos 4 renglones
        for (String textoVisible : ultimosTextos) {
            if (textoVisible.equals(texto7) || textoVisible.equals(texto8) || textoVisible.equals(texto9)) {
                return true;  // Se encontró un "falso"
            }
        }
        return false;  // No se encontró "falso"
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

    private void verificarMinijuego() {
        if (dia == 1) {
            return;
        }

        int numeroAleatorio = random.nextInt(100) + 1; // Aleatorio entre 1 y 100

        if (dia == 2 && numeroAleatorio <= 20) {

            new prueba.minijuegos.botones().setVisible(true);
        } else if (dia == 3) {
            // D�a 3: 1/10 probabilidad de "reaccion" y 1/10 de "botones"
            if (numeroAleatorio <= 10) {
                new prueba.minijuegos.reaccion().setVisible(true);
            } else if (numeroAleatorio <= 20) {
                new prueba.minijuegos.botones().setVisible(true);
            }
        } else if (dia == 4) {
            // D�a 4: 5/100 de "reaccion", 5/100 de "botones", 10/100 de "atrapa"
            if (numeroAleatorio <= 5) {
                new prueba.minijuegos.reaccion().setVisible(true);
            } else if (numeroAleatorio <= 10) {
                new prueba.minijuegos.botones().setVisible(true);
            } else if (numeroAleatorio <= 20) {
                new prueba.minijuegos.atrapa().setVisible(true);
            }
        } else if (dia == 5) {
            // D�a 5: 1/10 probabilidad para cada minijuego
            if (numeroAleatorio <= 10) {
                new prueba.minijuegos.reaccion().setVisible(true);
            } else if (numeroAleatorio <= 20) {
                new prueba.minijuegos.botones().setVisible(true);
            } else if (numeroAleatorio <= 30) {
                new prueba.minijuegos.atrapa().setVisible(true);
            }
        }
    }



    private void iniciarMinijuego(JDialog minijuego) {
        // Mostrar el minijuego como modal
        minijuego.setLocationRelativeTo(this); // Centrar respecto a la ventana principal
        minijuego.setVisible(true); // Mostrar el diálogo modal
    }

    public static void main(String[] args) {
        juego ventana = new juego();
        ventana.setVisible(true);
    }
}
