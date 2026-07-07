# AutoFinPe: Sistema de Gestión y Simulación de Crédito Vehicular

## Descripción del Proyecto

AutoFinPe es una plataforma integral para la gestión y simulación de créditos vehiculares. Permite a los usuarios administrar vehículos, clientes y configuraciones de crédito, así como simular operaciones de crédito y generar cronogramas de pago. El sistema está diseñado para ser utilizado por ejecutivos de ventas y administradores para facilitar el proceso de financiamiento de vehículos.

## Demo en línea
| Recurso |	Enlace |
|---------|--------|
| 🌐 Aplicación |	[https://feisty-youthfulness-production.up.railway.app/login](https://feisty-youthfulness-production.up.railway.app/login) |
| 📚 Swagger API |	[https://autofinpe-production.up.railway.app/api/swagger-ui/index.html](https://autofinpe-production.up.railway.app/api/swagger-ui/index.html) |
| 📄 OpenAPI |	[https://autofinpe-production.up.railway.app/api/v3/api-doc](https://autofinpe-production.up.railway.app/api/v3/api-doc) |

## Tecnologías Utilizadas

Este proyecto está construido como un monorepo, compuesto por un backend robusto y un frontend moderno.

### Backend (Java/Spring Boot)

-   **Lenguaje:** Java 17
-   **Framework:** Spring Boot 3.2.x
-   **Construcción:** Apache Maven
-   **Base de Datos:** MySQL
-   **ORM:** Spring Data JPA / Hibernate
-   **Seguridad:** Spring Security, JWT (Json Web Tokens) para autenticación y autorización
-   **Validación:** Bean Validation (Jakarta Validation)
-   **Documentación API:** Springdoc OpenAPI (Swagger UI)
-   **Generación de Reportes:** OpenPDF, Apache POI

### Frontend (React/TypeScript)

-   **Lenguaje:** TypeScript
-   **Framework:** React 19
-   **Empaquetador/Servidor:** Vite
-   **Gestor de Paquetes:** npm
-   **Routing:** React Router DOM
-   **Estilos:** CSS (con módulos o enfoques de utilidades)

### Otros

-   **Contenedorización:** Docker, Docker Compose
-   **Control de Versiones:** Git

## Arquitectura

El proyecto sigue una arquitectura de microservicios (simulada en un monorepo) con una clara separación entre el frontend y el backend:

-   **Frontend:** Una Single Page Application (SPA) desarrollada con React y TypeScript, que proporciona una interfaz de usuario interactiva y consume los servicios del backend a través de APIs REST.
-   **Backend:** Una aplicación RESTful API construida con Spring Boot, encargada de la lógica de negocio, la interacción con la base de datos MySQL y la seguridad (autenticación JWT).
-   **Base de Datos:** MySQL, utilizada para persistir todos los datos del sistema.

## Cómo Ejecutar el Proyecto

Puedes ejecutar el proyecto en modo desarrollo o usando Docker Compose.

### Requisitos Previos

-   Java Development Kit (JDK) 17 o superior
-   Apache Maven 3.x
-   Node.js (LTS)
-   npm (viene con Node.js)
-   Docker y Docker Compose (opcional, para ejecución contenedorizada)
-   Un cliente MySQL (opcional, para acceso directo a la DB)

### Ejecución en Modo Desarrollo

#### 1. Iniciar la Base de Datos MySQL (con Docker)

Es recomendable usar Docker Compose para la base de datos, incluso si ejecutas el backend y frontend localmente.

```bash
# Desde la raíz del proyecto
docker-compose up -d mysql
```

Esto iniciará el contenedor de MySQL en segundo plano. Los detalles de conexión estarán en el `docker-compose.yml` (puerto 3307 por defecto).

#### 2. Configurar Variables de Entorno

Copia los archivos `.env.example` en `backend/` y `frontend/` a `.env` respectivamente y ajusta las variables si es necesario.

-   **`backend/.env`**: Configuración de la base de datos (si no usas la de Docker Compose o la expones en otro puerto).
-   **`frontend/.env`**: URL base de la API (`VITE_API_BASE_URL`). Por defecto, `/api`.

#### 3. Iniciar el Backend

```bash
# Navega al directorio del backend
cd backend

# Ejecuta el proyecto Spring Boot
mvn spring-boot:run
```

El backend estará disponible en `http://localhost:8080` por defecto.

#### 4. Iniciar el Frontend

```bash
# Navega al directorio del frontend
cd frontend

# Instala las dependencias
npm install

# Inicia la aplicación en modo desarrollo
npm run dev
```

El frontend estará disponible en `http://localhost:5173` por defecto (o el puerto que Vite asigne).

### Ejecución con Docker Compose (Contenedores)

Si deseas ejecutar toda la aplicación (frontend, backend y base de datos) usando Docker Compose:

1.  **Construir las imágenes (la primera vez o cuando haya cambios en el código):**
    ```bash
    # Desde la raíz del proyecto
    docker-compose build
    ```
2.  **Iniciar todos los servicios:**
    ```bash
    # Desde la raíz del proyecto
    docker-compose up -d
    ```

Esto levantará los contenedores para MySQL, el backend y el frontend. Los servicios estarán accesibles en los puertos configurados (por ejemplo, `http://localhost:8080` para el backend y `http://localhost:5173` para el frontend, dependiendo de tu configuración).

## Datos de Demostración (Seed)

El proyecto incluye un script SQL para poblar la base de datos con datos de demostración útiles para pruebas:

-   **Ubicación:** `database/seed/reset_and_seed_demo_data.sql`
-   **Uso:** Este script borra todos los datos existentes y luego inserta usuarios, clientes, vehículos y configuraciones iniciales. Para ejecutarlo, puedes conectarte a tu base de datos MySQL (por ejemplo, con `mysql -u root -p autofinpe < database/seed/reset_and_seed_demo_data.sql`) o importarlo a través de una herramienta como DBeaver o MySQL Workbench.

## Usuario Demo

Puedes utilizar las siguientes credenciales para iniciar sesión en la aplicación después de ejecutar el script de seed:

-   **Usuario Administrador:**
    -   **Login:** `admin`
    -   **Clave:** `Admin123!`
-   **Usuarios Ejecutivos:**
    -   **Login:** `e.castillo`, `j.quispe`, `m.flores`, etc. (ver `reset_and_seed_demo_data.sql`)
    -   **Clave:** `Ejecutivo2026!` (para todos los ejecutivos demo)

