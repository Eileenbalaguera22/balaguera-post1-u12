# Sistema de Solicitudes de Reembolso de Gastos Corporativos

## 1. Descripción del proyecto

El proyecto consiste en desarrollar un sistema para gestionar las solicitudes de reembolso de gastos corporativos de los empleados de una empresa.

El sistema permite registrar solicitudes, validar la información, calcular el monto que puede ser aprobado según la categoría del gasto y notificar a las diferentes áreas cuando cambia el estado de una solicitud.

El desarrollo se realiza utilizando Java 17, Spring Boot y Maven, aplicando patrones de diseño para facilitar el mantenimiento y permitir futuras ampliaciones.

## 2. Tecnologías utilizadas

- Java 17.
- Spring Boot 3.x.
- Maven.
- Spring Web.
- Spring Data JPA.
- Base de datos H2.
- JUnit 5 para pruebas automatizadas.
- Git y GitHub para el control de versiones.

## 3. Diseño Propuesto

Antes de comenzar la implementación se analizaron los tres requisitos funcionales del sistema para identificar qué comportamientos pueden cambiar y qué patrones de diseño permiten resolverlos.

### RF1. Validación en varias etapas

**Problema identificado:**

Cada solicitud debe pasar por diferentes verificaciones antes de continuar con el proceso. Se debe comprobar que el monto cumpla los límites establecidos, que tenga los documentos soporte y que no exista otra solicitud pendiente del mismo empleado, categoría y período.

Además, Finanzas puede agregar nuevas verificaciones en el futuro.

**Punto de variabilidad:**

Las verificaciones que se deben realizar pueden aumentar o cambiar con el tiempo.

**Patrón seleccionado: Chain of Responsibility (Cadena de Responsabilidad).**

Se propone utilizar este patrón porque permite organizar las validaciones como una cadena de pasos independientes. Cada validador realiza su propia comprobación y, si la solicitud cumple, la pasa al siguiente.

**Justificación:**

Este patrón permite agregar nuevas validaciones sin tener que modificar las que ya existen, manteniendo cada comprobación separada y organizada.

### RF2. Cálculo diferente según la categoría del gasto

**Problema identificado:**

El sistema maneja tres categorías de gastos y cada una tiene una forma diferente de calcular el monto aprobado:

- VIAJE: aplica un tope diario multiplicado por los días del viaje, con un límite de $150.000 por día.
- REPRESENTACION: reembolsa el monto solicitado hasta un máximo mensual de $2.000.000.
- CAPACITACION: reembolsa el 80% del monto solicitado, hasta un máximo anual de $3.000.000.

Finanzas también planea agregar nuevas categorías en el futuro.

**Punto de variabilidad:**

La fórmula utilizada para calcular el monto aprobado cambia dependiendo de la categoría del gasto.

**Patrón seleccionado: Strategy (Estrategia).**

Se propone utilizar este patrón porque permite tener una calculadora independiente para cada categoría, manteniendo separadas las diferentes fórmulas.

**Justificación:**

Strategy permite agregar nuevas formas de cálculo sin modificar las estrategias que ya existen y facilita seleccionar la fórmula correspondiente según la categoría de la solicitud.

### RF3. Notificación simultánea a varios interesados

**Problema identificado:**

Cuando una solicitud cambia de estado, diferentes áreas deben recibir información, pero cada una tiene una reacción distinta.

- El empleado debe recibir un mensaje con el resultado de su solicitud.
- Contabilidad debe recibir las solicitudes aprobadas para programar el pago.
- Auditoría debe registrar todos los cambios de estado.

También existe la posibilidad de agregar nuevos interesados, como un panel interno de métricas.

**Punto de variabilidad:**

La cantidad de interesados puede aumentar y cada uno puede necesitar realizar una acción diferente cuando cambia el estado de una solicitud.

**Patrón seleccionado: Observer (Observador).**

Se propone utilizar este patrón porque permite que varios observadores reciban información cuando ocurre un cambio, sin que el servicio principal tenga que conocer los detalles de cada notificación.

**Justificación:**

Observer permite agregar nuevos interesados y sus propias acciones sin modificar directamente el proceso que realiza el cambio de estado.

---

## 4. Decisiones de diseño

### Decisión de diseño 1. Orden de los validadores

**Problema:**

Se debe definir el orden en que se ejecutan los validadores de la cadena, teniendo en cuenta que comprobar solicitudes duplicadas requiere comparar la solicitud con una lista de solicitudes pendientes.

**Decisión tomada:**

