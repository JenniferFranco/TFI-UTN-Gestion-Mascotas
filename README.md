# 🐾 Sistema de Gestión Veterinaria (TFI)

**Trabajo Final Integrador** | Programación 2 | Tecnicatura Universitaria en Programación (UTN)

Este repositorio contiene el código fuente de una aplicación de consola en **Java** para la gestión de una clínica veterinaria. El sistema implementa un CRUD completo y maneja transacciones complejas contra una base de datos **MySQL** utilizando **JDBC** puro y el patrón de diseño **DAO**.

---

## 📋 Descripción del Dominio

El sistema modela la relación entre tres entidades principales:
1.  **Dueño:** El cliente de la veterinaria.
2.  **Mascota:** La entidad principal.
3.  **Microchip:** Identificación única de la mascota.

### Relaciones del Modelo
* **1 a Muchos:** Un Dueño tiene muchas Mascotas.
* **1 a 1 (Unidireccional):** Una Mascota tiene un único Microchip.
*_(La clase Mascota referencia a Microchip, pero Microchip no conoce a Mascota)._*

---

## 🚀 Características Técnicas

* **Arquitectura en Capas:** (Main, Service, DAO, Entities).
* **Patrón DAO:** Desacoplamiento total entre la lógica y el SQL.
* **Transacciones ACID:** Gestión manual de `commit` y `rollback` para asegurar la integridad al crear Mascota y Microchip simultáneamente.
* **Connection Pooling:** Uso de **HikariCP** para optimizar conexiones.
* **Baja Lógica:** Implementación de *Soft Delete* (`eliminado = true`) en todas las tablas.
* **Integridad Referencial Lógica:** Validaciones de negocio previas a la eliminación (ej. no borrar dueño con mascotas activas).

---

## 🛠️ Tecnologías Utilizadas

* Java **JDK 21**
* **MySQL 8.0**
* **HikariCP**
* `mysql-connector-j-8.4.0.jar`
* **SLF4J**
* IDE recomendado: **Apache NetBeans**
* Herramientas recomendadas: **DBeaver / MySQL Workbench**

---

## ⚙️ Instalación y Configuración

### 1. Crear la Base de Datos

- Creamos la base de datos si no existe
CREATE DATABASE IF NOT EXISTS gestion_mascota;
USE gestion_mascota;

-- BLOQUE DE IDEMPOTENCIA
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS duenio;
DROP TABLE IF EXISTS microchip;
SET FOREIGN_KEY_CHECKS = 1;

-- DUENIOS (Tabla independiente)
CREATE TABLE duenios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(120) UNIQUE,
    direccion VARCHAR (50), 
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT chk_email CHECK (email LIKE '%@%'),
    CONSTRAINT chk_telefono CHECK (LENGTH(telefono) >= 7) 
);

-- MASCOTA (Depende de Duenio)
-- Esta es 'A' en la relación A->B
CREATE TABLE mascotas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(60)  NOT NULL,
    especie VARCHAR(30) NOT NULL,
    raza VARCHAR(60),
    fecha_nacimiento DATE,
    duenio_id BIGINT NOT NULL,
    
    -- RELACIONES 
    CONSTRAINT fk_mascota_duenio
        FOREIGN KEY (duenio_id) REFERENCES duenios(id),
        
    -- RESTRICCIONES
    CONSTRAINT chk_mascota_nombre CHECK (TRIM(nombre) <> '')
);

-- MICROCHIP (Depende de Mascota)
-- Esta es 'B' en la relación A->B
CREATE TABLE microchips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    codigo VARCHAR(25) NOT NULL UNIQUE, 
    observaciones TEXT,
    veterinaria VARCHAR(120),
    mascota_id BIGINT NOT NULL UNIQUE, 

    -- RELACIONES
    CONSTRAINT fk_microchip_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascotas(id),

    -- RESTRICCIONES
    CONSTRAINT chk_microchip_codigo CHECK (TRIM(codigo) <> '')
);
    
### 2. Configurar la Conexión a MySQL

Editar el archivo:

*src/config/DatabaseConnectionPool.java*

Y reemplazar:
config.setJdbcUrl("jdbc:mysql://localhost:3306/gestion_mascota");
config.setUsername("TU_USUARIO");
config.setPassword("TU_PASSWORD");

##🏗️ Estructura del Proyecto
* **`src/config/`**:
    * `DatabaseConnectionPool.java`: Configuración de la base de datos (HikariCP).
* **`src/entities/`**:
    * `Duenio.java`, `Mascota.java`, `Microchip.java`: Clases del modelo de datos.
* **`src/dao/`**:
    * `GenericDAO.java`: Interfaz base.
    * `DuenioDAO.java`, `MascotaDAO.java`, `MicrochipDAO.java`: Interfaces específicas.
    * `impl/`: Contiene las implementaciones (`DuenioDaoImpl`, etc.).
* **`src/service/`**:
    * `DuenioService.java`, `MascotaService.java`, `MicrochipService.java`: Interfaces de negocio.
    * `impl/`: Contiene la lógica de negocio y transacciones (`MascotaServiceImpl`, etc.).
* **`src/main/`**:
    * `Main.java`: Punto de entrada.
    * `MenuHandler.java`: Controlador de la consola.
    * `MenuDisplay.java`: Vista de la consola.
---
       
## ▶️ Ejecución del Programa

Para iniciar la aplicación, ejecuta el archivo principal desde tu IDE:

`src/main/Main.java`

### Funciones disponibles:
* ✅ **CRUD de Dueños** (Crear, Leer, Actualizar, Eliminar)
* ✅ **CRUD de Mascotas**
* ✅ **CRUD de Microchips** (gestionado internamente)
* ✅ **Creación Transaccional:** Mascota + Microchip (Atomicidad garantizada)
* ✅ **Búsquedas y Listados:** Por ID, DNI, Apellido, etc.
* ✅ **Baja Lógica:** Implementación de Soft Delete.

---

## 🧪 Pruebas Incluidas

* Creación exitosa de Mascota + Microchip (commit)
* Error por microchip duplicado → rollback
* Consultas SQL en DBeaver para validar registros
* Verificación del soft delete

## 👥 Integrantes del Equipo

| Integrante | Rol |
| :--- | :--- |
| **Jennifer Franco** | Desarrollo – Diseño UML – Informe |
| **Jonathan Franco** | Desarrollo – Pruebas – Base de Datos |

---
**Ciclo Lectivo: 2025** | **Materia: Programación 2 – UTN**
Y reemplazar:    Mascota "1" --> "1" Microchip : identifica
