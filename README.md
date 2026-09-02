# Práctica de Programación en Ambiente Cliente-Servidor: Java RMI

**Materia:** Programación en Ambiente Cliente/Servidor  
**Tema:** Invocación a Métodos Remotos (Remote Method Invocation - RMI)  
**Plataforma:** Java SE (Compatible con Java 8, 11, 17, 21 y 25)

---

## 1. Descripción del Proyecto

Este proyecto implementa una arquitectura distribuida completa utilizando **Java RMI (Remote Method Invocation)** para la gestión remota de un inventario y procesamiento de transacciones. 

El sistema demuestra la interacción en red entre dos procesos JVM independientes (Servidor y Cliente), permitiendo:
1. El intercambio bidireccional de **objetos serializables (`Serializable`)**.
2. La definición y publicación de una **interfaz remota (`Remote`)**.
3. La creación dinámica del **registro RMI (`rmiregistry`)** en tiempo de ejecución.
4. El enlace (`rebind`) y localización (`lookup`) del servicio distribuido.
5. La configuración y aplicación de **políticas de seguridad (`java.security.policy`)**.

---

## 2. Estructura del Proyecto

```
RMI/
├── pom.xml                                   # Configuración de compilación Maven
├── servidor.policy                           # Política de seguridad Java para el Servidor
├── cliente.policy                            # Política de seguridad Java para el Cliente
├── compilar.bat                              # Script Windows Batch de compilación
├── compilar.ps1                              # Script PowerShell de compilación
├── ejecutar-servidor.bat                     # Script Windows Batch para iniciar el Servidor
├── ejecutar-servidor.ps1                     # Script PowerShell para iniciar el Servidor
├── ejecutar-cliente.bat                      # Script Windows Batch para iniciar el Cliente
├── ejecutar-cliente.ps1                      # Script PowerShell para iniciar el Cliente
├── README.md                                 # Documentación técnica completa
└── src/
    └── main/
        └── java/
            └── com/
                └── rmi/
                    ├── dto/
                    │   ├── ProductoDTO.java   # DTO serializable: Entidad de producto
                    │   └── OperacionDTO.java  # DTO serializable: Resultado de transacciones
                    ├── interfaz/
                    │   └── IInventarioRemoto.java # Interfaz remota (extends java.rmi.Remote)
                    ├── servidor/
                    │   ├── InventarioRemotoImpl.java # Implementación (extends UnicastRemoteObject)
                    │   └── ServidorRMI.java          # Servidor principal (LocateRegistry + rebind)
                    └── cliente/
                        └── ClienteRMI.java           # Cliente RMI (lookup + invocación remota)
```

---

## 3. Fundamentos Teóricos de los Componentes

### A. Objeto de Transferencia de Datos (`ProductoDTO` y `OperacionDTO`)
- Implementan `java.io.Serializable`.
- Cuentan con un `serialVersionUID` explícito para garantizar que tanto la JVM del cliente como la del servidor mantengan compatibilidad binaria durante el proceso de empaquetado (*marshalling*) y desempaquetado (*unmarshalling*).

### B. Interfaz Remota (`IInventarioRemoto`)
- Hereda de `java.rmi.Remote`.
- Todos sus métodos declaran `throws java.rmi.RemoteException`. Esto es indispensable porque cualquier llamada a través de la red es susceptible a fallas de comunicación, desconexiones o latencias.

### C. Implementación del Servicio (`InventarioRemotoImpl`)
- Hereda de `java.rmi.server.UnicastRemoteObject`.
- Al instanciarse, exporta automáticamente el objeto para que pueda recibir llamadas remotas entrantes a través de un puerto TCP.

### D. Servidor y Registro RMI (`ServidorRMI`)
- Utiliza `LocateRegistry.createRegistry(1099)` para levantar el registro de nombres RMI de forma integrada sin necesidad de iniciar una utilidad externa manualmente.
- Vincula la instancia remota bajo el alias `"ServicioInventario"` mediante `registry.rebind(...)`.

### E. Archivo de Política de Seguridad (`.policy`)
- Los archivos `servidor.policy` y `cliente.policy` establecen los permisos requeridos para la apertura de sockets de red y la comunicación distribuida (`SocketPermission`, `AllPermission`).
- Se le indican a la máquina virtual mediante la propiedad de JVM: `-Djava.security.policy=nombre.policy`.

---

## 4. Instrucciones de Compilación y Ejecución

### Opción 1: Ejecución Rápida en 1 Clic (Scripts de Windows)

1. **Compilar el código:**  
   Haz doble clic en `compilar.bat` (o ejecuta `.\compilar.ps1` en PowerShell).

2. **Iniciar el Servidor:**  
   Haz doble clic en `ejecutar-servidor.bat` (o ejecuta `.\ejecutar-servidor.ps1`).  
   Verás el mensaje indicando que el registro RMI fue creado en el puerto 1099 y que el servidor está listo.

3. **Iniciar el Cliente:**  
   Abre una segunda ventana y haz doble clic en `ejecutar-cliente.bat` (o ejecuta `.\ejecutar-cliente.ps1`).  
   El cliente ejecutará automáticamente todas las pruebas remotas mostrando los objetos recibidos.

> **Modo Interactivo (Menú por consola):**  
> Si deseas interactuar manualmente ingresando datos con un menú en consola:  
> `ejecutar-cliente.bat --menu`  
> o en PowerShell:  
> `.\ejecutar-cliente.ps1 -Menu`

---

### Opción 2: Comandos Manuales desde la Terminal

Si deseas ejecutar los comandos manualmente en tu consola (cmd / PowerShell):

#### 1. Compilación:
```bash
# Crear directorio de salida
mkdir bin

# Compilar todos los archivos fuente
javac -encoding UTF-8 -d bin src/main/java/com/rmi/dto/*.java src/main/java/com/rmi/interfaz/*.java src/main/java/com/rmi/servidor/*.java src/main/java/com/rmi/cliente/*.java
```

#### 2. Ejecutar Servidor (con archivo de políticas):
```bash
java -Djava.security.policy=servidor.policy -cp bin com.rmi.servidor.ServidorRMI
```

#### 3. Ejecutar Cliente (en otra terminal):
```bash
# Modo demostración automática:
java -Djava.security.policy=cliente.policy -cp bin com.rmi.cliente.ClienteRMI

# O modo menú interactivo:
java -Djava.security.policy=cliente.policy -cp bin com.rmi.cliente.ClienteRMI localhost 1099 --menu
```

---

### Opción 3: Compilación con Maven (si cuentas con `mvn`)
```bash
mvn clean compile
```

---

## 5. Ejecución entre Dos Computadoras Distintas en Red LAN

Si vas a probar el servidor en una máquina y el cliente en otra:

1. **En la computadora Servidor:**
   Asegúrate de pasar la dirección IP local del servidor mediante `-Djava.rmi.server.hostname`:
   ```bash
   java -Djava.rmi.server.hostname=192.168.1.50 -Djava.security.policy=servidor.policy -cp bin com.rmi.servidor.ServidorRMI
   ```
   *(Sustituye `192.168.1.50` por la IP real de tu máquina servidor).*

2. **En la computadora Cliente:**
   Ejecuta pasando como primer parámetro la IP del servidor:
   ```bash
   java -Djava.security.policy=cliente.policy -cp bin com.rmi.cliente.ClienteRMI 192.168.1.50 1099
   ```
