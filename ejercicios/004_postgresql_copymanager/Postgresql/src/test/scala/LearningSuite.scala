import org.scalatest.funsuite.AnyFunSuite
import slick.jdbc.PostgresProfile.api._

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class LearningSuite extends AnyFunSuite {

  /** Modifique la ruta de configuración y ejecute esta prueba para
    * finalizar el ejercicio.
    */
  val RUTA_CONFIG = ""

  lazy val db = Database.forURL(
    url = "obtener_de_configuracion",
    user = "obtener_de_configuracion",
    password = "obtener_de_configuracion"
  )

  private def run[T](fn: => DBIO[T]): T = {
    Await.result(db.run(fn), Duration(30, TimeUnit.SECONDS))
  }

  test("Verifica flujo de datos Postgres") {

    val instance = new Learning("")
    instance.inicializarEsquema()
    instance.cargaFilasDesdeArchivo()
    instance.cargaFilasDesdeClase()

    val numRowsCsv = run(sql"""SELECT COUNT(*) FROM teia.carga_csv""".as[Long].head)
    assert(numRowsCsv == 3)

    val numRowsClass = run(sql"""SELECT COUNT(*) FROM teia.carga_clase""".as[Long].head)
    assert(numRowsClass == 3)
  }
}
