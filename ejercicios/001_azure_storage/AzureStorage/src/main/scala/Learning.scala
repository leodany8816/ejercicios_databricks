import java.io.FileInputStream
import java.util.Properties
import com.typesafe.config.{Config, ConfigFactory}
import java.io.File
import com.azure.storage.common.StorageSharedKeyCredential
import com.azure.core.credential.AzureNamedKeyCredential
import com.azure.storage.blob._
import com.azure.data.tables._
import com.azure.data.tables.models._
import java.time.LocalDateTime


object Learning {
  /**
   *
   * @param args
   */

  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.parseResources("ejercicios.conf")

    val accountName: String = config.getString("conexion-azure.nombreCuenta")
    val containerName: String = config.getString("conexion-azure.contenedor")
    val tableName: String = config.getString("conexion-azure.nombreTablaAzure")
    val accountKey: String = config.getString("conexion-azure.account-key")
    val localDir: String = config.getString("conexion-azure.localDir")

    println(
      s"""
        |Nombre del la cuenta de storage ${accountName}
        |Nombre del contenedor ${containerName}
        |Nombre de la tabla ${tableName}
        |Nombre de la rutaLocal ${localDir}
        |""".stripMargin)

    /**
     * Conexion para el contenedor en azure storage
     */
    val credential = new StorageSharedKeyCredential(accountName, accountKey)

    val blobServiceClient = new com.azure.storage.blob.BlobServiceClientBuilder()
      //      .endpoint(s"https://$accountName.blob.core.windows.net")
      .endpoint(s"http://127.0.0.1:10000/$accountName")
      .credential(credential)
      .buildClient()
    val containerClient = blobServiceClient.getBlobContainerClient(containerName)

    /**
     * Conexion para la tabla en azure storage
     */

    val tableCredential = new AzureNamedKeyCredential(accountName, accountKey)

    val clientTable = new TableServiceClientBuilder()
      //      .endpoint(s"https://$accountName.table.core.windows.net")
      .endpoint(s"http://127.0.0.1:10002/$accountName")
      .credential(tableCredential)
      .buildClient()

    val tableClient = clientTable.getTableClient(tableName)

    println("Iniciamos la carga de los archivos locales al storage y el registro a la tabla de azure storage")
    val folder = new File(localDir)
    if(folder.exists && folder.isDirectory) {
      val files = folder.listFiles()
      if (files != null) {
        files.filter(_.isFile).foreach { file =>
          // se realiza la carga
          val blobClient = containerClient.getBlobClient(file.getName)
          println(s"Subiendo el ${file.getName} a Azure Storage")
          blobClient.uploadFromFile(file.getAbsolutePath, true)
          println(s"${file.getName} subido exitosamente.")

          // se realiza el registro a la tabla
          val entidad = new TableEntity(file.getName, LocalDateTime.now().toString)
          entidad.addProperty("NombreArchivo", file.getName)
          entidad.addProperty("FechaInsercion", LocalDateTime.now().toString)

          tableClient.createEntity(entidad)
          println(s"Se regista en la tabla el siguiente archivo ${file.getName}")
        }
      }else{
        println("No existe archivos en la carpeta indicada")
      }
    }else{
      println("El directorio no existe")
    }


    // Listar los archivos del contenedor
    val blobs = containerClient.listBlobs().iterator()
    while (blobs.hasNext) {
      val blobItem = blobs.next()
      println(s"Blob name: ${blobItem.getName}")
    }
  }
}

