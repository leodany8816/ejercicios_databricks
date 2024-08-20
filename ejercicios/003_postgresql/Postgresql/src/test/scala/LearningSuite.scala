import org.scalatest.funsuite.AnyFunSuite

class LearningSuite extends AnyFunSuite {

  /** Modifique la ruta de configuración y ejecute esta prueba para
    * finalizar el ejercicio.
    */
  val RUTA_CONFIG = ""

  test("Verifica flujo de datos Postgres") {
    val instance = new Learning(config = RUTA_CONFIG)
    instance.crearTablas()
    instance.insertaRegistros()
    val registrosInsertadosTrue = instance.consultaRegistros(cargaCorrecta = true).size
    val registrosInsertadosFalse = instance.consultaRegistros(cargaCorrecta = false).size
    assert(registrosInsertadosTrue == 3)
    assert(registrosInsertadosFalse == 0)

    instance.actualizaRegistro("archivo1.txt", cargaCorrecta = false)

    val registrosFiltradosTrue = instance.consultaRegistros(cargaCorrecta = true).size
    val registrosFiltradosFalse = instance.consultaRegistros(cargaCorrecta = false).size
    assert(registrosFiltradosTrue == 2)
    assert(registrosFiltradosFalse == 1)

    instance.eliminaRegistros(cargaCorrecta = true)
    val registrosRestantesTrue = instance.consultaRegistros(cargaCorrecta = true).size
    val registrosRestantesFalse = instance.consultaRegistros(cargaCorrecta = false).size
    assert(registrosRestantesTrue == 0)
    assert(registrosRestantesFalse == 1)
  }

  test("Ejercicio opcional") {
    /**
     * Combine este ejercicio con el ejercicio 001 para registrar en la base de
     * datos de Postgresql el envío de cada archivo a Azure Blob Storage.
     */
    assert(true)
  }
}
