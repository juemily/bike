#Bike 

Api que crea y consulta Bicicletas e Items de bicicletas

# Caracteristicas del micro servicio:

    1. Java Springboot 
    2. arquitectura Hexagonal
    3. Docker file - Docker compose
    4. cache con caffeine 
    5. Swagger
    6. Base de datos en Memoria (H2)
    7. Securidad con JWT - con LDAP
    8. Test Unitarios con mockito/ Junit
    
    


## Tabla de Contenidos

- [Instalación](#instalación)
- [Uso](#uso)
- [Configuración](#configuración)
- [Contribución](#contribución)
- [Directorio de Archivos](#directorio-de-archivos)
- [Licencia](#licencia)

## Instalación
    Es necesario tener un minomo de requerimientos para el entorno

    1. Tener JAVA y Maven instalado en el ordenador
    2. ejecutar el comando 'mvn clean install'
    3. ejecutar el comando ' mvn spring-boot:run'


## Uso

- [Swagger](#http://localhost:8080/swagger-ui/index.html)

    para acceder a swagger  se debera utilizar usuario y contraseña.
## Credenciales
    User: admin
    Pass: goTeamBike!

## Acceso a Coleccion de postman

- dejo la coleccion de postman preparada (ademas del swagger ) la misma tiene el auth, create y list
- 
  ![Coleccion](src/main/resources/image/Screenshot_1.png)
- uso de los filtros de las caracteristicas de bike e item

        1. param sort : se el campo por el que se desea ordenar
        2. param fields : se envian los campos que se desean visualizar separados por ,



![busqueda](src/main/resources/image/Screenshot_2.png)
![respuesta](src/main/resources/image/Screenshot_3.png)



