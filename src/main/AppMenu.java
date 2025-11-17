package main;

/**
 * Componente Orquestador de la aplicación (Application Orchestrator).
 *
 * ROL: Gestiona el ciclo de vida de la aplicación y el flujo principal de ejecución.
 *
 * RESPONSABILIDADES:
 * 1.  **Ciclo de Vida:** Inicializar y mantener el bucle principal (Main Loop) hasta que el usuario decida salir.
 * 2.  **Coordinación:** Actuar como intermediario de alto nivel entre la Vista ({@link MenuDisplay}) 
 * y el Controlador ({@link MenuHandler}).
 * 3.  **Manejo de Errores:** Centralizar la captura de excepciones (Global Exception Handling) 
 * provenientes de las capas inferiores (Service/DAO) para evitar el cierre abrupto de la aplicación.
 */
public class AppMenu {
    // Dependencias de la capa de presentación (Vista y Controlador)
    private final MenuHandler handler;
    private final MenuDisplay display;
 
    /**
     * Constructor para la inyección de dependencias.
     * * @param handler El componente controlador encargado de procesar la lógica de entrada.
     * @param display El componente de vista encargado de la salida por consola.
     */
    public AppMenu(MenuHandler handler, MenuDisplay display) {
        this.handler = handler;
        this.display = display;
    }

    /**
     * Ejecuta el bucle principal de la aplicación.
     * Mantiene el programa en ejecución procesando secuencialmente la visualización,
     * lectura y ejecución de comandos.
     */
    public void iniciar() {
        boolean salir = false;
        
        while (!salir) {
            // 1. Delegación a la Vista: Renderizar opciones del menú
            display.mostrarMenuPrincipal();
            // 2. Delegación al Controlador: Captura de input de usuario
            int opcion = handler.leerOpcion();

            try {
                // 3. Ejecución de lógica de control
                // El método retorna 'true' si se seleccionó la opción de salida (0)
                salir = handler.procesarOpcion(opcion);
                
            } catch (Exception e) {
                /// 4. Manejo Centralizado de Excepciones
                // Captura cualquier error de negocio (Service) o de persistencia (DAO)
                // y delega su visualización amigable a la Vista, manteniendo el bucle activo.
                display.mostrarError(e.getMessage());
            }
        }
        
        // Finalización y limpieza de recursos
        handler.cerrarScanner();
        System.out.println("Saliendo del sistema...");
    }
}