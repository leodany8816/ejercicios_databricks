# Archivos JSON

En este ejercicio aprenderá a transformar archivos JSON a clases y viceversa.

La importancia de este ejercicio radica en que en varios proyectos usamos archivos de configuración en formato JSON, además de crear JSONs a partir de datos de tablas Delta (archivos parquet).

Se incluye el archivo `archivo-2.json` del ejercicio 001, pero puede utilizar algún otro JSON.

> [!TIP]
> Cargue sus archivos JSON generados a Azure Blob Storage para obtener puntos extra.

Por favor use la librería [Gson](https://github.com/google/gson) durante su desarrollo.

## Ejemplo

Considere este archivo JSON de ejemplo, usted podrá utilizar el archivo `archivo-2.json` o cualquier otro que elija.

```json
{
  "Nombre": "Carlos",
  "Edad": 900
}
```

Deberá leer este archivo con código en Scala y deberá imprimir en pantalla los atributos que desee modificar (mínimo un texto y un número).
Puede solicitar al usuario que ingrese los nuevos valores o leerlos de algún archivo de configuración.

Ejemplo lectura:

```scala
val rutaArchivoJson = "archivo.json"
val empleado = ProcesadorJson.leeJson(rutaArchivoJson)

println(s"El nombre del empleado es ${empleado.Nombre} y tiene ${empleado.Edad} años.")
```

La salida del código anterior sería algo como lo siguiente:

```
> El nombre del empleado es Carlos y tiene 900 años.
```

Ahora deberá modificar las propiedades de la variable `empleado` (o crear una nueva variable), que actualmente tiene el JSON deserializado:

```scala
empleado.Nombre = "Mi nombre"
empleado.Edad = 30

println(s"El nombre del nuevo empleado es ${empleado.Nombre} y tiene ${empleado.Edad} años.")
```

> [!TIP]
> Investigue el método `copy` de las case classes en Scala.

Finalmente deberá serializar a JSON la variable empleado y escribir el archivo. Ejemplo:

```scala
val jsonComoString = ProcesadorJson.transformaEntidadAJsonString(empleado);
Utilerias.escribeArchivo(contenido = jsonComoString, ruta = ".../Jsons/archivo-resultado.json")
```

El contenido del archivo archivo-resultado.json para este ejemplo será:

```json
{
  "Nombre": "Mi nombre",
  "Edad": 30
}
```

Recuerde añadir una prueba en `LearningSuite` para verificar que se genere el JSON modificado.

## Referencias

1. [Json](https://www.json.org/json-en.html)
2. [Gson](https://github.com/google/gson)

> [!IMPORTANT]  
> Recuerde que las librerías para Java pueden ser utilizadas en Scala.
