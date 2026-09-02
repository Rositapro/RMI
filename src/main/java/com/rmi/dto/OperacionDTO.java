package com.rmi.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Objeto de Transferencia de Datos (DTO) que encapsula el resultado
 * de cualquier operación transaccional ejecutada en el servidor RMI.
 */
public class OperacionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean exitosa;
    private String tipoOperacion;
    private String mensaje;
    private String marcaTiempo;
    private ProductoDTO productoInvolucrado;
    private double montoTotal;

    public OperacionDTO() {
        this.marcaTiempo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public OperacionDTO(boolean exitosa, String tipoOperacion, String mensaje, ProductoDTO productoInvolucrado, double montoTotal) {
        this.exitosa = exitosa;
        this.tipoOperacion = tipoOperacion;
        this.mensaje = mensaje;
        this.productoInvolucrado = productoInvolucrado;
        this.montoTotal = montoTotal;
        this.marcaTiempo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Métodos estáticos de fábrica para conveniencia
    public static OperacionDTO exito(String operacion, String mensaje, ProductoDTO producto, double monto) {
        return new OperacionDTO(true, operacion, mensaje, producto, monto);
    }

    public static OperacionDTO fallo(String operacion, String mensaje) {
        return new OperacionDTO(false, operacion, mensaje, null, 0.0);
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public boolean isExitosa() {
        return exitosa;
    }

    public void setExitosa(boolean exitosa) {
        this.exitosa = exitosa;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(String marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }

    public ProductoDTO getProductoInvolucrado() {
        return productoInvolucrado;
    }

    public void setProductoInvolucrado(ProductoDTO productoInvolucrado) {
        this.productoInvolucrado = productoInvolucrado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    @Override
    public String toString() {
        return String.format("OperacionDTO [Estado: %s | Tipo: %s | Mensaje: %s | Monto: $%.2f | Fecha: %s]",
                exitosa ? "EXITOSA" : "FALLIDA", tipoOperacion, mensaje, montoTotal, marcaTiempo);
    }
}