Se propone ejecutar los validadores en el siguiente orden:

1. ValidadorDocumentosSoporte.
2. ValidadorMontoMaximo.
3. ValidadorDuplicados.

**Justificación:**

Se eligió este orden porque primero se realizan las comprobaciones más sencillas y rápidas, dejando la búsqueda de duplicados para el final, ya que requiere comparar información de otras solicitudes.

**Alternativa considerada:**

Ejecutar primero la validación de duplicados. Sin embargo, esto podría generar comparaciones innecesarias cuando la solicitud no cumple requisitos anteriores, como tener los documentos soporte.

**Beneficio:**

Se evita realizar comprobaciones más costosas cuando la solicitud ya fue rechazada por una validación anterior.

**Complejidad adicional:**

Es necesario mantener el orden de los validadores al construir la cadena y asegurarse de que cada uno cumpla una responsabilidad específica.

### Decisión de diseño 2. Selección de la calculadora

**Problema:**

El sistema necesita seleccionar la calculadora correspondiente a la categoría del gasto. Además, Finanzas planea agregar nuevas categorías en el futuro.

**Alternativas consideradas:**

- Utilizar un `switch` manual para seleccionar la calculadora.
- Construir un mapa a partir de una lista de implementaciones de `CalculadoraReembolso`, inyectada automáticamente por Spring.

**Decisión tomada:**

Se propone utilizar un mapa construido a partir de `List<CalculadoraReembolso>`, donde cada calculadora se identifica mediante su categoría.

**Justificación:**

Se eligió esta alternativa porque permite incorporar una nueva categoría creando una nueva implementación de la interfaz, sin tener que modificar un `switch` central ni las calculadoras existentes.

**Beneficio:**

El sistema queda más preparado para crecer y permite mantener cada fórmula de cálculo independiente.

**Complejidad adicional:**

Es necesario registrar correctamente cada calculadora y asegurar que no existan dos implementaciones asociadas a la misma categoría.

---

## 5. Estructura del proyecto

El proyecto se organiza en paquetes para separar las responsabilidades:

```text
src/main/java/com/empresa/reembolsos/
│
├── ReembolsosApplication.java
│
├── dominio/
│   ├── SolicitudReembolso.java
│   ├── CategoriaGasto.java
│   └── EstadoSolicitud.java
│
├── procesamiento/
│   ├── validacion/
│   ├── calculo/
│   └── notificacion/
│
├── aplicacion/
│   └── SolicitudReembolsoService.java
│
├── infraestructura/
│   └── RepositorioSolicitudesJpa.java
│
└── adaptadores/
    └── rest/
        └── SolicitudReembolsoController.java
```

## 6. Requisitos de implementación

El sistema debe contar con:

- Validación de documentos soporte.
- Validación de los montos solicitados.
- Detección de solicitudes duplicadas.
- Cálculo del monto aprobado según la categoría.
- Notificación al empleado, Contabilidad y Auditoría.
- Registro y consulta de solicitudes.
- Actualización del estado de una solicitud.

## 7. Endpoints del sistema

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/solicitudes` | Registrar una solicitud de reembolso |
| PUT | `/api/solicitudes/{id}/estado` | Cambiar el estado de una solicitud |
| GET | `/api/solicitudes` | Consultar las solicitudes registradas |

## 8. Pruebas

Para verificar el funcionamiento del proyecto se deben realizar como mínimo:

**Pruebas unitarias:**

- Una prueba para ValidadorDocumentosSoporte.
- Una prueba para ValidadorMontoMaximo.
- Una prueba para ValidadorDuplicados.
- Una prueba para CalculadoraViaje.
- Una prueba para CalculadoraRepresentacion.
- Una prueba para CalculadoraCapacitacion.

**Prueba de integración:**

Una prueba con `@SpringBootTest` para comprobar que la aplicación puede iniciar correctamente y que sus componentes se integran.

Las pruebas se ejecutan mediante Maven:

```powershell
.\mvnw.cmd clean test
```

## 9. Ejecución del proyecto

Para ejecutar el proyecto desde la terminal de Visual Studio Code:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación utiliza el puerto 8080, salvo que se configure uno diferente.

Para ejecutar las pruebas:

```powershell
.\mvnw.cmd clean test
```

## 10. Control de versiones

El proyecto utiliza Git para registrar los avances mediante commits descriptivos y GitHub para almacenar el repositorio remoto.

Los commits se realizan de forma progresiva, siguiendo el orden de implementación establecido en el documento del proyecto integrador.