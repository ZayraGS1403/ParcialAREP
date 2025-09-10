# Key-Value Store - Parcial 1 AREP

## ¿Qué es?
Un sistema de almacenamiento clave-valor distribuido que permite realizar operaciones SET y GET a través de una interfaz web. El proyecto implementa una arquitectura cliente-servidor donde el Facade actúa como proxy entre la interfaz web y el servidor de almacenamiento.

## Comandos para ejecutar

### 1. Compilar el proyecto
```bash
mvn clean install
```

### 2. Ejecutar los servidores

#### En la primera terminal - Facade (Puerto 36000):
```bash
java -cp target/classes co.edu.escuelaing.parcial1arep.Facade
```

#### En una segunda terminal - HttpServer (Puerto 37000):
```bash
java -cp target/classes co.edu.escuelaing.parcial1arep.HttpServer
```

### 3. Acceder a la aplicación
Abre tu navegador y ve a:
```
http://localhost:36000/cliente
```


## Ejemplo
1. **SET**: Key=`alumno`, Value=`zayra`
   - Respuesta: `{"key": "alumno", "value": "zayra", "status": "created"}`

2. **GET**: Key=`alumno`
   - Respuesta: `{"key": "alumno", "value": "zayra"}`


## Autor
Zayra Gutierrez
