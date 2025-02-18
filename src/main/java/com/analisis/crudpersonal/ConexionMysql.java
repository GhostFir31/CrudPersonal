package com.analisis.crudpersonal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConexionMysql {

    private Connection conexion;

    public ConexionMysql() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarioscrud", "root", "root");
        } catch (Exception e) {
            System.err.println("Error de conexion: " + e);
        }
    }

    public Connection getConexion(){
        return conexion;
    }

    public void mostrarPersonasConTelefonos() {
        try {
            String sql = "SELECT p.id, p.nombre, p.direccion, t.telefono FROM personas p " +
                    "LEFT JOIN telefonos t ON p.id = t.persona_id";
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println("Listado de personas con sus telefonos:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Nombre: " + rs.getString("nombre") +
                        " | Direccion: " + rs.getString("direccion") +
                        " | Telefono: " + rs.getString("telefono"));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener datos: " + e.getMessage());
        }
    }

    public void editarPersona(int id, String nuevoNombre, String nuevaDireccion, String nuevoTelefono) {
        try {
            // Actualizar datos de la persona
            String sqlPersona = "UPDATE personas SET nombre = ?, direccion = ? WHERE id = ?";
            PreparedStatement psPersona = conexion.prepareStatement(sqlPersona);
            psPersona.setString(1, nuevoNombre);
            psPersona.setString(2, nuevaDireccion);
            psPersona.setInt(3, id);
            psPersona.executeUpdate();

            // Actualizar el numero telefonico (si existe)
            String sqlTelefono = "UPDATE telefonos SET telefono = ? WHERE persona_id = ?";
            PreparedStatement psTelefono = conexion.prepareStatement(sqlTelefono);
            psTelefono.setString(1, nuevoTelefono);
            psTelefono.setInt(2, id);
            psTelefono.executeUpdate();

            System.out.println("Persona actualizada con exito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar persona: " + e.getMessage());
        }
    }

    public void agregarPersona(String nombre, String direccion, String telefono) {
        try {
            // Insertar en personas
            String sqlPersona = "INSERT INTO personas (nombre, direccion) VALUES (?, ?)";
            PreparedStatement psPersona = conexion.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psPersona.setString(1, nombre);
            psPersona.setString(2, direccion);
            psPersona.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = psPersona.getGeneratedKeys();
            int idPersona = 0;
            if (rs.next()) {
                idPersona = rs.getInt(1);
            }

            // Insertar el numero en telefonos
            String sqlTelefono = "INSERT INTO telefonos (persona_id, telefono) VALUES (?, ?)";
            PreparedStatement psTelefono = conexion.prepareStatement(sqlTelefono);
            psTelefono.setInt(1, idPersona);
            psTelefono.setString(2, telefono);
            psTelefono.executeUpdate();

            System.out.println("Persona agregada con exito.");
        } catch (Exception e) {
            System.err.println("Error al agregar persona: " + e.getMessage());
        }
    }

    public void eliminarPersona(int id) {
        try {

            String sqlEliminar = "DELETE FROM personas WHERE id = ?";

            PreparedStatement psEliminar = conexion.prepareStatement(sqlEliminar);
            psEliminar.setInt(1, id);
            int filasAfectadas = psEliminar.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Persona eliminada correctamente.");
            } else {
                System.out.println("⚠️ No se encontró ninguna persona con ese ID.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar persona: " + e.getMessage());
        }
    }

    public List<Vehiculo> obtenerVehiculos() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String query = "SELECT id, nombre, tipo FROM vehiculo";

        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");

                // Creamos el objeto Vehículo con los datos obtenidos
                Vehiculo vehiculo = new Vehiculo(id, nombre, tipo);
                vehiculos.add(vehiculo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener los vehículos: " + e.getMessage());
        }

        return vehiculos;
    }
    public void asociarVehiculoConPersona(int personaId, int vehiculoId) {
        String query = "INSERT INTO persona_vehiculo (persona_id, vehiculo_id) VALUES (?, ?)";
        try (Connection con = getConexion();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, vehiculoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Vehiculo> obtenerVehiculosDePersona(int personaId) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String query = "SELECT v.id, v.nombre, v.tipo " +
                "FROM vehiculo v " +
                "JOIN persona_vehiculo pv ON v.id = pv.vehiculo_id " +
                "WHERE pv.persona_id = ?";
        try (Connection con = getConexion();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, personaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehiculos.add(new Vehiculo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    public void desasociarVehiculoDePersona(int personaId, int vehiculoId) {
        String query = "DELETE FROM persona_vehiculo WHERE persona_id = ? AND vehiculo_id = ?";
        try (Connection con = getConexion();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, vehiculoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        String query = "INSERT INTO vehiculo (nombre, tipo) VALUES (?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            // Configuramos el PreparedStatement para devolver las claves generadas automáticamente
            preparedStatement = conexion.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, vehiculo.getNombre());
            preparedStatement.setString(2, vehiculo.getTipo());

            // Ejecutamos la consulta
            int filasInsertadas = preparedStatement.executeUpdate();

            if (filasInsertadas > 0) {
                System.out.println("Vehículo agregado exitosamente a la base de datos.");

                // Obtenemos el ID generado automáticamente
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    vehiculo.setId(idGenerado); // Actualizamos el objeto Vehiculo con su ID
                    System.out.println("ID generado: " + idGenerado);
                }
            } else {
                System.out.println("No se pudo agregar el vehículo a la base de datos.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al agregar el vehículo: " + e.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Persona obtenerPersonaPorId(int personaId) {
        Persona persona = null;

        // Define the query to fetch person details and associated phone numbers
        String consultaSQL = "SELECT p.id, p.nombre, p.direccion, t.telefono " +
                "FROM personas p " +
                "LEFT JOIN telefonos t ON p.id = t.persona_id " +
                "WHERE p.id = ?";

        try (PreparedStatement preparedStatement = conexion.prepareStatement(consultaSQL)) {
            preparedStatement.setInt(1, personaId);
            ResultSet resultado = preparedStatement.executeQuery();

            int id = 0;
            String nombre = null, direccion = null, telefono = null;

            while (resultado.next()) {
                id = resultado.getInt("id");
                nombre = resultado.getString("nombre");
                direccion = resultado.getString("direccion");
                telefono = resultado.getString("telefono");

                // Exit the loop after processing the first phone
                // or override this logic as needed
                break;
            }

            // Only create the persona object if we got a result
            if (nombre != null && direccion != null) {
                persona = new Persona(id, nombre, direccion, telefono);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener la persona con ID: " + personaId);
        }

        return persona;
    }
}
