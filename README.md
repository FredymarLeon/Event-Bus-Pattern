# Event Bus Pattern 📱

Una aplicación Android educativa que implementa el **patrón Event Bus** en Kotlin.
La aplicación muestra un listado de eventos deportivos con sus resultados y simula una campaña
publicitaria interactiva.

## 📋 Características

- **Listado de Eventos**: RecyclerView que muestra eventos deportivos con información completa:
  - Nombre del deporte
  - Resultados y rankings
  - Icono representativo del deporte
  - Avisos y advertencias

- **Pull to Refresh**: Swipe down para recargar la lista de eventos

- **Botón de Anuncio Publicitario**: Botón ubicado en la parte inferior que simula la visualización
de publicidad

- **Arquitectura limpia**: Implementación del patrón Event Bus para comunicación entre componentes

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin
- **Versión Android mínima**: 24
- **Versión Android objetivo**: 37
- **Java**: Version 11

### Dependencias principales

- AndroidX Activity KTX
- AndroidX AppCompat
- AndroidX ConstraintLayout
- AndroidX Core KTX
- AndroidX SwipeRefreshLayout
- Material Components

## 📁 Estructura del Proyecto

```
EventBusPattern/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fredymarleon/eventbuspattern/
│   │   │   │   ├── MainActivity.kt                          # Actividad principal (UI)
│   │   │   │   ├── eventsBus/
│   │   │   │   │   ├── EventBus.kt                          # Patrón Singleton + Observable (MutableSharedFlow)
│   │   │   │   │   └── SportEvent.kt                        # Sealed class con eventos tipados
│   │   │   │   ├── services/
│   │   │   │   │   └── SportService.kt                      # Singleton que emite y se suscribe a eventos
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── ResultAdapter.kt                     # RecyclerView Adapter con método add()
│   │   │   │   │   └── OnClickListener.kt                   # Interfaz para clicks de items
│   │   │   │   └── dataAcces/
│   │   │   │       └── DataBase.kt                          # Simulación de datos (eventos en tiempo real)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml                    # Layout principal con RecyclerView y botón
│   │   │   │   │   └── item_event.xml                       # Item del listado
│   │   │   │   ├── drawable/                                # Iconos de deportes
│   │   │   │   └── values/
│   │   │   │       └── strings.xml                          # Recursos de string
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                            # Pruebas unitarias
│   │   └── androidTest/                                     # Pruebas instrumentadas
│   └── build.gradle.kts
└── build.gradle.kts
```

### Componentes clave del patrón Event Bus

#### 1. **EventBus (Singleton + Observable)**

Ubicado en `eventsBus/EventBus.kt`, es el corazón del patrón:

- **Singleton**: Implementado con `companion object` y `by lazy`, garantiza una única instancia accesible
desde cualquier lugar mediante `EventBus.instance()`.
- **Observable**: Usa `MutableSharedFlow<Any>` (Flow de Kotlin Coroutines) para permitir múltiples suscriptores.
- **Publicación**: Método `publishEvent(event: Any)` emite eventos de forma suspendida (async).
- **Suscripción**: Método `subscribeToEvents<T>()` permite suscribirse a eventos específicos usando genéricos reificados.

#### 2. **SportEvent (Eventos Tipados)**

Ubicado en `eventsBus/SportEvent.kt`, es una sealed class que define todos los eventos posibles:

- `ResultSuccess`: Evento cuando se obtienen resultados de un deporte.
- `ResultError`: Evento cuando ocurre un error en la obtención de datos.
- `AdEvent`: Evento para mostrar publicidad.
- `SaveEvent`: Evento cuando se guardan datos correctamente.
- `CloseAdEvent`: Evento cuando se cierra la publicidad.

#### 3. **SportService (Suscriptor y Emisor)**

Ubicado en `services/SportService.kt`, también es un Singleton que:

- Se suscribe a eventos del Event Bus mediante `EventBus.instance().subscribeToEvents<SportEvent>()`.
- Emite eventos al Event Bus usando `EventBus.instance().publishEvent()`.
- Maneja la lógica de negocio (p. ej., guardar resultados, manejar errores, procesar anuncios).

#### 4. **ResultAdapter (Adaptador del RecyclerView)**

Ubicado en `adapters/ResultAdapter.kt`:

- Mantiene una lista interna de `SportEvent.ResultSuccess`.
- Método `add()`: Añade nuevos eventos a la lista y notifica al RecyclerView con `notifyItemInserted()`.
- Método `clear()`: Limpia todos los elementos.
- Implementa `OnClickListener` para manejar clicks de items.
- En `onBindViewHolder()`: Mapea los datos del evento a las vistas (nombre, resultados, imagen, warning).

#### 5. **DataBase (Simulación de Datos)**

Ubicado en `dataAcces/DataBase.kt`:

- `getResultEventsInRealtime()`: Retorna una lista simulada de eventos deportivos.
- `getAdEventsInRealtime()`: Retorna eventos de publicidad.
- `someTime()`: Simula latencia de red con un delay aleatorio.

### Flujo de comunicación

