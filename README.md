# Virtual ticket backend

Aplicación para pedir números en una fila virtual con visualización en tiempo real

## Funcionalidades consideradas
- Autentificación y autorización con JWT Tokens
- Sockets
- API del dominio

## Tecnologias usadas
- Java 17
- Spring Boot 3.x
- Spring security
- Postgres en Supabase
- H2 database para tests
- Lombok

## Otras consideraciones
- Se divide la aplicación por componentes (domain, security, socket)
- Se considera una arquitectura por capas (repository, service, controller)
- Se realizan pruebas a servicios del dominio y socket.
