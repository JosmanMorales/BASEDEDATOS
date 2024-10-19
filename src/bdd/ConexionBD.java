package bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/BDNegocio";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Método para conectar a la base de datos
    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conexion;
    }

    // Método para insertar un producto
    public static void insertarProducto(String codigo, String nombre, double precio, int cantidad, String fecha) {
        Connection con = ConexionBD.conectar();
        if (con == null) {
            System.out.println("No se pudo realizar la inserción debido a problemas con la conexión a la base de datos.");
            return;
        }

        // Validación de la fecha
        java.sql.Date fechaSQL = validarFecha(fecha);
        if (fechaSQL == null) {
            System.out.println("Fecha no válida. Debe estar en formato YYYY-MM-DD.");
            return;
        }

        String query = "INSERT INTO producto (codigoProducto, nombreProducto, precioUnitario, cantidadProducto, fechaVencimiento) VALUES (?,?,?,?,?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigo);
            pst.setString(2, nombre);
            pst.setDouble(3, precio);
            pst.setInt(4, cantidad);
            pst.setDate(5, fechaSQL);
            pst.executeUpdate();
            System.out.println("Producto insertado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
        }
    }

    // Método para listar productos
    public static void listarProductos() {
        String query = "SELECT * FROM producto";
        try (Connection con = ConexionBD.conectar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                System.out.println("Código: " + rs.getString("codigoProducto"));
                System.out.println("Nombre: " + rs.getString("nombreProducto"));
                System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                System.out.println("Fecha de Vencimiento: " + rs.getDate("fechaVencimiento"));
                System.out.println("");
            }
            if (!hayResultados) {
                System.out.println("No hay productos disponibles.");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
    }

    // Método para buscar un producto
    public static void buscarProducto(String codigoProducto) {
        String query = "SELECT * FROM producto WHERE codigoProducto = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("Código: " + rs.getString("codigoProducto"));
                System.out.println("Nombre: " + rs.getString("nombreProducto"));
                System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                System.out.println("Fecha de Vencimiento: " + rs.getDate("fechaVencimiento"));
            } else {
                System.out.println("Producto no encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
    }

    // Método para actualizar un producto
    public static void actualizarProducto(String codigoProducto, String nombre, double precio, String fecha) {
        Connection con = ConexionBD.conectar();
        if (con == null) {
            System.out.println("No se pudo realizar la actualización debido a problemas con la conexión a la base de datos.");
            return;
        }

        // Validación de la fecha
        java.sql.Date fechaSQL = validarFecha(fecha);
        if (fechaSQL == null) {
            System.out.println("Fecha no válida. Debe estar en formato YYYY-MM-DD.");
            return;
        }

        String query = "UPDATE producto SET nombreProducto = ?, precioUnitario = ?, fechaVencimiento = ? WHERE codigoProducto = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, nombre);
            pst.setDouble(2, precio);
            pst.setDate(3, fechaSQL);
            pst.setString(4, codigoProducto);
            pst.executeUpdate();
            System.out.println("Producto actualizado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    // Método para eliminar un producto
    public static void eliminarProducto(String codigoProducto) {
        String query = "DELETE FROM producto WHERE codigoProducto = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            pst.executeUpdate();
            System.out.println("Producto eliminado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    // Método para validar la fecha
    public static java.sql.Date validarFecha(String fecha) {
        try {
            LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE);
            return java.sql.Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Menú principal
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Insertar producto");
            System.out.println("2. Mostrar productos");
            System.out.println("3. Buscar producto por código");
            System.out.println("4. Modificar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar el buffer de entrada

            switch (opcion) {
                case 1:
                    System.out.print("Introduce el código del producto: ");
                    String codigo = sc.nextLine();
                    System.out.print("Introduce el nombre del producto: ");
                    String nombre = sc.nextLine();
                    System.out.print("Introduce el precio del producto: ");
                    double precio = sc.nextDouble();
                    System.out.print("Introduce la cantidad del producto: ");
                    int cantidad = sc.nextInt();
                    sc.nextLine(); // Limpiar el buffer de entrada
                    System.out.print("Introduce la fecha de vencimiento (YYYY-MM-DD): ");
                    String fecha = sc.nextLine();
                    insertarProducto(codigo, nombre, precio, cantidad, fecha);
                    break;
                case 2:
                    listarProductos();
                    break;
                case 3:
                    System.out.print("Introduce el código del producto a buscar: ");
                    String codigoBuscar = sc.nextLine();
                    buscarProducto(codigoBuscar);
                    break;
                case 4:
                    System.out.print("Introduce el código del producto a modificar: ");
                    String codigoModificar = sc.nextLine();
                    System.out.print("Introduce el nuevo nombre del producto: ");
                    String nuevoNombre = sc.nextLine();
                    System.out.print("Introduce el nuevo precio del producto: ");
                    double nuevoPrecio = sc.nextDouble();
                    sc.nextLine(); // Limpiar el buffer de entrada
                    System.out.print("Introduce la nueva fecha de vencimiento (YYYY-MM-DD): ");
                    String nuevaFecha = sc.nextLine();
                    actualizarProducto(codigoModificar, nuevoNombre, nuevoPrecio, nuevaFecha);
                    break;
                case 5:
                    System.out.print("Introduce el código del producto a eliminar: ");
                    String codigoEliminar = sc.nextLine();
                    eliminarProducto(codigoEliminar);
                    break;
                case 6:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida, intenta nuevamente.");
                    break;
            }
        } while (opcion != 6);

        sc.close();
    }
}
