package com.analisis.crudpersonal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class VentanaRegistrarVehiculoController {

    @FXML
    private TextField marcaTXT;

    @FXML
    private TextField tipoTXT;

    @FXML
    private Button botonRegistrar;

    @FXML
    private void initialize() {
        botonRegistrar.setOnAction(event -> registrarAutomovil());
    }

    @FXML
    private void registrarAutomovil() {
        // Obtener los datos de los campos de texto
        String marca = marcaTXT.getText().trim();
        String tipo = tipoTXT.getText().trim();

        // Validación básica para asegurarse de que los campos no estén vacíos
        if (marca.isEmpty() || tipo.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        Vehiculo auto = new Vehiculo(marca,tipo);
        // Crear una instancia de la clase ConexionMysql e intentar agregar la persona
        ConexionMysql con = new ConexionMysql();
        con.agregarVehiculo(auto);

        botonRegistrar.getScene().getWindow().hide();

    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Información");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        marcaTXT.clear();
        tipoTXT.clear();

    }
}