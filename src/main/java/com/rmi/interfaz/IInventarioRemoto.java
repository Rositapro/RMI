package com.rmi.interfaz;

import com.rmi.dto.OperacionDTO;
import com.rmi.dto.ProductoDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaz Remota del servicio de Inventario.
 * Cumple con los estándares de Java RMI:
 * 1. Extiende de java.rmi.Remote.
 * 2. Cada método declara la excepción java.rmi.RemoteException.
 * 3. Permite la transferencia de objetos serializables en sus parámetros y retornos.
 */
public interface IInventarioRemoto extends Remote {

    /**
     * Registra un nuevo producto en el inventario remoto.
     *
     * @param producto El objeto ProductoDTO serializable a almacenar.
     * @return El ProductoDTO registrado con identificador asignado.
     * @throws RemoteException En caso de fallas de comunicación remota.
     */
    ProductoDTO agregarProducto(ProductoDTO producto) throws RemoteException;

    /**
     * Consulta un producto por su identificador único.
     *
     * @param id Identificador numérico del producto.
     * @return El ProductoDTO encontrado o null si no existe.
     * @throws RemoteException En caso de fallas de comunicación remota.
     */
    ProductoDTO obtenerProductoPorId(int id) throws RemoteException;

    /**
     * Obtiene la lista completa de todos los productos en el inventario.
     *
     * @return Lista serializable conteniendo todos los ProductoDTO.
     * @throws RemoteException En caso de fallas de comunicación remota.
     */
    List<ProductoDTO> listarProductos() throws RemoteException;

    /**
     * Procesa una venta remota disminuyendo el stock del producto indicado.
     *
     * @param idProducto Identificador del producto a vender.
     * @param cantidad   Unidades a vender.
     * @return Objeto OperacionDTO detallando el estatus, monto total y producto actualizado.
     * @throws RemoteException En caso de fallas de comunicación remota.
     */
    OperacionDTO registrarVenta(int idProducto, int cantidad) throws RemoteException;

    /**
     * Actualiza las existencias disponibles de un producto específico.
     *
     * @param idProducto Identificador del producto.
     * @param nuevoStock Nueva cantidad de inventario físico.
     * @return Objeto OperacionDTO con el resultado de la actualización.
     * @throws RemoteException En caso de fallas de comunicación remota.
     */
    OperacionDTO actualizarStock(int idProducto, int nuevoStock) throws RemoteException;

    /**
     * Calcula la sumatoria del valor monetario total del inventario actual.
     *
     * @return Suma de (precio * stock) de todos los productos.
     * @throws RemoteException En caso de fallas de comunicación remota.
     */
    double calcularValorTotalInventario() throws RemoteException;
}
