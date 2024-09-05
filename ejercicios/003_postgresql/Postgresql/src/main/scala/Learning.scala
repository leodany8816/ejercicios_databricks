import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import java.sql.Timestamp
import java.time.LocalDateTime


//import slick.jdbc.H2Profile.api._

object Learning {

  /** Complete o modifique las secciones de código según sea necesario. */

  /** La variable `db` es usada para la creación de un cliente de base de datos
    * para comunicarse con Postgresql. Recuerde usar esta forma para enviar
    * url, usuario y contraseña de manera separada para alinearse a los lineamientos
    * existentes dados por varios clientes.
    *
    * Use un archivo de configuración para leer estos valores.
    */

  def main(args: Array[String]): Unit = {



    val config: Config = ConfigFactory.parseResources("conexion.conf")

    val host: String = config.getString("conexion-postgres.url")
    val usuario: String = config.getString("conexion-postgres.usuario")
    val password: String = config.getString("conexion-postgres.password")

    println(
      s"""
         |host ${host}
         |usuario ${usuario}
         |password ${password}
         |""".stripMargin)

    lazy val db = Database.forURL(url = host, user = usuario, password = password)
    crearTablas(db)

    insertaRegistros(db)



//
//    // Consultar los datos
//    val queryFuture = db.run(proCrontol.result)
//
//    queryFuture.map { result =>
//      result.foreach(println)
//    }

//    val setup = db.run(DBIO.seq(
//      schema.createIfNotExists
//    ))

//    val setupFuture = db.run(setup)
//    Await.result(setupFuture, 10.seconds)

  }

  /** Complete esta función para crear las tablas definidas */
  def crearTablas(db:Database): Unit = {
    println("Entra para crear la tabla")
    val proCrontol = TableQuery[ProcesoControlTable]
    try {
      println("Conectado a la base de datos correctamente.")

      val schema = proCrontol.schema
      val createSchemaFuture = db.run(DBIO.seq(
        schema.createIfNotExists
      ))

      // Esperamos la finalización del Future
      Await.result(createSchemaFuture, 10.seconds)

      // Si llega aquí, significa que el Future se completó con éxito
      schema.create.statements.foreach(println)
      println("La tabla se creo correctamente.")

    } catch {
      case e: Exception =>
        println(s"Error al crear la tabla: ${e.getMessage}")
    }
  }

  /** Complete esta función para insertar los registros especificados */
  def insertaRegistros(db:Database): Unit = {
    println("Insertar registros")
//    val proCrontol = TableQuery[ProcesoControlTable]
//    try{
//      val sql = DBIO.seq(
//        proControl  += (0, Timestamp.valueOf(LocalDateTime.now()), "1", true, "test1.txt"),
//        proControl  += (0, Timestamp.valueOf(LocalDateTime.now()), "2", true, "test2.txt")
//      )
//
//      val sql1 = sql.insertStatement
//
//    }catch {
//      case e: Exception =>
//        println(s"Error al insertar el registro en la tabla: ${e.getMessage}")
//    }
  }

  /** Complete esta función para obtener los registros de la tabla [[Table]] */
  def consultaRegistros() = ???

  /** Complete esta función para actualizar los registros insertados */
  def actualizaRegistro(nombreArchivo: String, cargaCorrecta: Boolean): Unit =
    ???

  /** Complete esta función para eliminar registros */
  def eliminaRegistros() = ???
}

case class ProcesoControl(id: Int, fechaActualizacion: Timestamp, identificador: String, cargaCorrecta: Boolean, nombreArchivo: String)

class ProcesoControlTable(tag: Tag) extends Table[ProcesoControl](tag, "PROCESOCONTROL") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def fechaActualizacion = column[Timestamp]("fecha_actualizacion")
  def identificador = column[String]("identificador")
  def cargaCorrecta = column[Boolean]("carga_correcta")
  def nombreArchivo = column[String]("nombre_archivo")

  def * = (id, fechaActualizacion, identificador, cargaCorrecta, nombreArchivo) <> (ProcesoControl.tupled, ProcesoControl.unapply)
}
