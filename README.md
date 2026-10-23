# MCmovil Care - Seguimiento móvil del ciclo de vida de equipos

MVP académico Android (Jetpack Compose) desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles** (Duoc UC, Evaluación Parcial 2).

MCmovil Care permite a un cliente de MCmovil SpA consultar sus equipos, órdenes de trabajo, garantías y mantenciones, responder cotizaciones y registrar solicitudes de atención desde un único canal móvil. Todos los datos son **ficticios**: la aplicación no se conecta a Gestioo ni a ningún sistema productivo.

## Integrantes
- Lucas Sepúlveda
- Valentín Salfate

## Planificación
Tablero de Trello (distribución de tareas): 
https://trello.com/invite/b/6ad2895b6d80d6761d07c6fa/ATTIc305d1cbd3292bf78106b5603006313d904E3957/appmcmovilcaregrupo6

## Funcionalidades implementadas
- **Registro** con validación por campo: nombre, correo, teléfono, clave, repetir clave y aceptación de términos.
- **Inicio de sesión** con botón de carga y mensaje de error animado. La **sesión se recuerda** al cerrar y reabrir la app.
- **Home** con `TopAppBar` y `NavigationBar` (Equipos, Solicitudes y Perfil) y transición animada entre pestañas.
- **Equipos del cliente**: lista adaptable al ancho de pantalla, con garantía vigente o vencida.
- **Detalle del equipo**: datos, órdenes de trabajo y mantenciones (próximas y realizadas).
- **Orden de trabajo**: barra de progreso animada según el estado y **autorización de cotización** (aprobar, o rechazar con motivo obligatorio).
- **Solicitud de atención**: formulario con equipo, tipo de servicio, prioridad y descripción; historial **persistente** y cancelación de solicitudes enviadas.
- **Perfil**: foto desde **cámara** o **galería**, guardada por usuario.
- **Transiciones animadas** entre pantallas (deslizamiento y desvanecimiento).

## Reglas de negocio
- Cada cliente ve únicamente sus propios equipos.
- La garantía está vigente mientras su fecha de término no haya pasado.
- La cotización se responde una sola vez y solo si la orden está en estado "Cotizado".
- Rechazar una cotización exige un motivo de al menos 10 caracteres.
- Toda solicitud nueva nace en estado "Enviada" y solo puede cancelarse mientras siga así.
- El correo de un usuario registrado no puede repetirse.

## Arquitectura (MVVM)
```
app/src/main/java/com/example/mcmovilcare/
├── model/        data classes y estados de formularios (UiState + Errores)
├── data/         PreferenciasDataStore (persistencia local) y SolicitudMapper (JSON)
├── repository/   UsuarioRepository, EquipoRepository, SolicitudRepository
├── viewmodels/   estado y validaciones con StateFlow
├── navigation/   rutas (Screen) y eventos de navegación
├── screens/      pantallas Compose
│   └── components/   CampoFormulario, SelectorChips, ImagenInteligente, BotonConLoader
├── ui/theme/     tema Material 3
└── utils/        archivos de cámara, lectura de imágenes y fechas
```
- **Model**: datos del dominio y estado de cada formulario.
- **ViewModel**: guarda el estado, valida los formularios y expone `StateFlow`.
- **Repository**: único acceso a los datos y sus reglas de negocio.
- **Screens/Composables**: solo muestran el estado y notifican los eventos; no contienen reglas de validación.

## Persistencia local
Se usa **DataStore Preferences**, que guarda: el usuario registrado, la sesión activa, la foto de perfil de cada usuario y las solicitudes de atención (convertidas a JSON con `org.json`).

## Recursos nativos
1. **Cámara**: `ActivityResultContracts.TakePicture` con permiso `CAMERA` solicitado en tiempo de ejecución y `FileProvider`. Si el permiso se deniega, se muestra un mensaje.
2. **Galería**: `ActivityResultContracts.GetContent` (selector del sistema, no requiere permiso).

La imagen se copia al almacenamiento interno de la app, se corrige su orientación y se muestra en un contenedor circular.

## Cómo ejecutar
1. Clonar el repositorio y abrir la carpeta con **Android Studio**.
2. Esperar el *Gradle Sync*. La primera vez puede descargar el JDK que pide el proyecto, por lo que se requiere conexión a internet.
3. Ejecutar (`Run`) en un emulador o dispositivo Android (minSdk 24). Para probar la cámara se recomienda un dispositivo físico.
4. Usuarios de prueba (ficticios):
   - `cafeeee@mcmovil.com` / `cafeee1234`
   - `cafe@mcmovil.com` / `cream-coffee`
   - O crear una cuenta nueva desde **Crear cuenta**.

## Tecnologías
Kotlin 2.2.10 · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · DataStore Preferences · Coroutines · Gradle 9.6 · AGP 9.4.1.

## Alcance y trabajo futuro
Este MVP corresponde a un avance intermedio. Para la EFT se contempla consumir una API REST de prueba con Retrofit, persistir las respuestas a cotizaciones y agregar los perfiles Técnico y Administrador.
