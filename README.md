# ⏰ TimeforMed

**TimeforMed** es una aplicación móvil para la gestión de recordatorios de medicamentos, diseñada para ayudar a los usuarios a seguir correctamente sus tratamientos médicos. Permite registrar usuarios, medicamentos, tratamientos personalizados, horarios de toma y confirmar si los medicamentos fueron tomados o no.

---

## 🚀 Tecnologías utilizadas

- **Kotlin + Spring Boot** – Backend robusto y moderno
- **PostgreSQL** – Base de datos relacional
- **JPA / Hibernate** – Mapeo objeto-relacional
- **Postman** – Pruebas de endpoints
- **Docker** – Contenedor para PostgreSQL
- **GitHub** – Control de versiones y colaboración

---

## 🧱 Arquitectura por capas

El proyecto sigue una arquitectura por capas bien definida:

```
src/
├── controllers         # Lógica de entrada: maneja las rutas REST
├── services            # Lógica de negocio
├── repositories        # Acceso a la base de datos (JPA)
├── models
│   ├── entities        # Entidades JPA
│   ├── requests        # DTOs de entrada
│   └── responses       # DTOs de salida
├── mappers             # Conversión entre entidades y DTOs
├── routes              # Rutas centralizadas
└── exceptions          # Manejo de errores personalizados
```

---

## 🗃️ Estructura de la base de datos

El sistema contiene las siguientes tablas:

- `users`: Usuarios del sistema
- `meds`: Medicamentos
- `treatments`: Tratamientos médicos creados por cada usuario
- `treatment_meds`: Relación entre tratamientos y medicamentos, incluye información como dosis, frecuencia, duración y hora de inicio
- `takes`: Registro de tomas de medicamentos con hora programada y confirmación si se tomó


---

## 📌 Funcionalidades principales

- Crear, obtener, actualizar y eliminar:
    - Usuarios
    - Medicamentos
    - Tratamientos
    - Asociación de tratamientos con medicamentos (`TreatmentMed`)
    - Registro de tomas (`Take`)
- Confirmar si el medicamento fue tomado o no

---

## 📮 Endpoints disponibles

| Recurso         | Método | Ruta                                          | Descripción                                 |
|-----------------|--------|-----------------------------------------------|---------------------------------------------|
| Usuarios        | POST   | `/api/timeformed/users`                      | Crear usuario                                |
|                 | GET    | `/api/timeformed/users`                      | Listar usuarios                              |
|                 | GET    | `/api/timeformed/users/{id}`                 | Obtener usuario por ID                       |
|                 | PUT    | `/api/timeformed/users/{id}`                 | Actualizar usuario                           |
|                 | DELETE | `/api/timeformed/users/{id}`                 | Eliminar usuario                             |
| Medicamentos    | POST   | `/api/timeformed/meds`                       | Crear medicamento                            |
|                 | GET    | `/api/timeformed/meds`                       | Listar todos                                 |
|                 | GET    | `/api/timeformed/meds/{id}`                  | Obtener por ID                               |
|                 | PUT    | `/api/timeformed/meds/{id}`                  | Actualizar medicamento                       |
|                 | DELETE | `/api/timeformed/meds/{id}`                  | Eliminar medicamento                         |
| Tratamientos    | POST   | `/api/timeformed/treatments`                | Crear tratamiento                            |
|                 | GET    | `/api/timeformed/treatments`                | Listar tratamientos                          |
|                 | GET    | `/api/timeformed/treatments/{id}`           | Obtener por ID                               |
|                 | PUT    | `/api/timeformed/treatments/{id}`           | Actualizar tratamiento                       |
|                 | DELETE | `/api/timeformed/treatments/{id}`           | Eliminar tratamiento                         |
| TreatmentMeds   | POST   | `/api/timeformed/treatment-meds`            | Asociar medicamento a tratamiento            |
|                 | GET    | `/api/timeformed/treatment-meds`            | Listar asociaciones                          |
|                 | GET    | `/api/timeformed/treatment-meds/{id}`       | Obtener por ID                               |
|                 | PUT    | `/api/timeformed/treatment-meds/{id}`       | Actualizar relación                          |
|                 | DELETE | `/api/timeformed/treatment-meds/{id}`       | Eliminar relación                            |
| Takes           | POST   | `/api/timeformed/takes`                     | Registrar toma programada                    |
|                 | GET    | `/api/timeformed/takes`                     | Listar todas las tomas                       |
|                 | GET    | `/api/timeformed/takes/{id}`                | Obtener por ID                               |
|                 | GET    | `/api/timeformed/takes/treatment-med/{id}`  | Tomar por ID de tratamiento-medicamento      |
|                 | DELETE | `/api/timeformed/takes/{id}`                | Eliminar registro de toma                    |

---
## ⚙️ Cómo ejecutar el proyecto

1. Asegúrate de tener Docker instalado y corriendo.
2. Abre una terminal en el directorio raíz del proyecto.
3. Ejecuta:

```bash
docker-compose up
```

Esto levantará el contenedor de PostgreSQL necesario para el backend.

4. Abre otra terminal y corre el backend desde IntelliJ o usando:

```bash
./gradlew bootRun
```

---
## 📋 Cómo probar en Postman

1. Asegúrate de tener corriendo tu backend (`./gradlew bootRun`) y PostgreSQL en Docker.
2. Usa `http://localhost:8080/api/timeformed` como base URL.
3. Crea datos en orden lógico: `User` → `Med` → `Treatment` → `TreatmentMed` → `Take`.
4. Verifica el flujo completo con los métodos GET, POST, PUT y DELETE para cada entidad.

---




## 📁 Repositorio

[🔗 GitHub - TimeforMed](https://github.com/alcarrion/timeformed-backend.git)
