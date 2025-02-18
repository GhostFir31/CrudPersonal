package com.analisis.crudpersonal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class VentanaEditarController {

    @FXML
    private TextField nombreTXT;

    @FXML
    private TextField direccionTXT;

    @FXML
    private TextField telefonoTXT;

    @FXML
    private Button botonEditar;

    private Persona persona; // Variable para almacenar la persona a editar

    // Método para inicializar el controlador con una Persona específica
    public void inicializarDatos(Persona persona) {
        this.persona = persona;
        nombreTXT.setText(persona.getNombre());
        direccionTXT.setText(persona.getDireccion());
        telefonoTXT.setText(persona.getTelefono());
    }

    @FXML
    private void initialize() {

        botonEditar.setOnAction(event -> editarPersona());
    }

    @FXML
    private void editarPersona() {
        if (persona != null) {
            // Obtener los valores del formulario
            String nombre = nombreTXT.getText();
            String direccion = direccionTXT.getText();
            String telefono = telefonoTXT.getText();

            // Crear instancia de la clase de conexión
            ConexionMysql con = new ConexionMysql();

            con.editarPersona(persona.getId(), nombre, direccion, telefono);

            // Opcional: Limpiar los campos
            limpiarCampos();
        }

        // Cerrar esta ventana (la ventana de edición)
        botonEditar.getScene().getWindow().hide();

    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Información");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        nombreTXT.clear();
        direccionTXT.clear();
        telefonoTXT.clear();
    }
}