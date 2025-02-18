package com.analisis.crudpersonal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VehiculosUsuarioController {
    private Persona persona;
    @FXML
    private Button botonRegistrar;

    @FXML
    private Button botonAgregarVehiculo;

    @FXML
    private ListView<String> listaVehiculos;


    @FXML
    private void initialize() {

        configurarEventos();

    }

    private void configurarEventos() {
        // Asociar eventos a los botones
        botonRegistrar.setOnAction(event -> registrarVehiculo());
        botonAgregarVehiculo.setOnAction(event -> agregarVehiculoAUsuario());
    }

    private void registrarVehiculo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VentanaRegistrarVehiculo.fxml"));
            Parent root = fxmlLoader.load();

            Stage ventanaRegistrar = new Stage();
            ventanaRegistrar.setTitle("Registrar Vehículo");
            ventanaRegistrar.setScene(new Scene(root));
            ventanaRegistrar.initModality(Modality.APPLICATION_MODAL);

            // Escuchar el cierre de la ventana
            ventanaRegistrar.setOnHiding(event -> refrescarListaVehiculos());

            ventanaRegistrar.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la ventana de Registrar Vehículo: " + e.getMessage());
        }
    }

    private void agregarVehiculoAUsuario() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ventanaAnexarAuto.fxml"));
            Parent root = fxmlLoader.load();

            VentanaAnexarAutoController controlador = fxmlLoader.getController();
            controlador.inicializarPersona(persona);

            Stage ventanaAnexar = new Stage();
            ventanaAnexar.setTitle("Anexar Vehículo");
            ventanaAnexar.setScene(new Scene(root));
            ventanaAnexar.initModality(Modality.APPLICATION_MODAL);

            // Escuchar el cierre de la ventana
            ventanaAnexar.setOnHiding(event -> refrescarListaVehiculos());

            ventanaAnexar.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la ventana de Anexar Vehículo: " + e.getMessage());
        }
    }


    private void cargarVehiculosDePersona(int personaId) {
        listaVehiculos.getItems().clear();
        ConexionMysql conexion = new ConexionMysql();
        List<Vehiculo> vehiculos = conexion.obtenerVehiculosDePersona(personaId);
        for (Vehiculo vehiculo : vehiculos) {
            listaVehiculos.getItems().add(vehiculo.getNombre() + " (" + vehiculo.getTipo() + ")");
        }
    }

    public void inicializarDatosPersona(Persona persona) {
        this.persona = persona; // Guardar la referencia de la persona
        cargarVehiculosDePersona(persona.getId());
    }

    private void refrescarListaVehiculos() {
        if (persona != null) {
            cargarVehiculosDePersona(persona.getId());
        }
    }
    public void setPersona(Persona persona) {
        this.persona = persona; // Guardar la persona seleccionada
    }
}
