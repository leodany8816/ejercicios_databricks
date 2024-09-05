import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import java.util.UUID
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
    //crearTablas(db)

    //insertaRegistros(db)

    consultaRegistros(db)






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
    val proControl = TableQuery[ProcesoControlTable]
    val uuid: String = UUID.randomUUID().toString
    println(s"Identificador unico ${uuid}")
    try{
      // Definir los registros a insertar
      val insertActions = DBIO.seq(
        proControl += ProcesoControl(0, Timestamp.valueOf(LocalDateTime.now()), uuid, true, "test1.txt"),
        proControl += ProcesoControl(0, Timestamp.valueOf(LocalDateTime.now()), uuid, true, "test2.txt")
      )

      // Ejecutar las inserciones
      db.run(insertActions).onComplete {
        case Success(_) => println("Registros insertados correctamente.")
        case Failure(e) => println(s"Error al insertar registros: ${e.getMessage}")
      }

    }catch {
      case e: Exception =>
        println(s"Error al insertar el registro en la tabla: ${e.getMessage}")
    }
  }

  /** Complete esta función para obtener los registros de la tabla [[Table]] */
  def consultaRegistros(db:Database): Unit = {
    println("Consultar registros")
    val proControl = TableQuery[ProcesoControlTable]
    try {
    val query = proControl.result

    val result = db.run(query)

    result.onComplete {
      case Success(registros) =>
        println("Registros en la tabla:")
        registros.foreach { registro =>
          println(s"ID: ${registro.id}, Fecha: ${registro.fechaActualizacion}, Identificador: ${registro.identificador}, Carga Correcta: ${registro.cargaCorrecta}, Archivo: ${registro.nombreArchivo}")
        }
      case Failure(e) =>
        println(s"Error al consultar registros: ${e.getMessage}")
    }
      Await.result(result, 10.seconds)

    } catch {
      case e: Exception =>
        println(s"Error al realizar la consulta: ${e.getMessage}")
    }
  }

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
