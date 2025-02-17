package com.analisis.crudpersonal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class VentanaAgregarController {

    @FXML
    private TextField nombreTXT;

    @FXML
    private TextField direccionTXT;

    @FXML
    private TextField telefonoTXT;

    @FXML
    private Button botonAgregar;

    @FXML
    private void initialize() {
        botonAgregar.setOnAction(event -> agregarPersona());
    }

    @FXML
    private void agregarPersona() {
        // Obtener los datos de los campos de texto
        String nombre = nombreTXT.getText().trim();
        String direccion = direccionTXT.getText().trim();
        String telefono = telefonoTXT.getText().trim();

        // Validación básica para asegurarse de que los campos no estén vacíos
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        // Crear una instancia de la clase ConexionMysql e intentar agregar la persona
        ConexionMysql con = new ConexionMysql();
        con.agregarPersona(nombre, direccion, telefono);

        botonAgregar.getScene().getWindow().hide();

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