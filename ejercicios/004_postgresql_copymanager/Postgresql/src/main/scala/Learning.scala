import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import slick.jdbc.PostgresProfile.api._

import java.io.{BufferedReader, FileReader, InputStream}
import java.sql.DriverManager
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Proyecto(nombre: String, duracionEnDias: Int)

class Learning(config: String) {

  /** Complete o modifique las secciones de código según sea necesario. */

  /** La variable `db` es usada para la creación de un cliente de base de datos
    * para comunicarse con Postgresql. Recuerde usar esta forma para enviar
    * url, usuario y contraseña de manera separada para alinearse a los lineamientos
    * existentes dados por varios clientes.
    *
    * Use un archivo de configuración para leer estos valores.
    */
  val POSTGRES_URL = "obtener_de_configuracion"
  val POSTGRES_USR = "obtener_de_configuracion"
  val POSTGRES_PWD = "obtener_de_configuracion"

  lazy val db = Database.forURL(
    url = POSTGRES_URL,
    user = POSTGRES_USR,
    password = POSTGRES_PWD
  )

  /** Este método deberá crear el esquema teia y las tablas carga_csv y carga_clase.
    *
    * Sugerencias: puede utilizar Slick o el DriverManager directamente para enviar las
    * sentencias a PostgreSQL.
    *
    * Deberá ejecutar una sentencia para crear el esquema y dos para crear las tablas.
    * Ambas tablas deberán tener los campos: nombre varchar, duracion_en_dias int
    */
  def inicializarEsquema(): Unit = {}

  /** En este método deberá enviar los datos de la variable datosParaCargar a PostgreSQL utilizando
    * [[org.postgresql.copy.CopyManager]]. El proceso se resume en:
    *
    * 1. Obtenga su conexión utilizando `DriverManager.getConnection`.
    *
    * 2. Cree una nueva instancia de [[CopyManager]]
    *
    * 3. Utilice el método `copyIn` para cargar las filas de la variable `datosParaCargar`.
    *
    * Nota: aquí es donde debe utilizar el método [[rowsToInputStream]] ya proporcionado.
    */
  def cargaFilasDesdeClase(): Unit = {
    val datosParaCargar =
      Seq(Proyecto("A", 100), Proyecto("B", 200), Proyecto("C", 300))

    val conn = DriverManager

    /** La conexión que reciba CopyManager debe ser de tipo [[BaseConnection]] */
    val cm = new CopyManager()

    cm.copyIn()
  }

  /** Misma mecánica que el método `cargaFilasDesdeClase`. Deberá ajustar los parámetros que
    * recibe el CopyManager para tomar los datos desde un archivo.
    */
  def cargaFilasDesdeArchivo(): Unit = {
    val datosParaCargar =
      Seq(Proyecto("A", 100), Proyecto("B", 200), Proyecto("C", 300))

    val conn = DriverManager
    val rutaArchivo = "ruta_que_debe_leerse_de_configuracion"

    /** Pista: puede utilizar una combinación de [[FileReader]] y [[BufferedReader]]
      */

    val cm = new CopyManager()
    cm.copyIn()
  }

  /** Método de utilería. No es necesario que lo modifique.
    *
    * Opcional: explique que hace este método en un archivo markdown.
    */
  private def rowsToInputStream(datosParaCargar: Seq[Proyecto]): InputStream = {
    val bytes: Iterator[Byte] = datosParaCargar.flatMap { row =>
      val transformedRowToString = row.productIterator.toSeq
        .map { field =>
          if (field == null) {
            """\N"""
          } else {
            "\"" + field.toString
              .replaceAll("\u0000", "")
              .replaceAll("\"", "\"\"") + "\""
          }
        }
        .mkString("\t") + "\n"
      transformedRowToString.getBytes("UTF-8")
    }.toIterator

    () =>
      if (bytes.hasNext) {
        bytes.next & 0xff
      } else {
        -1
      }
  }

  /** Otro método de utilería. No es necesario que lo modifique.
    */
  private def run[T](fn: => DBIO[T]): T = {
    Await.result(db.run(fn), Duration(30, TimeUnit.SECONDS))
  }
}
