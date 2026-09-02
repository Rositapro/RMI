package com.rmi.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Objeto de Transferencia de Datos (DTO) que representa un Producto en el sistema.
 * Debe implementar java.io.Serializable para poder ser transmitido a través de
 * la red mediante Java RMI (Remote Method Invocation).
 */
public class ProductoDTO implements Serializable {

    // Identificador único para el proceso de serialización y deserialización
    private static final long serialVersionUID = 1L;

    private int id;
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;

    /**
     * Constructor por defecto (requerido para serialización e instanciación vacía).
     */
    public ProductoDTO() {
    }

    /**
     * Constructor completo parametrizado.
     *
     * @param id        Identificador numérico
     * @param codigo    Código alfanumérico único
     * @param nombre    Nombre del producto
     * @param categoria Categoría a la que pertenece
     * @param precio    Precio unitario en moneda local
     * @param stock     Cantidad de unidades disponibles
     */
    public ProductoDTO(int id, String codigo, String nombre, String categoria, double precio, int stock) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoDTO that = (ProductoDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[ID: %d | Cod: %-8s | %-22s | Cat: %-12s | Precio: $%8.2f | Stock: %3d]",
                id, codigo, nombre, categoria, precio, stock);
    }
}
