# 📊 Monitoreo Simple - Grafana + Loki

## 🚀 Inicio Rápido

### 1. Levantar el stack de monitoreo
```bash
docker-compose up -d mysql loki grafana
```

### 2. Ejecutar la aplicación Spring Boot
```bash
./gradlew bootRun
```

### 3. Acceder a Grafana
- **URL**: http://localhost:3000
- **Usuario**: admin
- **Contraseña**: admin

## 📋 Dashboard Incluido

### "Seek Management - Logs Dashboard"
- **Panel 1**: Application Logs - Todos los logs de la aplicación
- **Panel 2**: Error Logs - Solo logs de errores
- **Panel 3**: Business Logs - Logs relacionados con clientes, usuarios y autenticación

## 🔗 URLs de Servicios

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Aplicación | http://localhost:8080/api | API REST |
| Swagger | http://localhost:8080/api/swagger-ui/index.html | Documentación API |
| Grafana | http://localhost:3000 | Dashboard de logs |
| Loki | http://localhost:3100 | API de Loki (interno) |
| MySQL | localhost:3306 | Base de datos |

## 📝 Notas

- Los logs se guardan automáticamente en el archivo `logs/seek-management.log`
- Loki se conecta automáticamente como datasource en Grafana
- El dashboard se carga automáticamente al iniciar Grafana
- Los logs se actualizan cada 5 segundos en Grafana

## 🔄 Generar Actividad para Ver Logs

```bash
# Registro de usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@test.com","password":"password123","confirmPassword":"password123","firstName":"Demo","lastName":"User"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"demo","password":"password123"}'

# Crear cliente (usar el token del login)
curl -X POST http://localhost:8080/api/client \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"firstName":"Juan","lastName":"Pérez","age":32,"birthDate":"1993-05-15"}'
```
