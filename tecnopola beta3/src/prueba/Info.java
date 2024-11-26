package prueba;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class Info extends JFrame {
    private JTextArea textArea;
    private JButton botonAnterior;
    private JButton botonSiguiente;
    private int paginaActual = 0; // �ndice de la p�gina actual (0 para lista1, 1 para lista2, etc.)
    private ArrayList<ArrayList<String>> listas; // Contenedor de todas las listas
    private ArrayList<String> nombresListas; // Contenedor de los nombres de las listas
    public Info(ArrayList<String> lista1, ArrayList<String> lista2, ArrayList<String> lista3, ArrayList<String> lista4) {
        setTitle("Libro de Informacion");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    Font fuentePersonalizada = cargarFuentePersonalizada("recursos/fonts/Pixelon-OGALo.ttf", 24f);

        // Crear el �rea de texto y habilitar ajuste de l�nea
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(fuentePersonalizada.deriveFont(22f));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Contenedor de todas las listas para acceder a ellas por �ndice
        listas = new ArrayList<>();
        listas.add(lista1);
        listas.add(lista2);
        listas.add(lista3);
        listas.add(lista4);
        
     // Crear lista para los nombres de las listas
        nombresListas = new ArrayList<>();
        nombresListas.add("Provincias");
        nombresListas.add("Ciudades");
        nombresListas.add("Tipos de bananas");
        nombresListas.add("Lista 4");

        // Panel de botones para cambiar de p�gina
        JPanel panelBotones = new JPanel();
        botonAnterior = new JButton("Anterior");
        botonSiguiente = new JButton("Siguiente");
        panelBotones.add(botonAnterior);
        panelBotones.add(botonSiguiente);

        // Eventos para los botones de navegaci�n
        botonAnterior.addActionListener(e -> cambiarPagina(-1));
        botonSiguiente.addActionListener(e -> cambiarPagina(1));

        // Mostrar la primera p�gina al inicio
        mostrarPagina();

        // Agregar el �rea de texto en un JScrollPane y los botones en la parte inferior
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // M�todo para cambiar de p�gina
    private void cambiarPagina(int direccion) {
        int totalPaginas = listas.size();
        paginaActual = (paginaActual + direccion + totalPaginas) % totalPaginas;
        mostrarPagina();
    }

    // M�todo para mostrar el contenido de la p�gina actual (lista correspondiente)
    private void mostrarPagina() {
        textArea.setText(""); // Limpiar el �rea de texto
        ArrayList<String> lista = listas.get(paginaActual);
        textArea.append(nombresListas.get(paginaActual) + ":\n\n");

        int limite = Math.min(7, lista.size());
        for (int i = 0; i < limite; i++) {
            textArea.append(" - " + lista.get(i) + "\n\n");
        }
    }
    
    private Font cargarFuentePersonalizada(String rutaFuente, float tamano) {
        try {
            // Cargar la fuente desde el archivo
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, new File(rutaFuente));
            return fuente.deriveFont(tamano); // Ajustar tamano
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int)tamano); // Fuente por defecto si falla
        }
    }

}