```
MainActivity
    ↓
[Inicia] → SportService.setupSubscribers()
    ↓
[Listener de clicks] → ResultAdapter.onClick()
    ↓
EventBus.instance().publishEvent(SportEvent.*)
    ↓
SportService (suscriptor) recibe el evento
    ↓
[Según el tipo de evento] → Ejecuta lógica de negocio
    ↓
[Si aplica] → Emite nuevo evento → Actualiza UI
```

Este patrón permite:
- **Desacoplamiento**: MainActivity no necesita conocer los detalles de SportService.
- **Reusabilidad**: Cualquier componente puede suscribirse al Event Bus.
- **Testabilidad**: Es fácil mockear el Event Bus en pruebas unitarias.
- **Escalabilidad**: Nuevos eventos y suscriptores pueden agregarse sin modificar código existente.

## 🎨 Interfaz de Usuario

### Pantalla Principal

La pantalla principal contiene:

1. **SwipeRefreshLayout** con RecyclerView:
   - Lista de eventos deportivos
   - Soporte para pull-to-refresh
   - Cada item muestra información detallada del evento

2. **Botón de Anuncio** (Parte inferior):
   - Ocupa todo el ancho de la pantalla
   - Texto: "Simular Anuncio Publicitario"
   - Permite simular la visualización de publicidad

### Detalle del item (`item_event.xml`)

Cada fila del listado representa un evento deportivo y contiene los siguientes elementos (tal como están
implementados en `app/src/main/res/layout/item_event.xml`):

- Imagen del icono (`ImageView`): icono representativo del deporte.
- Texto de advertencia (`TextView` - `tvWarning`): usado para mostrar un warning o mensaje adicional;
por defecto está `gone` y se activa según la lógica del adaptador.
- Texto del nombre del deporte (`TextView` - `tvSport`).
- Texto con los tres primeros lugares (`TextView` - `tvResults`): muestra en varias líneas los 1º, 2º y 3º puestos.
- Divider (`MaterialDivider`): separador visual al final del item.

Estos elementos permiten representar de forma compacta la información más relevante del evento.

### Adaptador (RecyclerView.Adapter)

En el adaptador del RecyclerView se realiza la asignación de datos en `onBindViewHolder` de la siguiente manera:

- Se extraen los resultados del modelo (por ejemplo una lista o un objeto `Event`) y se asignan a `tvSport`, `tvResults`, y `imgSport`.
- Si el evento contiene una advertencia, se muestra `tvWarning` (cambiando su visibilidad y su texto); si no, se mantiene `gone`.

Ejemplos de responsabilidades del adaptador:

- onBindViewHolder(holder, position):
  - Obtener el item en `position`.
  - Mapear el icono al `ImageView` (por recursos o carga desde URL si aplica).
  - Formatear los tres primeros lugares y asignarlos a `tvResults` (p. ej. con saltos de línea).
  - Ajustar la visibilidad de `tvWarning` según corresponda.

- Método para añadir nuevos resultados:
  - El adaptador expone un método público (p. ej. `fun addResults(newItems: List<Event>)`) que inserta los elementos en la colección
  interna y notifica al RecyclerView (por ejemplo `notifyItemRangeInserted` o `notifyDataSetChanged` si la inserción es simple).
  - También es común exponer `fun addResult(item: Event)` para añadir un solo elemento.

- Evento de click:
  - El adaptador puede exponer un listener (lambda o interfaz) que se llama desde el `ViewHolder` cuando el usuario hace click en el item o en un subelemento.
  - Ejemplo: `var onItemClick: ((Event, Int) -> Unit)? = null` y en el `ViewHolder` `itemView.setOnClickListener { onItemClick?.invoke(event, adapterPosition) }`.

Estas operaciones permiten actualizar dinámicamente la UI y responder a la interacción del usuario (simular acciones publicitarias).

### Comunicación y eventos

Aunque el proyecto se titula "Event Bus Pattern", el adaptador y la actividad pueden comunicarse mediante:

- Llamadas directas (listeners) desde el adaptador hacia la Activity/Fragment mediante lambdas o interfaces.
- Publicación de eventos en el Event Bus (si está integrado) para notificar cambios globales (p. ej. refresco de datos,
apertura de un anuncio o tracking de interacción).

Es recomendable mantener la lógica de negocio fuera del adaptador: el adaptador solo debe mapear datos a vistas y reenviar eventos.

## 🚀 Cómo Ejecutar

### Requisitos previos
- SDK de Android 37+
- Gradle 8.x

### Pasos de instalación

1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza las dependencias de Gradle
4. Ejecuta la aplicación en un emulador o dispositivo físico

```bash
./gradlew assembleDebug
```

## 📚 Aprendizajes Clave

Este proyecto es parte de un curso sobre **arquitecturas Kotlin** y demuestra:

- Implementación del **patrón Event Bus** para comunicación entre componentes
- Uso de **RecyclerView** y **Adapters**
- Gestión de layouts con **ConstraintLayout**
- Implementación de **SwipeRefreshLayout**
- Buenas prácticas en desarrollo Android con Kotlin

## 👤 Estudiante

Fredymar León

## 📄 Licencia

Este proyecto es de propósito educativo.

---

¿Preguntas? Consulta la documentación de Android en [developer.android.com](https://developer.android.com)



