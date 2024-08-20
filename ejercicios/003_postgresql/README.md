# Comunicación con Postgresql Single

En este ejercicio aprenderá a utilizar [Slick](https://scala-slick.org/) para comunicarse con [Postgresql](https://www.postgresql.org/).

Esta forma de comunicación normalmente es usada cuando llenamos tablas de control, de estatus o registramos bitácoras de los procesos.

## Introducción

### Slick

Es una librería que permite que trabajemos con las bases de datos utilizando clases y tipos de dato especificos en lugar de escribir únicamente SQL.

Por ejemplo, en lugar de utilizar esta sentencia SQL:

```sql
UPDATE tablaprocesoControl
SET estatus = 1, fechaActualizacion = '2024-05-24'
WHERE idEjecucion = '0aeb5645-048f-48a3-bb5e-ab90ef273346'
```

Podemos hacerlo con código:

```scala
def actualizarEstatusDeRegistro(idEjecucion: String, estatus: Int): Unit = {
  val q = tablaProcesoControl
    .filter(_.idEjecucion === idEjecucion)
    .map(r => (r.estatus, r.fechaActualizacion))
    .update(estatus, Util.obtenerTimestamp())

  Await.result(db.run(q), QUERY_TIMEOUT)
}
```

También podemos usar SQL si es necesario, [como se muestra aquí](https://scala-slick.org/doc/prerelease/sql.html).

### Postgresql

Postgresql es una base de datos relacional de código abierto que ha sido desarrollada por más de 35 años, normalmente la usamos para registrar datos de control y usamos su versión distribuida (Citus) para almacenar millones de filas de modelos de datos que consume el usuario.

## Instrucciones

1. Instale [WSL 2](https://learn.microsoft.com/en-us/windows/wsl/install).
2. Instale [Docker Desktop](https://docs.docker.com/desktop/install/windows-install/), es la manera más rápida de instalar y usar docker. Asegurese de que use WSL 2.
3. Descargue e inicie un contenedor para Postgres ejecutando los siguientes comandos en la terminal:
   
  ```
  docker pull postgres
  docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=root -e POSTGRES_USER=root -d postgres
  ```

4. Puede verificar el funcionamiento de su instancia de Postgres conectandose a ella utilizando [DBeaver](https://dbeaver.io/) o [pgAdmin](https://www.pgadmin.org/). Vea un ejemplo de cómo conectarse con DBeaver [en este GIF](https://github.com/capemo42/teia-training/assets/89544452/ca2787fb-5f0c-4ca2-b00b-e0bb10bad26d).
5. Abra la solución 003 en IntelliJ y lea la clase `Learning`. La tabla que debe crear el método `crearTablas` deberá tener el nombre `ProcesoControl` en Scala y `proceso_control` en Postgresql, aparecer en el esquema `teia` y tener los campos descritos en la siguiente tabla. [Aquí puede ver un ejemplo de como definir una tabla con Slick](https://scala-slick.org/doc/prerelease/schemas.html#case-classes-with-the-mapto-macro).
   
  | Nombre campo Scala | Nombre campo Postgresql | Tipo de dato |
  |--------------------|-------------------------|--------------|
  | fechaActualizacion | fecha_actualizacion     | Timestamp    |
  | identificador      | identificador           | String       |
  | cargaCorrecta      | carga_correcta          | Boolean      |
  | nombreArchivo      | nombre_archivo          | String       |

Ejemplo resultado:

![image](https://github.com/capemo42/teia-training/assets/89544452/266fe355-a650-4913-aa10-42ca1ac714dd)


6. El método `insertaRegistros` deberá insertar los siguientes datos:

  | nombre_archivo | carga_correcta | identificador       | fecha_actualizacion                                                     |
  |----------------|----------------|---------------------|-------------------------------------------------------------------------|
  | archivo1.txt   | true           | Aquí genere un UUID | Aquí inserte la fecha actual en la zona horaria de Europa central (CET) |
  | archivo2.json  | true           | Aquí genere un UUID | Aquí inserte la fecha actual en la zona horaria de Europa central (CET) |
  | archivo3.csv   | true           | Aquí genere un UUID | Aquí inserte la fecha actual en la zona horaria de Europa central (CET) |

  Ejemplo resultado:

  ![image](https://github.com/capemo42/teia-training/assets/89544452/08fa2a04-a66e-43a1-9346-d172fdb70f10)

7. El método `consultaRegistros` deberá consultar la tabla ProcesoControl y permitir filtrar por la columna `cargaCorrecta`. Deberá regresar una colección de entidades. Ejemplo: el método deberá regresar todas las entidades donde el campo `cargaCorrecta` sea `true`.
8. El método `actualizaRegistro` deberá permitir la actualización del campo `cargaCorrecta` de una fila de la tabla `ProcesoControl`. Ejemplo: el método deberá actualizar el campo `cargaCorrecta` a `false` para el archivo `archivo2.json`.
9. El método `eliminaRegistros` deberá eliminar todos los registros que cumplan con el filtro enviado con el campo `cargaCorrecta`. Ejemplo: el método deberá eliminar todos los registros donde el campo `cargaCorrecta` sea `true`.
10. Una vez que termine de codificar sus métodos, ejecute las pruebas definidas en `LearningSuite` y verifique que la prueba termine exitosamente. Ejemplo:

IntelliJ:

![image](https://github.com/capemo42/teia-training/assets/89544452/49636ebb-f514-4db2-b6cf-60fca52e9c9d)

Postgresql:

![image](https://github.com/capemo42/teia-training/assets/89544452/15cdd4c2-0ab9-4fb1-89a7-04fc0eb26189)


