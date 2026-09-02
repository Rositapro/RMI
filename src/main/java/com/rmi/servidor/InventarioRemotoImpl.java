package com.rmi.servidor;

import com.rmi.dto.OperacionDTO;
import com.rmi.dto.ProductoDTO;
import com.rmi.interfaz.IInventarioRemoto;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementación del Objeto Remoto.
 * Extiende UnicastRemoteObject para habilitar la exportación del objeto remoto
 * y la comunicación punto a punto sobre sockets TCP/IP.
 * Implementa la interfaz IInventarioRemoto.
 */
public class InventarioRemotoImpl extends UnicastRemoteObject implements IInventarioRemoto {

    private static final long serialVersionUID = 1L;

    // Almacén en memoria concurrente para acceso seguro multi-hilo
    private final Map<Integer, ProductoDTO> baseDatosProductos;
    private final AtomicInteger generadorId;

    /**
     * Constructor que exporta el objeto remoto en un puerto anónimo (super(0))
     * e inicializa datos de muestra en el servidor.
     *
     * @throws RemoteException Si ocurre un error al exportar el objeto remoto.
     */
    public InventarioRemotoImpl() throws RemoteException {
        super(0); // 0 indica que la JVM elegirá un puerto TCP efímero para el stub
        this.baseDatosProductos = new ConcurrentHashMap<>();
        this.generadorId = new AtomicInteger(100);

        // Cargar productos iniciales para pruebas inmediatas
        inicializarDatosMuestra();
    }

    private void inicializarDatosMuestra() {
        int id1 = generadorId.incrementAndGet();
        baseDatosProductos.put(id1, new ProductoDTO(id1, "LAP-001", "Laptop Dell XPS 15", "Computación", 1850.00, 15));

        int id2 = generadorId.incrementAndGet();
        baseDatosProductos.put(id2, new ProductoDTO(id2, "MOU-002", "Mouse Logitech MX 3", "Accesorios", 99.99, 40));

        int id3 = generadorId.incrementAndGet();
        baseDatosProductos.put(id3, new ProductoDTO(id3, "TEC-003", "Teclado Mecánico RGB", "Accesorios", 129.50, 25));

        int id4 = generadorId.incrementAndGet();
        baseDatosProductos.put(id4, new ProductoDTO(id4, "MON-004", "Monitor 4K 27 Pulgadas", "Pantallas", 420.00, 10));

        System.out.println("[SERVIDOR] Inventario inicializado con " + baseDatosProductos.size() + " productos.");
    }

    @Override
    public synchronized ProductoDTO agregarProducto(ProductoDTO producto) throws RemoteException {
        if (producto == null) {
            throw new IllegalArgumentException("El producto recibido no puede ser nulo.");
        }

        // Si el cliente no asignó ID o es 0, el servidor genera uno autoincrementable
        if (producto.getId() <= 0) {
            producto.setId(generadorId.incrementAndGet());
        }

        baseDatosProductos.put(producto.getId(), producto);

        System.out.println("[SERVIDOR - INVOCACIÓN REMOTA] agregarProducto() -> " + producto);
        return producto;
    }

    @Override
    public ProductoDTO obtenerProductoPorId(int id) throws RemoteException {
        ProductoDTO producto = baseDatosProductos.get(id);
        System.out.println("[SERVIDOR - INVOCACIÓN REMOTA] obtenerProductoPorId(" + id + ") -> "
                + (producto != null ? producto.getNombre() : "NO ENCONTRADO"));
        return producto;
    }

    @Override
    public List<ProductoDTO> listarProductos() throws RemoteException {
        System.out.println("[SERVIDOR - INVOCACIÓN REMOTA] listarProductos() solicitados. Total: " + baseDatosProductos.size());
        // Devolvemos un ArrayList estándar (que es Serializable)
        return new ArrayList<>(baseDatosProductos.values());
    }

    @Override
    public synchronized OperacionDTO registrarVenta(int idProducto, int cantidad) throws RemoteException {
        System.out.println("[SERVIDOR - INVOCACIÓN REMOTA] registrarVenta(ID: " + idProducto + ", Cantidad: " + cantidad + ")");

        ProductoDTO prod = baseDatosProductos.get(idProducto);
        if (prod == null) {
            return OperacionDTO.fallo("VENTA", "El producto con ID " + idProducto + " no existe en el sistema.");
        }

        if (cantidad <= 0) {
            return OperacionDTO.fallo("VENTA", "La cantidad a comprar debe ser mayor a 0.");
        }

        if (prod.getStock() < cantidad) {
            return OperacionDTO.fallo("VENTA", String.format(
                    "Stock insuficiente. Solicitado: %d, Disponible: %d para '%s'.",
                    cantidad, prod.getStock(), prod.getNombre()));
        }

        // Actualizar stock de forma consistente
        prod.setStock(prod.getStock() - cantidad);
        double total = prod.getPrecio() * cantidad;

        String mensaje = String.format("Venta de %d unidad(es) de '%s' procesada con éxito.", cantidad, prod.getNombre());
        return OperacionDTO.exito("VENTA", mensaje, prod, total);
    }

    @Override
    public synchronized OperacionDTO actualizarStock(int idProducto, int nuevoStock) throws RemoteException {
        System.out.println("[SERVIDOR - INVOCACIÓN REMOTA] actualizarStock(ID: " + idProducto + ", NuevoStock: " + nuevoStock + ")");

        ProductoDTO prod = baseDatosProductos.get(idProducto);
        if (prod == null) {
            return OperacionDTO.fallo("ACTUALIZAR_STOCK", "El producto con ID " + idProducto + " no existe.");
        }

        if (nuevoStock < 0) {
            return OperacionDTO.fallo("ACTUALIZAR_STOCK", "El stock no puede ser negativo.");
        }

        int stockAnterior = prod.getStock();
        prod.setStock(nuevoStock);

        String mensaje = String.format("Stock de '%s' actualizado de %d a %d unidades.", prod.getNombre(), stockAnterior, nuevoStock);
        return OperacionDTO.exito("ACTUALIZAR_STOCK", mensaje, prod, 0.0);
    }

    @Override
    public double calcularValorTotalInventario() throws RemoteException {
        double valorTotal = baseDatosProductos.values().stream()
                .mapToDouble(p -> p.getPrecio() * p.getStock())
                .sum();

        System.out.printf("[SERVIDOR - INVOCACIÓN REMOTA] calcularValorTotalInventario() -> $%,.2f%n", valorTotal);
        return valorTotal;
    }
}
