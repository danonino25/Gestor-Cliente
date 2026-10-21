# 💻 Ejercicio5Cliente - Interfaz Web Cliente

Cliente Web e interfaz de usuario desarrollada con **Java 21**, **Spring Boot 3.2.5**, **Thymeleaf** y **Bootstrap 5**. Este microservicio consume los servicios y endpoints de la API REST Backend (`Ejercicio5MyApiGestor`) para la administración y visualización de proyectos, tareas y asignaciones.

El proyecto está preparado para integrarse en un flujo de **Integración y Despliegue Continuo (CI/CD)** en **Jenkins**, incluyendo análisis estático de calidad con **SonarQube**, medición de cobertura con **JaCoCo** y ejecución automatizada de pruebas con **Maven Surefire Plugin**.

---

## 🛠️ Arquitectura y Tecnologías

- **Lenguaje:** Java 21 (JDK 21)
- **Framework Principal:** Spring Boot 3.2.5 (Spring MVC)
- **Motor de Plantillas:** Thymeleaf
- **Diseño Front-End:** Bootstrap 5.3.3 (vía WebJars)
- **Ejecución de Pruebas:** Maven Surefire Plugin (v3.2.5)
- **Cobertura de Código:** JaCoCo Maven Plugin (v0.8.11)
- **Análisis Estático y Calidad:** SonarQube Scanner Plugin (v5.5.0.6356)
- **Gestión de Proyectos y Construcción:** Apache Maven 3.9+
- **Automatización CI/CD:** Jenkins LTS Pipeline

---

## 🔄 Diagrama de Integración

```text
[ Usuario / Navegador Web ]
           │
           ▼
[ Microservicio Cliente (Ejercicio5Cliente : Port 8080) ]
           │
           │ (Consumo mediante Peticiones HTTP / REST JSON)
           ▼
[ Microservicio API (Ejercicio5MyApiGestor : Port 8081) ]
           │
           ▼
[ Base de Datos Persistente (MariaDB / MySQL en Docker) ]
