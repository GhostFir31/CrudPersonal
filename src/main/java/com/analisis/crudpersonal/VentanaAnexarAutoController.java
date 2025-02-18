package com.analisis.crudpersonal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.List;

public class VentanaAnexarAutoController {
    private Persona personaActual; // Persona seleccionada
    @FXML
    private ListView<String> listaVehiculosTotal;

    @FXML
    private Button botonAgregarVehiculoPersona;

    @FXML
    private void initialize() {
        // Inicializamos la lista con los vehículos de la base de datos
        cargarVehiculosEnLista();
        // Configuramos el botón para anexar el vehículo a una persona
        botonAgregarVehiculoPersona.setOnAction(event -> agregarVehiculoAPersona());

    }
    public void inicializarPersona(Persona persona) {
        this.personaActual = persona;
    }
    private void cargarVehiculosEnLista() {
        ConexionMysql conexion = new ConexionMysql();

        // Obtenemos todos los vehículos de la base de datos
        List<Vehiculo> vehiculos = conexion.obtenerVehiculos();

        // Limpiamos la lista antes de llenarla
        listaVehiculosTotal.getItems().clear();

        // Agregamos los vehículos a la lista
        if (vehiculos.isEmpty()) {
            listaVehiculosTotal.getItems().add("No se encontraron vehículos.");
        } else {
            for (Vehiculo vehiculo : vehiculos) {
                listaVehiculosTotal.getItems().add(
                        "ID: " + vehiculo.getId() + ", Marca: " + vehiculo.getNombre() + ", Tipo: " + vehiculo.getTipo()
                );
            }
        }
    }

    private void agregarVehiculoAPersona() {
        // Verificar si se seleccionó un vehículo de la lista
        String vehiculoSeleccionado = listaVehiculosTotal.getSelectionModel().getSelectedItem();
        if (vehiculoSeleccionado == null || vehiculoSeleccionado.equals("No se encontraron vehículos.")) {
            mostrarAlerta("Error", "Debe seleccionar un vehículo para asociarlo.", Alert.AlertType.ERROR);
            return;
        }
        // Extraer el ID del vehículo del texto seleccionado
        int vehiculoId = extraerIdVehiculo(vehiculoSeleccionado);
        if (vehiculoId == -1) {
            mostrarAlerta("Error", "No se pudo determinar el ID del vehículo seleccionado.", Alert.AlertType.ERROR);
            return;
        }

        // Aquí deberías obtener el ID de la persona correspondiente
        int personaId = obtenerIdPersona();

        if (personaId == -1) {
            mostrarAlerta("Error", "No se pudo determinar el ID de la persona asociada.", Alert.AlertType.ERROR);
            return;
        }

        // Asociar el vehículo con la persona en la base de datos
        ConexionMysql conexion = new ConexionMysql();
        conexion.asociarVehiculoConPersona(personaId, vehiculoId);

        // Mostrar un mensaje de confirmación
        mostrarAlerta("Éxito", "El vehículo ha sido asociado exitosamente a la persona.", Alert.AlertType.INFORMATION);

        // Cerrar la ventana actual
        cerrarVentana();
    }

    private void cerrarVentana() {
        // Obtener la ventana (stage) actual y cerrarla
        botonAgregarVehiculoPersona.getScene().getWindow().hide();
    }

    private int obtenerIdPersona() {
        return personaActual.getId();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private int extraerIdVehiculo(String vehiculoSeleccionado) {
        try {
            // El ID debe estar después del texto "ID: " y antes de la coma
            String[] partes = vehiculoSeleccionado.split(",");
            String idParte = partes[0]; // "ID: <numero>"
            return Integer.parseInt(idParte.split(":")[1].trim());
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Retornamos -1 si hay algún error
        }
    }

}