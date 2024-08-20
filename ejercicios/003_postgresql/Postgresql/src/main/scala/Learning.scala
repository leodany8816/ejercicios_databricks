import slick.jdbc.PostgresProfile.api._

class Learning(config: String) {

  /** Complete o modifique las secciones de código según sea necesario. */

  /** La variable `db` es usada para la creación de un cliente de base de datos
    * para comunicarse con Postgresql. Recuerde usar esta forma para enviar
    * url, usuario y contraseña de manera separada para alinearse a los lineamientos
    * existentes dados por varios clientes.
    *
    * Use un archivo de configuración para leer estos valores.
    */
  lazy val db = Database.forURL(url = "", user = "", password = "")

  /** Complete esta función para crear las tablas definidas */
  def crearTablas(): Unit = ???

  /** Complete esta función para insertar los registros especificados */
  def insertaRegistros(): Unit = ???

  /** Complete esta función para obtener los registros de la tabla [[Table]] */
  def consultaRegistros() = ???

  /** Complete esta función para actualizar los registros insertados */
  def actualizaRegistro(nombreArchivo: String, cargaCorrecta: Boolean): Unit =
    ???

  /** Complete esta función para eliminar registros */
  def eliminaRegistros() = ???
}
