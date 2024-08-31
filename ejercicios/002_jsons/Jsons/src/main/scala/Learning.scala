import java.io.FileInputStream
import java.util.Properties
import com.typesafe.config.{Config, ConfigFactory}
import java.io.File
import java.io.FileReader
import java.io.{FileWriter, BufferedWriter}
import com.azure.storage.common.StorageSharedKeyCredential
import com.azure.storage.blob._
import com.azure.storage.blob.models._
import com.google.gson.Gson

object Learning {

  /***
   *
   * @param args
   */
  def main(args: Array[String]): Unit = {
    println("Hello JSon!")

    /**
     * Obtenemos la configuracion de la conexion al storage
     */
    val config: Config = ConfigFactory.parseResources("ejercicios.conf")
    val accountName: String = config.getString("conexion-azure.nombreCuenta")
    val containerName: String = config.getString("conexion-azure.contenedor")
    val accountKey: String = config.getString("conexion-azure.account-key")
    val localDir: String = config.getString("conexion-azure.localDir")

    /**
     * Conexion para el contenedor en azure storage
     */
    val credential = new StorageSharedKeyCredential(accountName, accountKey)

    val blobServiceClient = new com.azure.storage.blob.BlobServiceClientBuilder()
      .endpoint(s"http://127.0.0.1:10000/$accountName")
      .credential(credential)
      .buildClient()
    val containerClient = blobServiceClient.getBlobContainerClient(containerName)

    /**
     * Funcion para leer el archivo Json de manera local
     * Pasando la ruta local donde se encuentra el archivo
     */
    leeJsonDesdeArchivo(localDir)

    /***
     * Funcion para generar el nuevo Json a partir de informacion capturada
     * Se pasa como parametro la ruta local donde se va a guardar el archivo
     * con el nuevo json generado
     */
    generaJsonDesdeObjeto(localDir)

    /**
     * Se realiza la carga a azure storage
     * del nuevo archivo json creado
     */

    val blodClient = containerClient.getBlobClient("newJson.json")
    var fileLoad = s"${localDir}newJson.json"
    var fileInputStream = new FileInputStream(fileLoad)

    try{
      val fileSize = fileLoad.length()
      blodClient.uploadFromFile(fileLoad, true)
      println(s"El nuevo archivo json se cargo al storage")
    }catch {
      case e: Exception =>
        println(s"Error al cargar el archivo: ${e.getMessage}")
    }

  }

  case class Person(Nombre: String, Edad: Int)

  /**
   * Puede utilizar las funciones siguientes o crear una nueva
   * clase u objeto para implementar la funcionalidad.
   */
  def leeJsonDesdeArchivo(pathFile:String): Unit = {
    println("Aqui se va leer el archivo JSON")
    val gson = new Gson()
    val readJson = new FileReader(s"${pathFile}test.json")
    val dataJson = gson.fromJson(readJson, classOf[Person])

    println(s"Estos son los datos Nombre: ${dataJson.Nombre}, Edad: ${dataJson.Edad}")

    readJson.close()

  }

  def generaJsonDesdeObjeto(pathFile:String): Unit = {
    println("Aqui se va generar el nuevo archivo json")

    print("Captura el nuevo nombre ")
    val nombre = scala.io.StdIn.readLine()

    print("Captura la nueva edad ")
    val edad = scala.io.StdIn.readLine().toInt

    val person = Person(Nombre = s"${nombre}", Edad = edad)

    val newPerson = person.copy(Nombre = nombre, Edad = edad)

    println(s"Estos son los nuevos datos Nombre: ${newPerson.Nombre}, Edad: ${newPerson.Edad}")

    val gson = new Gson()
    val newJson = gson.toJson(newPerson)

    println(
      s"""
        |Nuevo Json
        |${newJson}
        |""".stripMargin)

    val newFile =  s"${pathFile}newJson.json"

    val writeJson = new BufferedWriter(new FileWriter(newFile))
    writeJson.write(newJson)
    writeJson.close()

    println("El nuevo archvivo Json fue creado")

  }

}
