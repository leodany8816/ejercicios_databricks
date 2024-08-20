# Comunicación con Azure Storage

En este ejercicio deberá clonar el repositorio, después abrir el proyecto ubicado en la carpeta `AzureStorage` en IntelliJ IDEA ([video ejemplo](https://teianet-my.sharepoint.com/:v:/g/personal/carlos_pena_teia_mx/EQRHRA6HWIBPpbXr_6XANqEBNGa0jFWEqG7NlUhwAdibUw?e=HlDkeS)) y completar el código para que la solución cargue los archivos proporcionados (son tres archivos, un txt, un json y un csv) a Azure Blob Storage e inserte en una tabla de Azure un registro por cada uno, indicando el nombre del archivo que se insertó y la fecha en que lo hizo.

Al finalizar la carga, deberá comprobar el número de archivos y registros cargados en la suite de pruebas `LearningSuite`.

Ejemplo:

```scala
  test("Ejecutar y verificar carga de blobs") {
    val rutaBlobs = ".../teia/ejercicio_001/archivos"
    val numeroBlobsLocal = Util.cuentaArchivosEnCarpeta(rutaBlobs)
    val numeroBlobsCargados = ProcesadorArchivos.cargaBlobs(rutaBlobs)
    assert(numeroBlobsCargados == numeroBlobsLocal)
  }

  test("Ejecutar y verificar carga de filas") {
    val numeroFilasEsperadas = Util.cuentaArchivosEnCarpeta(rutaBlobs)
    val numeroFilasCargadas = VerificadorCarga.cuentaFilasEnTablaAzure(nombreTAbla)
    assert(numeroFilasEsperadas == numeroFilasCargadas)
  }
```

En el siguiente gif se muestra lo que deberá ser uno de los pasos finales de su ejercicio. Ejecutará una prueba, que cargará los archivos a Azure Blob Storage y después verificará que se hayan cargado.

[Vea aquí cómo llamar sus métodos para que sean probados](https://teianet-my.sharepoint.com/:v:/g/personal/carlos_pena_teia_mx/ETkjNPAKIZJCmEj6oTPDoD8BTwQbVzHXEJv6o1jdaVpL-g?e=DQnwp4).

![Intellij-run-test](https://github.com/capemo42/teia-training/assets/89544452/ae1cd150-d3b0-4f08-8b6f-5de4dfa1f014)

Al final del ejercicio deberá crear un archivo llamado `USAGE.md`, donde deberá describir al "usuario" de su programa los pasos a seguir para poder utilizarlo. Ejemplo:

```
  Instrucciones de uso:

  1. Deberá configurar la variable de entorno ABC, con la cadena de conexión a la cuenta de almacenamiento.
  2. Deberá configurar la variable de entorno D, con el nombre del contenedor de blobs donde se cargarán los archivos.
  
  ...
```

## Consideraciones

1. El nombre del archivo en Azure Blob Storage deberá ser el mismo que el nombre del archivo original.

2. La tabla de Azure deberá tener las siguientes columnas:

| Columna        | Descripción                                               |
|----------------|-----------------------------------------------------------|
| PartitionKey   | Nombre del archivo                                        |
| RowKey         | Concatenación de la hora actual en formato yyyyMMddHHmmss |
| NombreArchivo  | Nombre del archivo                                        |
| FechaInsercion | Timestamp en zona horaria de la ciudad de México          |

> [!TIP]
> Puede usar una cuenta de Azure de su suscripción o usar el emulador [Azurite](https://github.com/Azure/Azurite).

3. Recuerde no escribir cadenas de conexión o datos sensibles en el código. Puede usar un archivo de configuración, una variable de entorno o similares para leer los datos desde ahí. [Vea aquí cómo usar un archivo de configuración simple](https://teianet-my.sharepoint.com/:v:/g/personal/carlos_pena_teia_mx/Ea8S1MQCE4ZIic014uOCMwsBMMm_bIPCyxfXH7rHZzwbJQ?e=9Aba3p).
4. Recuerde no dejar "código duro" para las rutas donde se leen los archivos. Considere que cuando alguien más descargue su código, no lo descargará en la misma ruta que usted.
5. Use las librerías [azure-data-tables.](https://mvnrepository.com/artifact/com.azure/azure-data-tables/12.4.0) y [azure-storage-blob](https://mvnrepository.com/artifact/com.azure/azure-storage-blob/12.25.4) dado que la librería [azure-storage-8.6.6](https://mvnrepository.com/artifact/com.microsoft.azure/azure-storage/8.6.6) no ha sido actualizada desde 2021 y tiene una vulnerabilidad.

## Referencias

1. [Azure Storage Introduction](https://learn.microsoft.com/en-us/azure/storage/common/storage-introduction)
2. [Azure Storage SDK for Java](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/storage)
3. [Scala AnyFunSuite](https://www.scalatest.org/scaladoc/3.1.2/org/scalatest/funsuite/AnyFunSuite.html)

> [!IMPORTANT]  
> Recuerde que las librerías para Java pueden ser utilizadas en Scala.
