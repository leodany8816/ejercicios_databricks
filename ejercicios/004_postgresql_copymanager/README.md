# Introducción

## Postgresql CopyManager

[CopyManager](https://jdbc.postgresql.org/documentation/publicapi/org/postgresql/copy/CopyManager.html) es una herramienta que proporciona el driver jdbc de PostgreSQL, su función es facilitar las operaciones en bulk entre aplicaciones de Java (o Scala) y PostgreSQL.

Los casos de uso más comunes en los proyectos que usualmente trabajamos son:

  1. Carga de archivos CSV.
  2. Carga hacia Citus de DataFrames con millones de datos utilizando Spark.

# Instrucciones

1. Utilice Docker para iniciar un contenedor con Citus con el siguiente comando:
   
   ```
   docker run -d --name citus -p 5432:5432 -e POSTGRES_PASSWORD=mypass citusdata/citus:12.1
   ```
   
2. Abra la solución 004 en IntelliJ y complete los métodos en la clase `Learning`.
3. El método `inicializarEsquema` deberá crear el esquema `teia` y las tablas `carga_csv` y `carga_clase`.
   ![image](https://github.com/capemo42/teia-training/assets/89544452/5a208b19-ee11-431e-bb5a-e7b112eca360)
4. El método `cargaFilasDesdeClase` deberá cargar las filas definidas en la variable `datosParaCargar` a Citus.
   ![image](https://github.com/capemo42/teia-training/assets/89544452/c3fb2be9-ee26-4e4d-9e9c-c4d9cdef2ac1)
5. El método `cargaFilasDesdeArchivo` deberá cargar las filas encontradas en el archivo `proyectos.txt` a Citus.
   ![image](https://github.com/capemo42/teia-training/assets/89544452/43262b51-8618-4bf7-902f-ef32ee36758e)
6. Ejecute la clase de pruebas `LearningSuite` para comprobar el resultado.
