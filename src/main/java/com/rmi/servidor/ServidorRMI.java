package com.rmi.servidor;

import com.rmi.interfaz.IInventarioRemoto;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Clase principal encargada de iniciar el servidor RMI:
 * 1. Configura el entorno de seguridad y verifica la política (java.security.policy).
 * 2. Inicia o localiza el Registro RMI (rmiregistry) en el puerto 1099.
 * 3. Instancia el objeto remoto (InventarioRemotoImpl).
 * 4. Publica/Registra el objeto remoto bajo el nombre "ServicioInventario".
 */
public class ServidorRMI {

    public static final int PUERTO_RMI = 1099;
    public static final String NOMBRE_SERVICIO = "ServicioInventario";

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("            INICIANDO SERVIDOR RMI DE INVENTARIO         ");
        System.out.println("=========================================================");

        // 1. Manejo de Políticas de Seguridad (Security Policy)
        configurarSeguridad();

        try {
            // 2. Inicialización del Registro RMI (rmiregistry)
            Registry registro;
            try {
                registro = LocateRegistry.createRegistry(PUERTO_RMI);
                System.out.println("[+] Registro RMI creado exitosamente en el puerto local: " + PUERTO_RMI);
            } catch (Exception ex) {
                System.out.println("[!] El registro RMI ya existía en el puerto " + PUERTO_RMI + ". Conectando al existente...");
                registro = LocateRegistry.getRegistry(PUERTO_RMI);
            }

            // 3. Creación de la instancia del servicio remoto
            System.out.println("[+] Instanciando la implementación del servicio remoto...");
            IInventarioRemoto servicio = new InventarioRemotoImpl();

            // 4. Registro del objeto en el rmiregistry
            // Opción A: Mediante Registry.rebind
            registro.rebind(NOMBRE_SERVICIO, servicio);

            // Opción B: También documentada mediante Naming.rebind
            String urlRmi = String.format("rmi://localhost:%d/%s", PUERTO_RMI, NOMBRE_SERVICIO);
            // Naming.rebind(urlRmi, servicio); // Equivalente a registro.rebind

            System.out.println("---------------------------------------------------------");
            System.out.println("[OK] Servicio registrado con éxito bajo el nombre: \"" + NOMBRE_SERVICIO + "\"");
            System.out.println("[OK] URL RMI de acceso: " + urlRmi);
            System.out.println("[OK] Servidor en espera de invocaciones de clientes remotos...");
            System.out.println("=========================================================");

        } catch (Exception e) {
            System.err.println("[-] Error crítico al iniciar el servidor RMI:");
            e.printStackTrace();
        }
    }

    /**
     * Valida la propiedad java.security.policy y gestiona el SecurityManager
     * de forma compatible tanto con versiones clásicas de Java (8-11)
     * como modernas (17, 21 y 25).
     */
    @SuppressWarnings("removal")
    private static void configurarSeguridad() {
        String policyFile = System.getProperty("java.security.policy");
        if (policyFile != null) {
            System.out.println("[+] Archivo de política detectado (-Djava.security.policy): " + policyFile);
        } else {
            System.out.println("[!] Advertencia: No se especificó -Djava.security.policy. Usando permisos por defecto.");
        }

        try {
            // En versiones modernas de Java (17+), SecurityManager está marcado para eliminación (JEP 411)
            // Intentamos activarlo respetando la configuración del usuario si el entorno lo soporta
            if (System.getSecurityManager() == null) {
                try {
                    System.setSecurityManager(new SecurityManager());
                    System.out.println("[+] SecurityManager inicializado con las directivas de la política.");
                } catch (UnsupportedOperationException uoe) {
                    System.out.println("[i] Java moderno detectado: SecurityManager es obsoleto/desactivado por la JVM.");
                }
            }
        } catch (Throwable t) {
            System.out.println("[i] Nota sobre seguridad: " + t.getMessage());
        }
    }
}
