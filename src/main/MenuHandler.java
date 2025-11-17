package main;

import entities.Duenio;
import entities.Mascota;
import entities.Microchip;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import service.DuenioService;
import service.MascotaService;
import service.MicrochipService;
import java.util.List;
import java.util.Scanner;

/**
 * Componente Controlador (Controller) de la interfaz de usuario.
 *
 * ROL: Gestiona la interacción entre el usuario y la lógica de negocio (Services).
 *
 * RESPONSABILIDADES:
 * 1.  Capturar y validar la entrada del usuario (Input Handling).
 * 2.  Invocar a los métodos apropiados de la Capa de Servicio.
 * 3.  Coordinar el flujo de la aplicación (navegación del menú).
 * 4.  Delegar la visualización de resultados a la clase {@link MenuDisplay}.
 */
public class MenuHandler {
   // Dependencias (Servicios y Utilidades)
    private final Scanner scanner;
    private final MenuDisplay display; 
    
    private final DuenioService duenioService;
    private final MascotaService mascotaService;
    private final MicrochipService microchipService;

    /**
     * Constructor para la inyección de dependencias.
     * @param scanner Objeto Scanner para lectura de entrada estándar.
     * @param display Componente de visualización (Vista).
     * @param duenioService Servicio de gestión de Dueños.
     * @param mascotaService Servicio de gestión de Mascotas.
     * @param microchipService Servicio de gestión de Microchips.
     */
    public MenuHandler(Scanner scanner, MenuDisplay display, 
                       DuenioService duenioService, MascotaService mascotaService, 
                       MicrochipService microchipService) {
        this.scanner = scanner;
        this.display = display;
        this.duenioService = duenioService;
        this.mascotaService = mascotaService;
        this.microchipService = microchipService;
    }

   /**
     * Lee y parsea la opción numérica ingresada por el usuario.
     * @return La opción como entero, o -1 si la entrada no es válida.
     */
    public int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Devuelve una opción inválida
        }
    }

    /**
     * Libera los recursos del Scanner.
     */
    public void cerrarScanner() {
        scanner.close();
    }

    /**
     * Procesa la opción seleccionada en el menú principal.
     * Actúa como un despachador  de comandos.
     *
     * @param opcion El número de opción seleccionado.
     * @return true si se debe salir de la aplicación, false en caso contrario.
     * @throws Exception Propaga excepciones de la capa de servicio para ser manejadas en AppMenu.
     */
    public boolean procesarOpcion(int opcion) throws Exception {
        switch (opcion) {
            // Gestión de Dueños
            case 1 -> crearDuenio();
            case 2 -> listarDuenios();
            case 3 -> buscarDuenioPorDni();
            case 4 -> buscarDuenioPorEmail();
            case 5 -> buscarDuenioPorApellido();
            case 6 -> actualizarDuenio();
            case 7 -> eliminarDuenio();
           // Gestión de Mascotas
            case 8 -> crearMascota();
            case 9 -> listarMascotas();
            case 10 -> listarMascotasPorDuenio();
            case 11 -> eliminarMascota();
            case 0 -> {
                return true; // Salir
            }
            default -> display.mostrarError("Opción no válida. Por favor, intente de nuevo.");
        }
        
        return false; // Continuar ejecución
    }

