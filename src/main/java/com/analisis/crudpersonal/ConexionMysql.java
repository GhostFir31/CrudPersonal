package com.analisis.crudpersonal;

import java.sql.*;
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

    // 1️⃣ Mostrar todas las personas con sus numeros telefonicos
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

    // 2️⃣ Editar una persona y su numero telefonico
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

    // 3️⃣ Agregar una persona con su numero telefonico
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
            // 1️⃣ Eliminar la persona (los teléfonos se eliminan en cascada por ON DELETE CASCADE)
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

    public static void main(String[] args) {


        ConexionMysql con = new ConexionMysql();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📌 Menu:");
            System.out.println("1. Mostrar personas con telefonos");
            System.out.println("2. Agregar nueva persona");
            System.out.println("3. Editar una persona");
            System.out.println("4. Eliminar una persona");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opcion: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    con.mostrarPersonasConTelefonos();
                    break;
                case 2:
                    System.out.print("Ingrese el nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Ingrese la direccion: ");
                    String direccion = scanner.nextLine();
                    System.out.print("Ingrese el numero telefonico: ");
                    String telefono = scanner.nextLine();
                    con.agregarPersona(nombre, direccion, telefono);
                    break;
                case 3:
                    System.out.print("Ingrese el ID de la persona a editar: ");
                    int idEditar = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nuevo nombre: ");
                    String nuevoNombre = scanner.nextLine();
                    System.out.print("Nueva direccion: ");
                    String nuevaDireccion = scanner.nextLine();
                    System.out.print("Nuevo numero telefonico: ");
                    String nuevoTelefono = scanner.nextLine();
                    con.editarPersona(idEditar, nuevoNombre, nuevaDireccion, nuevoTelefono);
                    break;
                case 4:
                    System.out.print("Ingrese el ID de la persona a eliminar: ");
                    int idEliminar = scanner.nextInt();
                    con.eliminarPersona(idEliminar);
                    break;
                case 5:
                    System.out.println("Saliendo del programa...");
                    scanner.close();
                    return;
                default:
                    System.out.println("⚠️ Opcion no valida.");
            }
        }
    }
}
