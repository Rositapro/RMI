# INSTITUTO TECNOLÓGICO SUPERIOR DE MONCLOVA
## TECNOLÓGICO NACIONAL DE MÉXICO
### DEPARTAMENTO DE SISTEMAS Y COMPUTACIÓN

---

# REPORTE DE PRÁCTICA DE LABORATORIO

**Materia:** Programación en Ambiente Cliente/Servidor  
**Docente:** Ing. / M.C. Riojas  
**Semestre:** 7mo Semestre — Ingeniería en Sistemas Computacionales  
**Tema:** Unidad: Invocación a Métodos Remotos (Java RMI)  
**Nombre de la Práctica:** Implementación de un Sistema Distribuido de Gestión de Inventario mediante Java RMI con Objetos Serializables y Políticas de Seguridad  
**Alumno(a):** Rosita (No. de Control: 23050333)  
**Repositorio GitHub:** [https://github.com/Rositapro/RMI](https://github.com/Rositapro/RMI)  
**Fecha:** Septiembre de 2026  
**Lugar:** Monclova, Coahuila, México  

---

## 1. OBJETIVO DE LA PRÁCTICA

Diseñar, implementar y comprobar experimentalmente una aplicación distribuida basada en el modelo Cliente-Servidor utilizando la tecnología **Java Remote Method Invocation (RMI)**. 

### Objetivos específicos:
1. Diseñar **Objetos de Transferencia de Datos (DTO)** que implementen la interfaz `java.io.Serializable` para transportar información de forma estructurada a través de la red.
2. Definir una **Interfaz Remota** que extienda de `java.rmi.Remote`, declarando las excepciones de comunicación distribuida (`RemoteException`).
3. Construir la **Implementación del Servicio Remoto** heredando de `UnicastRemoteObject` para la exportación del stub de comunicaciones en el puerto TCP correspondiente.
4. Inicializar y administrar el **Servicio de Nombres RMI (`rmiregistry`)** en tiempo de ejecución en el puerto estándar 1099 y registrar el objeto servidor mediante `rebind`.
5. Implementar un **Cliente RMI** capaz de localizar el stub remoto mediante `lookup` e invocar procedimientos remotos de manera transparente tanto en modalidad automatizada como interactiva.
6. Aplicar y verificar **Políticas de Seguridad Java (`java.security.policy`)** para el control de accesos a sockets y recursos del sistema.

---

## 2. COMPETENCIAS A DESARROLLAR

- **Competencia Instrumental:** Capacidad de análisis y programación orientada a objetos en entornos distribuidos, empleando Java SE.
- **Competencia Técnica:** Habilidad para gestionar la serialización y deserialización binaria de objetos en tránsito por la red, así como la configuración de entornos de seguridad y resolución de nombres remotos.
- **Competencia Sistémica:** Comprensión de la abstracción que ofrecen los middlewares orientados a objetos (RPC / RMI) frente a la comunicación básica basada en Sockets TCP puros.

---

## 3. FUNDAMENTOS TEÓRICOS

### 3.1. ¿Qué es Java RMI (Remote Method Invocation)?
Java RMI es un mecanismo provisto por el lenguaje Java que permite que un objeto que se ejecuta en una Máquina Virtual de Java (JVM) invoque métodos de un objeto que se ejecuta en otra JVM, la cual puede estar situada en el mismo equipo o en otra computadora a través de una red local (LAN) o Internet.

A diferencia del paradigma tradicional de Sockets donde el programador debe serializar a mano cadenas de texto o flujos de bytes (e implementar un protocolo de aplicación propio), RMI abstrae la capa de transporte: el programador simplemente llama a un método como si fuera local (`objeto.metodo()`), y la infraestructura se encarga de la comunicación subyacente.

### 3.2. Arquitectura de Java RMI
La infraestructura de RMI se divide en tres capas fundamentales:
1. **Capa de Stubs y Skeletons:**
   - **Stub (Cliente):** Actúa como el representante o proxy local del objeto remoto. Intercepta la llamada al método, empaqueta los parámetros en un flujo binario (*marshalling*) y los envía por la red.
   - **Skeleton / Dispatcher (Servidor):** Recibe la llamada, desempaqueta los parámetros (*unmarshalling*), invoca el método sobre la instancia real del servidor y serializa la respuesta para enviarla de regreso al cliente.
2. **Capa de Referencia Remota (Remote Reference Layer - RRL):** Maneja la semántica de la invocación (punto a punto, unicast).
3. **Capa de Transporte (Transport Layer):** Gestiona las conexiones de red TCP/IP reales entre los dos procesos.

### 3.3. Serialización de Objetos (`Serializable` y `serialVersionUID`)
Para que un objeto pueda viajar desde una JVM emisora a una receptora, debe convertirse en una secuencia de bytes mediante el proceso de serialización. En Java, esto se logra implementando la interfaz marcadora `java.io.Serializable`. 
Se define el campo `private static final long serialVersionUID` para asegurar que ambas partes utilicen versiones binariamente idénticas de la clase; de lo contrario, se produciría un `InvalidClassException`.

### 3.4. El Registro de Nombres RMI (`rmiregistry`)
Es un servicio de directorio simple que asocia un nombre alfanumérico (URL de servicio) con una referencia remota (Stub). Funciona como un servicio de "páginas amarillas":
- El **Servidor** utiliza `rebind("NombreServicio", objetoRemoto)` para publicar su servicio.
- El **Cliente** utiliza `lookup("NombreServicio")` para obtener la referencia remota y poder comenzar a invocarlo.

### 3.5. Políticas de Seguridad (`.policy`) y `SecurityManager`
En aplicaciones distribuidas donde se descargan o intercambian clases y se abren puertos de red, Java implementa un mecanismo de arena de seguridad (*sandbox*). Mediante el archivo de política (ej. `servidor.policy` y `cliente.policy`) se otorgan los permisos necesarios (`SocketPermission`, `accept`, `connect`, `listen`) para que la JVM permita la comunicación entre procesos.

---

## 4. DESCRIPCIÓN DE LA SOLUCIÓN Y ARQUITECTURA DEL SISTEMA

Se diseñó e implementó un **Sistema Distribuido de Gestión de Inventario y Transacciones**, cuya estructura modular en paquetes es la siguiente:

```
RMI/
├── cliente.policy                           # Política de seguridad para la JVM Cliente
├── servidor.policy                          # Política de seguridad para la JVM Servidor
├── compilar.bat                             # Script de compilación automatizada
├── ejecutar-servidor.bat                    # Script de inicio del Servidor RMI
├── ejecutar-cliente.bat                     # Script de inicio del Cliente (demostración)
├── ejecutar-cliente-menu.bat                # Script de inicio del Cliente interactivo
├── pom.xml                                  # Descriptor de proyecto Maven
├── README.md                                # Documentación técnica del repositorio
└── src/main/java/com/rmi/
    ├── dto/
    │   ├── ProductoDTO.java                 # Entidad del producto (Serializable)
    │   └── OperacionDTO.java                # Entidad de resultado transaccional (Serializable)
    ├── interfaz/
    │   └── IInventarioRemoto.java           # Contrato remoto (extends Remote)
    ├── servidor/
    │   ├── InventarioRemotoImpl.java        # Lógica de negocio (extends UnicastRemoteObject)
    │   └── ServidorRMI.java                 # Host del servidor e inicio del LocateRegistry
    └── cliente/
        └── ClienteRMI.java                  # Cliente consumidor (lookup e invocación)
```

### 4.1. Clases y Componentes Implementados

1. **`ProductoDTO`:** Representa un artículo del almacén. Contiene atributos como `id`, `codigo`, `nombre`, `categoria`, `precio` y `stock`. Implementa `Serializable`.
2. **`OperacionDTO`:** Representa el recibo o resultado de una transacción (Venta o Actualización). Transporta el estado (`EXITOSA`/`FALLIDA`), tipo de operación, mensaje de retroalimentación, monto total, producto resultante y marca de tiempo.
3. **`IInventarioRemoto`:** Interfaz que define las operaciones remotas disponibles:
   - `listarProductos()`: Retorna `List<ProductoDTO>`.
   - `agregarProducto(ProductoDTO)`: Inserta un nuevo producto serializado.
   - `obtenerProductoPorId(int)`: Consulta un producto por su clave primaria.
   - `registrarVenta(int id, int cantidad)`: Realiza el débito de existencias y calcula el costo.
   - `actualizarStock(int id, int nuevoStock)`: Reabastece o ajusta inventario.
   - `calcularValorTotalInventario()`: Realiza el cálculo económico del almacén.
4. **`InventarioRemotoImpl`:** Implementa los métodos anteriores con soporte a concurrencia mediante `ConcurrentHashMap` y métodos sincronizados para evitar condiciones de carrera (*race conditions*) en ventas simultáneas.
5. **`ServidorRMI`:** Levanta dinámicamente el registro en el puerto 1099 (`LocateRegistry.createRegistry(1099)`), instancia el objeto y lo vincula bajo el alias `"ServicioInventario"`.
6. **`ClienteRMI`:** Obtiene el stub mediante `LocateRegistry.getRegistry()` y `lookup()`, ofreciendo dos modos de operación:
   - **Modo Demostración Automática:** Ejecuta secuencialmente 8 pasos de validación.
   - **Modo Menú Interactivo:** Permite al usuario capturar registros, vender y consultar desde consola en tiempo real.

---

## 5. EVIDENCIAS DE EJECUCIÓN Y RESULTADOS

### 5.1. Paso 1: Compilación del Código Fuente
Se ejecutó el archivo `compilar.bat`. El compilador `javac` generó los archivos de bytecode binario (`.class`) organizados en el directorio `bin/`:

```text
========================================================
      COMPILANDO PROYECTO JAVA RMI (CLIENTE-SERVIDOR)
========================================================
[+] Compilando clases Java con javac hacia carpeta 'bin/'...

[OK] ¡Compilación exitosa!
Las clases compiladas se encuentran en la carpeta 'bin/'
```
*(Insertar aquí: Captura de pantalla de la ventana de compilación exitosa)*

---

### 5.2. Paso 2: Inicialización del Servidor RMI
Al ejecutar `ejecutar-servidor.bat`, el servidor carga la política de seguridad `servidor.policy`, levanta el registro RMI en el puerto 1099 y publica el servicio:

```text
=========================================================
            INICIANDO SERVIDOR RMI DE INVENTARIO         
=========================================================
[+] Registro RMI creado exitosamente en el puerto local: 1099
[+] Instanciando la implementación del servicio remoto...
[SERVIDOR] Inventario inicializado con 4 productos.
---------------------------------------------------------
[OK] Servicio registrado con éxito bajo el nombre: "ServicioInventario"
[OK] URL RMI de acceso: rmi://localhost:1099/ServicioInventario
[OK] Servidor en espera de invocaciones de clientes remotos...
=========================================================
```
*(Insertar aquí: Captura de pantalla del Servidor esperando peticiones)*

---

### 5.3. Paso 3: Ejecución del Cliente (Demostración Automatizada)
Al ejecutar `ejecutar-cliente.bat`, el cliente se conecta remotamente y ejecuta con éxito los 8 pasos de prueba:

```text
=========================================================
               CLIENTE JAVA RMI DE INVENTARIO            
=========================================================
[+] Conectando al Registro RMI en localhost:1099...
[+] Buscando el servicio remoto 'ServicioInventario' en el registro...
[OK] ¡Conexión establecida con éxito con el objeto remoto!
---------------------------------------------------------

>>> [PASO 1] Consultando lista de productos iniciales en el servidor...
------------------------------------------------------------------------------------------------------
ID     | CÓDIGO     | NOMBRE                     | CATEGORÍA       | PRECIO       | STOCK 
------------------------------------------------------------------------------------------------------
101    | LAP-001    | Laptop Dell XPS 15         | Computación     | $   1850.00 |     15
102    | MOU-002    | Mouse Logitech MX 3        | Accesorios      | $     99.99 |     40
103    | TEC-003    | Teclado Mecánico RGB       | Accesorios      | $    129.50 |     25
104    | MON-004    | Monitor 4K 27 Pulgadas     | Pantallas       | $    420.00 |     10
------------------------------------------------------------------------------------------------------

>>> [PASO 2] Enviando un nuevo objeto ProductoDTO serializado al servidor...
  -> Objeto local a enviar: [ID: 0 | Cod: AUR-005  | Audífonos Sony WH-1000XM5 | Cat: Audio | Precio: $349.99 | Stock: 18]
  <- Objeto recibido tras registro: [ID: 105 | Cod: AUR-005 | Audífonos Sony WH-1000XM5 | Cat: Audio | Precio: $349.99 | Stock: 18]

>>> [PASO 3] Consultando producto por ID remoto (ID: 105)...
  <- Objeto consultado: [ID: 105 | Cod: AUR-005 | Audífonos Sony WH-1000XM5 | Cat: Audio | Precio: $349.99 | Stock: 18]

>>> [PASO 4] Realizando una operación remota de VENTA (3 unidades)...
  <- Resultado: OperacionDTO [Estado: EXITOSA | Tipo: VENTA | Mensaje: Venta de 3 unidad(es) de 'Audífonos Sony WH-1000XM5' procesada con éxito. | Monto: $1049.97 | Fecha: 2026-09-02]
  <- Stock restante en servidor: 15

>>> [PASO 5] Intentando una VENTA con stock insuficiente (Validación de Regla de Negocio)...
  <- Resultado esperado: OperacionDTO [Estado: FALLIDA | Tipo: VENTA | Mensaje: Stock insuficiente. Solicitado: 999, Disponible: 15... | Monto: $0.00]

>>> [PASO 6] Actualizando stock remotamente con OperacionDTO...
  <- Resultado: OperacionDTO [Estado: EXITOSA | Tipo: ACTUALIZAR_STOCK | Mensaje: Stock actualizado de 15 a 50 unidades.]

>>> [PASO 7] Calculando el valor monetario total del inventario remoto...
  <- Valor total del inventario en el servidor: $56,686.60 MXN

>>> [PASO 8] Lista final de productos en el servidor tras todas las operaciones:
------------------------------------------------------------------------------------------------------
ID     | CÓDIGO     | NOMBRE                     | CATEGORÍA       | PRECIO       | STOCK 
------------------------------------------------------------------------------------------------------
101    | LAP-001    | Laptop Dell XPS 15         | Computación     | $   1850.00 |     15
102    | MOU-002    | Mouse Logitech MX 3        | Accesorios      | $     99.99 |     40
103    | TEC-003    | Teclado Mecánico RGB       | Accesorios      | $    129.50 |     25
104    | MON-004    | Monitor 4K 27 Pulgadas     | Pantallas       | $    420.00 |     10
105    | AUR-005    | Audífonos Sony WH-1000XM5  | Audio           | $    349.99 |     50
------------------------------------------------------------------------------------------------------

=========================================================
  ¡DEMOSTRACIÓN RMI COMPLETADA CON ÉXITO SIN ERRORES!
=========================================================
```
*(Insertar aquí: Captura de pantalla de la salida completa del cliente)*

---

### 5.4. Paso 4: Registro de Invocaciones en la Consola del Servidor
Al mismo tiempo que el cliente realiza las solicitudes, el servidor registra en tiempo real cada invocación remota entrante recibida en su puerto TCP:

```text
[SERVIDOR - INVOCACIÓN REMOTA] listarProductos() solicitados. Total: 4
[SERVIDOR - INVOCACIÓN REMOTA] agregarProducto() -> [ID: 105 | Cod: AUR-005 | ...]
[SERVIDOR - INVOCACIÓN REMOTA] obtenerProductoPorId(105) -> Audífonos Sony WH-1000XM5
[SERVIDOR - INVOCACIÓN REMOTA] registrarVenta(ID: 105, Cantidad: 3)
[SERVIDOR - INVOCACIÓN REMOTA] registrarVenta(ID: 105, Cantidad: 999)
[SERVIDOR - INVOCACIÓN REMOTA] actualizarStock(ID: 105, NuevoStock: 50)
[SERVIDOR - INVOCACIÓN REMOTA] calcularValorTotalInventario() -> $56,686.60
[SERVIDOR - INVOCACIÓN REMOTA] listarProductos() solicitados. Total: 5
```
*(Insertar aquí: Captura de pantalla de la consola del servidor mostrando las llamadas reflejadas)*

---

### 5.5. Paso 5: Modo Interactivo por Menú de Consola
Se probó la herramienta interactiva `ejecutar-cliente-menu.bat`, permitiendo la captura de datos manuales y la interacción dinámica con el servidor:

```text
========== MENÚ DE GESTIÓN RMI ==========
1. Listar todos los productos
2. Registrar nuevo producto (enviar DTO)
3. Buscar producto por ID
4. Registrar venta (recibir OperacionDTO)
5. Actualizar existencias de stock
6. Calcular valor total de inventario
7. Salir
Seleccione una opción: 
```
*(Insertar aquí: Captura de pantalla interactuando con el menú y registrando un producto nuevo)*

---

## 6. DISCUSIÓN Y COMPARATIVA TÉCNICA

| Criterio | Sockets TCP / UDP | Java RMI (Remote Method Invocation) |
| :--- | :--- | :--- |
| **Nivel de Abstracción** | Bajo nivel (flujos continuos de bytes/strings). | Alto nivel (orientado a objetos, llamadas a métodos). |
| **Paso de Parámetros** | Requiere empaquetado/desempaquetado manual (ej. JSON, delimitadores por comas). | Automático y fuertemente tipado mediante `java.io.Serializable`. |
| **Transparencia de Red** | Baja. El cliente debe gestionar el ciclo de vida del socket explícitamente. | Alta. La invocación remota luce sintácticamente idéntica a una llamada local. |
| **Localización de Servicios**| Requiere conocer dirección IP fija y puerto estático de antemano. | Dinámica a través del servicio de nombres (`rmiregistry` y `lookup`). |
| **Manejo de Excepciones** | `IOException` genérico. | `RemoteException` tipificado con detalle del punto de fallo (red, marshaling, etc.). |

---

## 7. CONCLUSIONES

La realización de esta práctica permitió comprender en profundidad el funcionamiento interno del middleware **Java RMI** y su relevancia en el desarrollo de sistemas distribuidos empresariales. 

Se constató cómo el uso de interfaces remotas (`Remote`) y objetos serializables (`Serializable`) simplifica notablemente la programación cliente-servidor al eliminar la necesidad de diseñar protocolos textuales complejos sobre sockets. Asimismo, se comprendió la importancia crítica del `rmiregistry` como catálogo desacoplador y del archivo de políticas de seguridad para autorizar conexiones en entornos restringidos.

El sistema implementado resultó robusto, tolerante a entradas inválidas (validación de stock y cantidades negativas) y completamente compatible con versiones modernas de Java, cumpliendo con la totalidad de los requerimientos fijados para la práctica.

---

## 8. REFERENCIAS BIBLIOGRÁFICAS

1. **Oracle Corporation.** (2024). *Java Remote Method Invocation (RMI) Specification*. Oracle Java Documentation. https://docs.oracle.com/en/java/javase/
2. **Coulouris, G., Dollimore, J., Kindberg, T., & Blair, G.** (2012). *Distributed Systems: Concepts and Design* (5th ed.). Addison-Wesley.
3. **Tanenbaum, A. S., & Van Steen, M.** (2017). *Distributed Systems: Principles and Paradigms* (3rd ed.). CreateSpace Independent Publishing Platform.
4. **Deitel, P., & Deitel, H.** (2018). *Java: How to Program* (11th ed.). Pearson.

---

**Repositorio del Proyecto con Código Fuente Completo:**  
👉 **[https://github.com/Rositapro/RMI](https://github.com/Rositapro/RMI)**