// --- MÉTODOS PRIVADOS DE OPERACIÓN  ---

    // --- GESTIÓN DE DUEÑOS ---

    private void crearDuenio() throws Exception {
        System.out.println("\n--- 1. Crear Nuevo Dueño ---");
        // Captura de datos
        System.out.print("Ingrese DNI: ");
        String dni = scanner.nextLine().trim();
        System.out.print("Ingrese Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Ingrese Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Ingrese Email (opcional): ");
        String email = scanner.nextLine().trim();
        System.out.print("Ingrese Teléfono (opcional): ");
        String telefono = scanner.nextLine().trim();
        System.out.print("Ingrese Dirección (opcional): ");
        String direccion = scanner.nextLine().trim();

        // Construcción del objeto (DTO)
        Duenio nuevoDuenio = new Duenio();
        nuevoDuenio.setDni(dni);
        nuevoDuenio.setNombre(nombre);
        nuevoDuenio.setApellido(apellido);
        nuevoDuenio.setEmail(email);
        // Manejo de campos opcionales (null si están vacíos)
        nuevoDuenio.setTelefono(telefono.isEmpty() ? null : telefono);
        nuevoDuenio.setDireccion(direccion.isEmpty() ? null : direccion);
        
        // Invocación al Servicio
        Duenio duenioCreado = duenioService.insertar(nuevoDuenio);
        
        display.mostrarExito("Dueño creado exitosamente con ID: " + duenioCreado.getId());
    }

    private void listarDuenios() throws Exception {
        System.out.println("\n--- 2. Listar Todos los Dueños ---");
        List<Duenio> duenios = duenioService.getAll();
        display.mostrarDuenios(duenios);
    }
    
    private void buscarDuenioPorDni() throws Exception {
        System.out.println("\n--- 3. Buscar Dueño por DNI ---");
        System.out.print("Ingrese DNI a buscar: ");
        String dni = scanner.nextLine().trim();
        
        Duenio duenio = duenioService.buscarPorDni(dni);
        
        if (duenio == null) {
            display.mostrarError("No se encontró ningún dueño con el DNI: " + dni);
        } else {
            // Reutilizamos el método de 'mostrarDuenios'
            display.mostrarDuenios(List.of(duenio));
        }
    }
    private void buscarDuenioPorEmail() throws Exception {
        System.out.println("\n--- 4. Buscar Dueño por Email ---");
        System.out.print("Ingrese Email a buscar: ");
        String email = scanner.nextLine().trim();
        
        Duenio duenio = duenioService.buscarPorEmail(email);
        
        if (duenio == null) {
            display.mostrarError("No se encontró ningún dueño con el email: " + email);
        } else {
            display.mostrarDuenios(List.of(duenio));
        }
    }
    
    private void buscarDuenioPorApellido() throws Exception {
        System.out.println("\n--- 5. Buscar Dueños por Apellido ---");
        System.out.print("Ingrese Apellido a buscar: ");
        String apellido = scanner.nextLine().trim();
        
        List<Duenio> resultados = duenioService.buscarPorApellido(apellido);
        display.mostrarDuenios(resultados);
    }

    private void actualizarDuenio() throws Exception {
        System.out.println("\n--- 6. Actualizar Dueño ---");
        System.out.print("Ingrese el ID del dueño a actualizar: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        // 1. Recuperar estado actual
        Duenio duenio = duenioService.getById(id);
        if (duenio == null) {
            throw new Exception("No se encontró un dueño con ID: " + id);
        }

        System.out.println("Datos actuales: " + duenio.getNombre() + " " + duenio.getApellido());
        System.out.println("(Deje en blanco y presione Enter para no cambiar un campo)");

       // 2. Captura de cambios (Patrón de actualización parcial)
        System.out.print("Nuevo DNI (" + duenio.getDni() + "): ");
        String dni = scanner.nextLine().trim();
        if (!dni.isEmpty()) duenio.setDni(dni);

        System.out.print("Nuevo Nombre (" + duenio.getNombre() + "): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) duenio.setNombre(nombre);
        
        System.out.print("Nuevo Apellido (" + duenio.getApellido() + "): ");
        String apellido = scanner.nextLine().trim();
        if (!apellido.isEmpty()) duenio.setApellido(apellido);
        
        System.out.print("Nuevo Télefono (" + duenio.getTelefono() + "): ");
        String telefono = scanner.nextLine().trim();
        if (!telefono.isEmpty()) duenio.setTelefono(telefono);
        
        System.out.print("Nuevo Email (" + duenio.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) duenio.setEmail(email);
        
        System.out.print("Nueva Direccion (" + duenio.getDireccion() + "): ");
        String direccion = scanner.nextLine().trim();
        if (!direccion.isEmpty()) duenio.setDireccion(direccion);

        /// 3. Persistencia de cambios
        duenioService.actualizar(duenio);
        display.mostrarExito("Dueño ID " + id + " actualizado correctamente.");
    }

    private void eliminarDuenio() throws Exception {
        System.out.println("\n--- 7. Eliminar Dueño (Baja Lógica) ---");
        System.out.print("Ingrese el ID del dueño a eliminar: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        System.out.print("¿Está seguro que desea eliminar al dueño ID " + id + "? (s/n): ");
        String confirmacion = scanner.nextLine().trim();
        
        if (confirmacion.equalsIgnoreCase("s")) {
            // El Service validará la integridad referencial (RN-008)
            duenioService.eliminar(id);
            display.mostrarExito("Dueño ID " + id + " dado de baja.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
    
// --- GESTIÓN DE MASCOTAS (TRANSACCIONAL) ---

    private void crearMascota() throws Exception {
        System.out.println("\n--- 8. Registrar Mascota (con Microchip) ---");
        // Paso 1: Selección del Dueño
        System.out.print("Ingrese el ID del Dueño de la mascota: ");
        Long duenioId = Long.parseLong(scanner.nextLine().trim());
        // Validación previa de existencia
        Duenio duenio = duenioService.getById(duenioId);
        if (duenio == null) {
            throw new Exception("Error: No se encontró un dueño con ID: " + duenioId + ". No se puede crear la mascota.");
        }
        System.out.println("Dueño encontrado: " + duenio.getNombre() + " " + duenio.getApellido());
        // Paso 2: Datos de la Mascota
        System.out.print("Nombre de la mascota: ");
        String nombreMascota = scanner.nextLine().trim();
        System.out.print("Especie: ");
        String especie = scanner.nextLine().trim();
        System.out.print("Raza (opcional): ");
        String raza = scanner.nextLine().trim();
        LocalDate fechaNacimiento = pedirFecha("Ingrese Fecha de Nacimiento (AAAA-MM-DD) [Opcional]: ");
        
        Mascota nuevaMascota = new Mascota();
        nuevaMascota.setDuenio(duenio); 
        nuevaMascota.setNombre(nombreMascota);
        nuevaMascota.setEspecie(especie);
        nuevaMascota.setRaza(raza.isEmpty() ? null : raza);
        nuevaMascota.setFechaNacimiento(fechaNacimiento);nuevaMascota.setFechaNacimiento(fechaNacimiento);

        // Paso 3: Datos del Microchip
        System.out.print("Ingrese Código del Microchip (obligatorio): ");
        String codigoChip = scanner.nextLine().trim();
        System.out.print("Ingrese Veterinaria (opcional): ");
        String veterinaria = scanner.nextLine().trim();
        
        Microchip nuevoMicrochip = new Microchip();
        nuevoMicrochip.setCodigo(codigoChip);
        nuevoMicrochip.setVeterinaria(veterinaria.isEmpty() ? null : veterinaria);
        
        // Paso 4: Ejecución Transaccional
        Mascota mascotaCreada = mascotaService.crearMascotaCompleta(nuevaMascota, nuevoMicrochip);
        
        display.mostrarExito("Mascota creada con ID: " + mascotaCreada.getId() + 
                             " y Microchip ID: " + mascotaCreada.getMicrochip().getId());
    }

    private void listarMascotas() throws Exception {
        System.out.println("\n--- 9. Listar Todas las Mascotas ---");
        List<Mascota> mascotas = mascotaService.getAll();
        display.mostrarMascotas(mascotas);
    }
    
    private void listarMascotasPorDuenio() throws Exception {
        System.out.println("\n--- 10. Listar Mascotas por Dueño ---");
        System.out.print("Ingrese el ID del Dueño: ");
        Long duenioId = Long.parseLong(scanner.nextLine().trim());
        
        Duenio duenio = duenioService.getById(duenioId);
        if (duenio == null) {
            throw new Exception("No se encontró un dueño con ID: " + duenioId);
        }
        
        System.out.println("Mostrando mascotas de: " + duenio.getNombre() + " " + duenio.getApellido());
        List<Mascota> mascotas = mascotaService.buscarPorDuenioId(duenioId);
        display.mostrarMascotas(mascotas);
    }

    private void eliminarMascota() throws Exception {
        System.out.println("\n--- 11. Eliminar Mascota (Baja Lógica en Cascada) ---");
        System.out.print("Ingrese el ID de la mascota a eliminar: ");
        Long mascotaId = Long.parseLong(scanner.nextLine().trim());
        
        Mascota mascota = mascotaService.getById(mascotaId);
        if (mascota == null) {
            throw new Exception("No se encontró una mascota con ID: " + mascotaId);
        }

        System.out.print("¿Está seguro que desea eliminar a '" + mascota.getNombre() + 
                         "' (y su chip asociado)? (s/n): ");
        String confirmacion = scanner.nextLine().trim();
        
        if (confirmacion.equalsIgnoreCase("s")) {
            // Ejecuta la baja lógica transaccional
            mascotaService.eliminar(mascotaId);
            display.mostrarExito("Mascota ID " + mascotaId + " y su microchip han sido dados de baja.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
    
    /**
     * Solicita y parsea una fecha ingresada por el usuario.
     * Formato esperado: YYYY-MM-DD (Estándar ISO-8601).
     * @param mensaje El mensaje a mostrar al usuario.
     * @return LocalDate con la fecha, o null si se deja vacío o hay error.
     */
    private LocalDate pedirFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null; // Es opcional, devolvemos null
            }

            try {
                // Intenta convertir el String a LocalDate
                return LocalDate.parse(input); 
            } catch (DateTimeParseException e) {
                System.out.println(">>> Error: Formato de fecha inválido. Use AAAA-MM-DD (ej. 2023-05-20).");
                // El bucle while hace que vuelva a preguntar
            }
        }
    }
}
