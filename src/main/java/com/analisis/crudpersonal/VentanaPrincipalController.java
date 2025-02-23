package com.analisis.crudpersonal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class VentanaPrincipalController {

    @FXML
    private ListView<String> listaPersonas;

    @FXML
    private Button botonAgregar; // Referencia al botón "Agregar Persona"
    @FXML
    private Button botonEditar; // Referencia al botón "Editar Persona"
    @FXML
    private Button botonEliminar; // Referencia al botón "Eliminar Persona"

    @FXML
    private Button botonVerVehiculos;


    private ConexionMysql conexionMysql;
    private ObservableList<String> listaObservable;

    public VentanaPrincipalController() {
        this.conexionMysql = new ConexionMysql();
        this.listaObservable = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        cargarDatosEnLista();
        // Inicializar acciones para los botones
        botonAgregar.setOnAction(event -> {
            agregarPersona();
        });

        botonEditar.setOnAction(event -> {
            editarPersona();
        });

        botonEliminar.setOnAction(event -> {
            eliminarPersona();
        });
      botonVerVehiculos.setOnAction(event -> {
            verVehiculos();
        });
    }

    public void cargarDatosEnLista() {
        listaObservable.clear();
        try {
            String sql = "SELECT p.id, p.nombre, p.direccion, t.telefono FROM personas p " +
                    "LEFT JOIN telefonos t ON p.id = t.persona_id";

            Connection con = conexionMysql.getConexion();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String datos = "ID: " + rs.getInt("id") +
                        " | Nombre: " + rs.getString("nombre") +
                        " | Direccion: " + rs.getString("direccion") +
                        " | Telefono: " + rs.getString("telefono");
                listaObservable.add(datos);
            }

            listaPersonas.setItems(listaObservable);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }

    private void agregarPersona() {
        try {
            // Cargar el archivo FXML de la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ventanaAgregar.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            Stage stage = new Stage();
            stage.setTitle("Agregar Persona");
            stage.setScene(new Scene(root));

            // Mostrar la ventana y esperar que se cierre antes de continuar
            stage.showAndWait();

            // Recargar la lista de personas después de agregar una nueva persona
            cargarDatosEnLista();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al abrir la ventana de Agregar Persona: " + e.getMessage());
        }
    }

    private void editarPersona() {
        // Asegurarnos de que hay algo seleccionado en la lista
        int indiceSeleccionado = listaPersonas.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado != -1) {
            // Obtener el texto del elemento seleccionado en la vista de lista
            String seleccionado = listaPersonas.getSelectionModel().getSelectedItem();

            // Separar los valores por "|"
            String[] datos = seleccionado.split("\\|");

            // Extraer y limpiar los datos eliminando etiquetas
            String idStr = datos[0].trim().replace("ID:", "").trim(); // Por ejemplo: "1"
            int id = Integer.parseInt(idStr); // Convertir "1" a entero
            String nombre = datos[1].trim().replace("Nombre:", "").trim();  // Extraer Nombre
            String direccion = datos[2].trim().replace("Direccion:", "").trim(); // Extraer Dirección
            String telefono = datos[3].trim().replace("Telefono:", "").trim();  // Extraer Teléfono

            // Crear un objeto Persona con los datos extraídos
            Persona persona = new Persona(id, nombre, direccion, telefono);

            // Cargar el archivo FXML de la ventana de edición
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ventanaEditar.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Obtener el controlador de la ventana de edición
            VentanaEditarController controlador = loader.getController();

            // Pasar el objeto Persona al controlador de la ventana de edición
            controlador.inicializarDatos(persona);

            // Crear una nueva ventana para mostrar la edición
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Persona");
            stage.show();

            // Actualizar la lista en la ventana principal cuando se cierre la ventana de edición
            stage.setOnHiding(event -> cargarDatosEnLista());
        } else {
            // Mostrar una alerta si no hay nada seleccionado
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("⚠️ Por favor, selecciona una persona para editar.");
            alerta.showAndWait();
        }
    }

    private void eliminarPersona() {
        // Obtener el índice del elemento seleccionado en el ListView
        int indice = listaPersonas.getSelectionModel().getSelectedIndex();

        // Comprobar si hay un elemento seleccionado
        if (indice != -1) {
            // Obtener la persona seleccionada del ListView
            String personaSeleccionada = listaPersonas.getItems().get(indice);

            int idPersona = Integer.parseInt(personaSeleccionada.split("\\|")[0].replace("ID:", "").trim());

            // Crear instancia de la clase de conexión para interactuar con la base de datos
            ConexionMysql con = new ConexionMysql();

            // Llamar a eliminarPersona pasando el id de la persona
            con.eliminarPersona(idPersona);

            // Recargar la lista del ListView
            cargarDatosEnLista();
        } else {
            // Mostrar un mensaje si no se seleccionó nada
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("⚠️ Debes seleccionar una persona de la lista.");
            alerta.showAndWait();
        }
    }

    private void verVehiculos() {
        String personaSeleccionada = listaPersonas.getSelectionModel().getSelectedItem();

        if (personaSeleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una persona de la lista.", Alert.AlertType.ERROR);
            return;
        }


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaVehiculosUsuario.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasarle los datos iniciales
            VentanaVehiculosUsuarioController controlador = loader.getController();

            String seleccionado = listaPersonas.getSelectionModel().getSelectedItem();

            // Separar los valores (asumiendo formato: "ID: 1 | Nombre | Dirección | Teléfono")
            String[] datos = seleccionado.split("\\|");

            // Extraer y limpiar los datos
            String idStr = datos[0].trim().replace("ID:", "").trim(); // Por ejemplo: "1"
            int id = Integer.parseInt(idStr); // Convertir "1" a entero
            String nombre = datos[1].trim();  // Nombre
            String direccion = datos[2].trim(); // Dirección
            String telefono = datos[3].trim();  // Teléfono

            // Crear un objeto Persona con los datos extraídos
            Persona persona = new Persona(id, nombre, direccion, telefono);

            controlador.inicializarDatosPersona(persona);

            Stage stage = new Stage();
            stage.setTitle("Vehículos de " + nombre + " (ID: " + id + ")");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de Vehículos de Usuario.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}