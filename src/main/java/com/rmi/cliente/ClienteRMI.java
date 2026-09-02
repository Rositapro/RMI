package com.rmi.cliente;

import com.rmi.dto.OperacionDTO;
import com.rmi.dto.ProductoDTO;
import com.rmi.interfaz.IInventarioRemoto;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/**
 * Aplicación Cliente que consume el servicio RMI remoto.
 * Localiza el rmiregistry, recupera el stub remoto mediante lookup
 * e invoca métodos remotos pasando y recibiendo objetos serializables.
 */
public class ClienteRMI {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String NOMBRE_SERVICIO = "ServicioInventario";

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("               CLIENTE JAVA RMI DE INVENTARIO            ");
        System.out.println("=========================================================");

        configurarSeguridad();

        String host = (args.length > 0) ? args[0] : DEFAULT_HOST;
        int puerto = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        try {
            System.out.printf("[+] Conectando al Registro RMI en %s:%d...%n", host, puerto);
            Registry registro = LocateRegistry.getRegistry(host, puerto);

            System.out.printf("[+] Buscando el servicio remoto '%s' en el registro...%n", NOMBRE_SERVICIO);
            IInventarioRemoto servicioRemoto = (IInventarioRemoto) registro.lookup(NOMBRE_SERVICIO);

            System.out.println("[OK] ¡Conexión establecida con éxito con el objeto remoto!");
            System.out.println("---------------------------------------------------------");

            // Si se pasa el argumento "--menu", abrimos modo interactivo
            // De lo contrario, ejecutamos la demostración completa de todas las operaciones
            boolean modoMenu = false;
            for (String arg : args) {
                if ("--menu".equalsIgnoreCase(arg) || "-m".equalsIgnoreCase(arg)) {
                    modoMenu = true;
                    break;
                }
            }

            if (modoMenu) {
                ejecutarMenuInteractivo(servicioRemoto);
            } else {
                ejecutarDemostracionAutomatica(servicioRemoto);
            }

        } catch (Exception e) {
            System.err.println("[-] Error en la ejecución del cliente RMI:");
            e.printStackTrace();
        }
    }

    /**
     * Demostración automatizada de todas las capacidades requeridas:
     * 1. Consulta remota de colecciones de objetos serializables.
     * 2. Envío de un nuevo ProductoDTO serializado hacia el servidor.
     * 3. Consulta de un ProductoDTO por ID.
     * 4. Ejecución de transacciones de venta retornando OperacionDTO.
     * 5. Actualización de inventario retornando OperacionDTO.
     * 6. Cálculo remoto del valor total de inventario.
     */
    public static void ejecutarDemostracionAutomatica(IInventarioRemoto servicio) throws Exception {
        System.out.println("\n>>> [PASO 1] Consultando lista de productos iniciales en el servidor...");
        List<ProductoDTO> productos = servicio.listarProductos();
        imprimirListaProductos(productos);

        System.out.println("\n>>> [PASO 2] Enviando un nuevo objeto ProductoDTO serializado al servidor...");
        ProductoDTO nuevoProducto = new ProductoDTO(
                0, // ID 0 para que el servidor lo autoasigne
                "AUR-005",
                "Audífonos Sony WH-1000XM5",
                "Audio",
                349.99,
                18
        );
        System.out.println("  -> Objeto local a enviar: " + nuevoProducto);
        ProductoDTO productoRegistrado = servicio.agregarProducto(nuevoProducto);
        System.out.println("  <- Objeto recibido de vuelta tras registro: " + productoRegistrado);

        System.out.println("\n>>> [PASO 3] Consultando producto por ID remoto (ID: " + productoRegistrado.getId() + ")...");
        ProductoDTO consultado = servicio.obtenerProductoPorId(productoRegistrado.getId());
        System.out.println("  <- Objeto consultado: " + consultado);

        System.out.println("\n>>> [PASO 4] Realizando una operación remota de VENTA (3 unidades)...");
        OperacionDTO venta = servicio.registrarVenta(productoRegistrado.getId(), 3);
        System.out.println("  <- Resultado de la operación: " + venta);
        System.out.println("  <- Stock restante en objeto: " + venta.getProductoInvolucrado().getStock());

        System.out.println("\n>>> [PASO 5] Intentando una VENTA con stock insuficiente (prueba de validación)...");
        OperacionDTO ventaFallida = servicio.registrarVenta(productoRegistrado.getId(), 999);
        System.out.println("  <- Resultado esperado: " + ventaFallida);

        System.out.println("\n>>> [PASO 6] Actualizando stock remotamente con OperacionDTO...");
        OperacionDTO actualizacion = servicio.actualizarStock(productoRegistrado.getId(), 50);
        System.out.println("  <- Resultado de la actualización: " + actualizacion);

        System.out.println("\n>>> [PASO 7] Calculando el valor monetario total del inventario remoto...");
        double valorTotal = servicio.calcularValorTotalInventario();
        System.out.printf("  <- Valor total del inventario en el servidor: $%,.2f MXN%n", valorTotal);

        System.out.println("\n>>> [PASO 8] Lista final de productos en el servidor tras todas las operaciones:");
        imprimirListaProductos(servicio.listarProductos());

        System.out.println("\n=========================================================");
        System.out.println("  ¡DEMOSTRACIÓN RMI COMPLETADA CON ÉXITO SIN ERRORES!    ");
        System.out.println("  (Tip: Ejecuta con argumento '--menu' para modo manual) ");
        System.out.println("=========================================================");
    }

    /**
     * Modo interactivo para pruebas manuales libres del usuario/profesor.
     */
    private static void ejecutarMenuInteractivo(IInventarioRemoto servicio) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n========== MENÚ DE GESTIÓN RMI ==========");
            System.out.println("1. Listar todos los productos");
            System.out.println("2. Registrar nuevo producto (enviar DTO)");
            System.out.println("3. Buscar producto por ID");
            System.out.println("4. Registrar venta (recibir OperacionDTO)");
            System.out.println("5. Actualizar existencias de stock");
            System.out.println("6. Calcular valor total de inventario");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1":
                        imprimirListaProductos(servicio.listarProductos());
                        break;
                    case "2":
                        System.out.print("Código: ");
                        String codigo = scanner.nextLine().trim();
                        System.out.print("Nombre: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("Categoría: ");
                        String categoria = scanner.nextLine().trim();
                        System.out.print("Precio: ");
                        double precio = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Stock inicial: ");
                        int stock = Integer.parseInt(scanner.nextLine().trim());

                        ProductoDTO nuevo = new ProductoDTO(0, codigo, nombre, categoria, precio, stock);
                        ProductoDTO guardado = servicio.agregarProducto(nuevo);
                        System.out.println("[+] Producto agregado: " + guardado);
                        break;
                    case "3":
                        System.out.print("Ingrese ID del producto: ");
                        int idBusqueda = Integer.parseInt(scanner.nextLine().trim());
                        ProductoDTO encontrado = servicio.obtenerProductoPorId(idBusqueda);
                        if (encontrado != null) {
                            System.out.println("[+] Encontrado: " + encontrado);
                        } else {
                            System.out.println("[-] No se encontró ningún producto con ID " + idBusqueda);
                        }
                        break;
                    case "4":
                        System.out.print("ID del producto a vender: ");
                        int idVenta = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Cantidad a vender: ");
                        int cantVenta = Integer.parseInt(scanner.nextLine().trim());
                        OperacionDTO resVenta = servicio.registrarVenta(idVenta, cantVenta);
                        System.out.println("[+] " + resVenta);
                        break;
                    case "5":
                        System.out.print("ID del producto: ");
                        int idStock = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Nuevo stock: ");
                        int nuevoStock = Integer.parseInt(scanner.nextLine().trim());
                        OperacionDTO resStock = servicio.actualizarStock(idStock, nuevoStock);
                        System.out.println("[+] " + resStock);
                        break;
                    case "6":
                        double total = servicio.calcularValorTotalInventario();
                        System.out.printf("[+] Valor total de inventario: $%,.2f%n", total);
                        break;
                    case "7":
                        salir = true;
                        System.out.println("Saliendo del cliente RMI...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.err.println("[-] Error durante la operación remota: " + e.getMessage());
            }
        }
    }

    private static void imprimirListaProductos(List<ProductoDTO> lista) {
        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("%-6s | %-10s | %-26s | %-15s | %-12s | %-6s%n", "ID", "CÓDIGO", "NOMBRE", "CATEGORÍA", "PRECIO", "STOCK");
        System.out.println("------------------------------------------------------------------------------------------------------");
        if (lista == null || lista.isEmpty()) {
            System.out.println("               (No hay productos registrados en el servidor)");
        } else {
            for (ProductoDTO p : lista) {
                System.out.printf("%-6d | %-10s | %-26s | %-15s | $%10.2f | %6d%n",
                        p.getId(), p.getCodigo(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock());
            }
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
    }

    @SuppressWarnings("removal")
    private static void configurarSeguridad() {
        String policy = System.getProperty("java.security.policy");
        if (policy != null) {
            System.out.println("[+] Política de seguridad cliente (-Djava.security.policy): " + policy);
        }
        try {
            if (System.getSecurityManager() == null) {
                try {
                    System.setSecurityManager(new SecurityManager());
                } catch (UnsupportedOperationException ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
