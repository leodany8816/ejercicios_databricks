Ejercicio de refactorización de consulta en Spark para evitar shuffle

Código original:

```scala
spark.table("cfdi_ref_plata_40.nomina12")
  .withColumn("PayLoadPago", explode_outer($"payload"))
  .withColumn("Deduccion", explode_outer($"PayLoadPago.Deducciones.Deduccion"))
  .select( 
      $"UUID"
      , $"Header.Emisor.Rfc".alias("RfcEmisor")
      , $"Header.Receptor.Rfc".alias("RfcReceptor")
      , $"Deduccion.Clave".alias("ClaveDeduccion")
      , $"Deduccion.Importe"
      , $"Deduccion.TipoDeduccion"
      , $"PayLoadPago.Receptor.TipoRegimen"
      , year($"PayLoadPago.FechaPago").alias("Ejercicio")
      , month($"PayLoadPago.FechaPago").alias("Periodo")
      , $"PayLoadPago.FechaPago"
      , $"PayLoadPago.FechaInicialPago"
      , $"PayLoadPago.FechaFinalPago"
      , $"p_fechaemision".alias("FechaEmision")
    )
  .groupBy(
      $"UUID"
      , $"RfcEmisor"
      , $"RfcReceptor"
      , $"Ejercicio"
      , $"Periodo"
      , $"ClaveDeduccion"
      , $"TipoDeduccion"
      , $"TipoRegimen"
      , $"FechaPago"
      , $"FechaInicialPago"
      , $"FechaFinalPago"
      , $"FechaEmision"
    )
    .agg(sum($"Importe").as("ImporteDeducciones"))
```

Plan lógico:

```
'Aggregate ['UUID, 'RfcEmisor, 'RfcReceptor, 'Ejercicio, 'Periodo, 'ClaveDeduccion, 'TipoDeduccion, 'TipoRegimen, 'FechaPago, 'FechaInicialPago, 'FechaFinalPago, 'FechaEmision], ['UUID, 'RfcEmisor, 'RfcReceptor, 'Ejercicio, 'Periodo, 'ClaveDeduccion, 'TipoDeduccion, 'TipoRegimen, 'FechaPago, 'FechaInicialPago, 'FechaFinalPago, 'FechaEmision, sum('Importe) AS ImporteDeducciones#1533]
+- Project [UUID#60, Header#61.Emisor.Rfc AS RfcEmisor#1489, Header#61.Receptor.Rfc AS RfcReceptor#1490, Deduccion#1478.Clave AS ClaveDeduccion#1491, Deduccion#1478.Importe AS Importe#1498, Deduccion#1478.TipoDeduccion AS TipoDeduccion#1499, PayLoadPago#77.Receptor.TipoRegimen AS TipoRegimen#1500, year(PayLoadPago#77.FechaPago) AS Ejercicio#1492, month(PayLoadPago#77.FechaPago) AS Periodo#1493, PayLoadPago#77.FechaPago AS FechaPago#1503, PayLoadPago#77.FechaInicialPago AS FechaInicialPago#1504, PayLoadPago#77.FechaFinalPago AS FechaFinalPago#1505, p_fechaemision#67 AS FechaEmision#1494]
   +- Project [UUID#60, Header#61, payload#62, load_ts#63, fecharecepcion#64, p_fecharecepcion#65, load_date#66, p_fechaemision#67, PayLoadPago#77, Deduccion#1478]
      +- Generate explode(PayLoadPago#77.Deducciones.Deduccion), true, [Deduccion#1478]
         +- Filter (p_fecharecepcion#65 >= cast(2024-01-01 as date))
            +- Project [UUID#60, Header#61, payload#62, load_ts#63, fecharecepcion#64, p_fecharecepcion#65, load_date#66, p_fechaemision#67, PayLoadPago#77]
               +- Generate explode(payload#62), true, [PayLoadPago#77]
                  +- SubqueryAlias spark_catalog.cfdi_ref_plata_40.nomina12
                     +- Relation spark_catalog.cfdi_ref_plata_40.nomina12[UUID#60,Header#61,payload#62,load_ts#63,fecharecepcion#64,p_fecharecepcion#65,load_date#66,p_fechaemision#67] parquet
```

Plan Spark:

```scala
00 HashAggregate(keys=[UUID#60, RfcEmisor#1631, RfcReceptor#1632, Ejercicio#1634, Periodo#1635, ClaveDeduccion#1633, TipoDeduccion#1641, TipoRegimen#1642, FechaPago#1645, FechaInicialPago#1646, FechaFinalPago#1647, FechaEmision#1636], functions=[finalmerge_sum(merge sum#1712, isEmpty#1713) AS sum(Importe#1640)#1674], output=[UUID#60, RfcEmisor#1631, RfcReceptor#1632, Ejercicio#1634, Periodo#1635, ClaveDeduccion#1633, TipoDeduccion#1641, TipoRegimen#1642, FechaPago#1645, FechaInicialPago#1646, FechaFinalPago#1647, FechaEmision#1636, ImporteDeducciones#1675])
01 +- HashAggregate(keys=[UUID#60, RfcEmisor#1631, RfcReceptor#1632, Ejercicio#1634, Periodo#1635, ClaveDeduccion#1633, TipoDeduccion#1641, TipoRegimen#1642, FechaPago#1645, FechaInicialPago#1646, FechaFinalPago#1647, FechaEmision#1636], functions=[partial_sum(Importe#1640) AS (sum#1712, isEmpty#1713)], output=[UUID#60, RfcEmisor#1631, RfcReceptor#1632, Ejercicio#1634, Periodo#1635, ClaveDeduccion#1633, TipoDeduccion#1641, TipoRegimen#1642, FechaPago#1645, FechaInicialPago#1646, FechaFinalPago#1647, FechaEmision#1636, sum#1712, isEmpty#1713])
02    +- Project [UUID#60, Rfc_0_extract_cbb34d7#1694 AS RfcEmisor#1631, Rfc_0_extract_addbb5f0#1693 AS RfcReceptor#1632, Deduccion#1697.Clave AS ClaveDeduccion#1633, Deduccion#1697.Importe AS Importe#1640, Deduccion#1697.TipoDeduccion AS TipoDeduccion#1641, TipoRegimen_7_extract_32a977e4#1690 AS TipoRegimen#1642, year(FechaPago_2_extract_9d7e4d47#1691) AS Ejercicio#1634, month(FechaPago_2_extract_9d7e4d47#1691) AS Periodo#1635, FechaPago_2_extract_9d7e4d47#1691 AS FechaPago#1645, FechaInicialPago_3_extract_9d7e4d47#1692 AS FechaInicialPago#1646, FechaFinalPago_4_extract_9d7e4d47#1695 AS FechaFinalPago#1647, p_fechaemision#67 AS FechaEmision#1636]
03       +- Generate explode(Deduccion_2_extract_57ee82ac#1698), [UUID#60, p_fechaemision#67, FechaFinalPago_4_extract_9d7e4d47#1695, FechaInicialPago_3_extract_9d7e4d47#1692, FechaPago_2_extract_9d7e4d47#1691, Rfc_0_extract_addbb5f0#1693, Rfc_0_extract_cbb34d7#1694, TipoRegimen_7_extract_32a977e4#1690], true, [Deduccion#1697]
04          +- Project [UUID#60, p_fechaemision#67, PayLoadPago#1700.Deducciones.Deduccion AS Deduccion_2_extract_57ee82ac#1698, PayLoadPago#1700.FechaFinalPago AS FechaFinalPago_4_extract_9d7e4d47#1695, PayLoadPago#1700.FechaInicialPago AS FechaInicialPago_3_extract_9d7e4d47#1692, PayLoadPago#1700.FechaPago AS FechaPago_2_extract_9d7e4d47#1691, Rfc_0_extract_addbb5f0#1693, Rfc_0_extract_cbb34d7#1694, PayLoadPago#1700.Receptor.TipoRegimen AS TipoRegimen_7_extract_32a977e4#1690]
05             +- Generate explode(payload#62), [UUID#60, Rfc_0_extract_addbb5f0#1693, Rfc_0_extract_cbb34d7#1694, p_fechaemision#67], true, [PayLoadPago#1700]
06                +- Project [UUID#60, Header#61.Receptor.Rfc AS Rfc_0_extract_addbb5f0#1693, Header#61.Emisor.Rfc AS Rfc_0_extract_cbb34d7#1694, payload#62, p_fechaemision#67]
07                   +- FileScan parquet spark_catalog.cfdi_ref_plata_40.nomina12[UUID#60,Header#61,payload#62,p_fecharecepcion#65,p_fechaemision#67] Batched: true, DataFilters: [], Format: Parquet, Location: PreparedDeltaFileIndex(1 paths)[dbfs:/mnt/ref_cfdi_plata_40/datalake/targets/cfdi_plata_40.db/nom..., PartitionFilters: [isnotnull(p_fecharecepcion#65), (p_fecharecepcion#65 >= 2024-01-01)], PushedFilters: [], ReadSchema: struct<UUID:string,Header:struct<Emisor:struct<Rfc:string>,Receptor:struct<Rfc:string>>,payload:a...
```


Código refactorizado:

```scala
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import scala.collection.mutable
import scala.util._

case class Deduccion(TipoDeduccion: String, Clave: String, Concepto: String, Importe: java.math.BigDecimal)
case class ResultadoAgrupacionDeduccion(
  FechaPago: java.sql.Date, FechaInicialPago: java.sql.Date, FechaFinalPago: java.sql.Date, 
  TipoDeduccion: String, Clave: String, TipoRegimen: String, Importe: java.math.BigDecimal)

val sumaImporte = udf((d: mutable.WrappedArray[GenericRowWithSchema]) => {

  d.map { data => 
    val fp = data.getAs[java.sql.Date]("FechaPago")
    val fip = data.getAs[java.sql.Date]("FechaInicialPago")
    val ffp = data.getAs[java.sql.Date]("FechaFinalPago")
    val tipoRegimen = data.getAs[GenericRowWithSchema]("Receptor").getAs[String]("TipoRegimen")

    val deducciones = for {
      deducciones <- Option(data.getAs[GenericRowWithSchema]("Deducciones"))
      deduccion <- Option(deducciones.getAs[mutable.WrappedArray[GenericRowWithSchema]]("Deduccion"))
    } yield deduccion

    // Revisar si mantenemos filas que no tengan Deducciones
    deducciones.map { arregloDeducciones => 
        arregloDeducciones.map { d =>
          Deduccion(
            d.getAs[String]("TipoDeduccion"),
            d.getAs[String]("Clave"),
            d.getAs[String]("Concepto"),
            d.getAs[java.math.BigDecimal]("Importe")
          )
        }.groupBy(a => (a.Clave, a.TipoDeduccion))
        .toSeq
        .map { group => 
          val importes = group._2.map(_.Importe).reduce((a, b) => a.add(b))
          ResultadoAgrupacionDeduccion(fp, fip, ffp, group._1._2, group._1._1, tipoRegimen, importes)
        }
    }
  }
  .flatten.flatten
  .groupBy(v => (v.FechaPago, v.FechaInicialPago, v.FechaFinalPago, v.TipoDeduccion, v.Clave, v.TipoRegimen))
  .toSeq
  .map { group => 
    val importes = group._2.map(_.Importe).reduce((a, b) => a.add(b))
    ResultadoAgrupacionDeduccion(group._1._1, group._1._2, group._1._3, group._1._4, group._1._5, group._1._6, importes)
  }
})

val dataNew = spark.table("cfdi_ref_plata_40.nomina12")
  .filter("p_fecharecepcion >= '2024-01-01'")
  .withColumn("resultadosUDF", explode_outer(sumaImporte($"payload")))
  .select(
    $"UUID"
    , $"Header.Emisor.Rfc".alias("RfcEmisor")
    , $"Header.Receptor.Rfc".alias("RfcReceptor")
    , $"resultadosUDF.Clave".alias("ClaveDeduccion")
    , $"resultadosUDF.Importe"
    , $"resultadosUDF.TipoDeduccion"
    , $"resultadosUDF.TipoRegimen"
    , year($"resultadosUDF.FechaPago").alias("Ejercicio")
    , month($"resultadosUDF.FechaPago").alias("Periodo")
    , $"resultadosUDF.FechaPago"
    , $"resultadosUDF.FechaInicialPago"
    , $"resultadosUDF.FechaFinalPago"
    , $"p_fechaemision".alias("FechaEmision")
  )
```

Plan lógico:

```
'Project ['UUID, 'Header.Emisor.Rfc AS RfcEmisor#1418, 'Header.Receptor.Rfc AS RfcReceptor#1419, 'resultadosUDF.Clave AS ClaveDeduccion#1420, 'resultadosUDF.Importe, 'resultadosUDF.TipoDeduccion, 'resultadosUDF.TipoRegimen, year('resultadosUDF.FechaPago) AS Ejercicio#1421, month('resultadosUDF.FechaPago) AS Periodo#1422, 'resultadosUDF.FechaPago, 'resultadosUDF.FechaInicialPago, 'resultadosUDF.FechaFinalPago, 'p_fechaemision AS FechaEmision#1423]
+- Project [UUID#1390, Header#1391, payload#1392, load_ts#1393, fecharecepcion#1394, p_fecharecepcion#1395, load_date#1396, p_fechaemision#1397, resultadosUDF#1408]
   +- Generate explode(UDF(payload#1392)), true, [resultadosUDF#1408]
      +- Filter (p_fecharecepcion#1395 >= cast(2024-01-01 as date))
         +- SubqueryAlias spark_catalog.cfdi_ref_plata_40.nomina12
            +- Relation spark_catalog.cfdi_ref_plata_40.nomina12[UUID#1390,Header#1391,payload#1392,load_ts#1393,fecharecepcion#1394,p_fecharecepcion#1395,load_date#1396,p_fechaemision#1397] parquet
```

Plan Spark:

```scala
00 Project [UUID#1743, Rfc_0_extract_cbb34d7#1801 AS RfcEmisor#1771, Rfc_0_extract_addbb5f0#1802 AS RfcReceptor#1772, resultadosUDF#1761.Clave AS ClaveDeduccion#1773, resultadosUDF#1761.Importe AS Importe#1780, resultadosUDF#1761.TipoDeduccion AS TipoDeduccion#1781, resultadosUDF#1761.TipoRegimen AS TipoRegimen#1782, year(resultadosUDF#1761.FechaPago) AS Ejercicio#1774, month(resultadosUDF#1761.FechaPago) AS Periodo#1775, resultadosUDF#1761.FechaPago AS FechaPago#1785, resultadosUDF#1761.FechaInicialPago AS FechaInicialPago#1786, resultadosUDF#1761.FechaFinalPago AS FechaFinalPago#1787, p_fechaemision#1750 AS FechaEmision#1776]
01 +- Generate explode(UDF(payload#1745)), [UUID#1743, p_fechaemision#1750, Rfc_0_extract_addbb5f0#1802, Rfc_0_extract_cbb34d7#1801], true, [resultadosUDF#1761]
02    +- Project [UUID#1743, payload#1745, p_fechaemision#1750, Header#1744.Receptor.Rfc AS Rfc_0_extract_addbb5f0#1802, Header#1744.Emisor.Rfc AS Rfc_0_extract_cbb34d7#1801]
03       +- FileScan parquet spark_catalog.cfdi_ref_plata_40.nomina12[UUID#1743,Header#1744,payload#1745,p_fecharecepcion#1748,p_fechaemision#1750] Batched: true, DataFilters: [], Format: Parquet, Location: PreparedDeltaFileIndex(1 paths)[dbfs:/mnt/ref_cfdi_plata_40/datalake/targets/cfdi_plata_40.db/nom..., PartitionFilters: [isnotnull(p_fecharecepcion#1748), (p_fecharecepcion#1748 >= 2024-01-01)], PushedFilters: [], ReadSchema: struct<UUID:string,Header:struct<Emisor:struct<Rfc:string>,Receptor:struct<Rfc:string>>,payload:a...
```

## Proceso

Lo primero que detectamos al analizar la consulta inicial y con un conocimiento de negocio, es que no es necesario realizar un `explode` y después una agrupación, ya que los datos que necesitamos para obtener la suma del campo `Importe` se encuentran dentro del campo payload de las filas "originales".

La tabla fuente es `nomina12` y para efectos del ejercicio, simplificaremos el esquema que tiene.

```
root
 |-- UUID: string (nullable = true)
 |-- Header: struct (nullable = true)
 |    |-- Version: string (nullable = true)
 |    |-- Receptor: struct (nullable = true)
 |    |    |-- Rfc: string (nullable = true)
 |    |    |-- Nombre: string (nullable = true)
 |    |    |-- DomicilioFiscalReceptor: string (nullable = true)
 |    |    |-- ResidenciaFiscal: string (nullable = true)
 |    |    |-- NumRegIdTrib: string (nullable = true)
 |    |    |-- RegimenFiscalReceptor: string (nullable = true)
 |    |    |-- UsoCFDI: string (nullable = true)
 |-- payload: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- Version: string (nullable = true)
 |    |    |-- TipoNomina: string (nullable = true)
 |    |    |-- FechaPago: date (nullable = true)
 |    |    |-- FechaInicialPago: date (nullable = true)
 |    |    |-- FechaFinalPago: date (nullable = true)
 |    |    |-- Receptor: struct (nullable = true)
 |    |    |    |-- TipoRegimen: string (nullable = true)
 |    |    |-- Percepciones: struct (nullable = true)
 |    |    |-- Deducciones: struct (nullable = true)
 |    |    |    |-- TotalOtrasDeducciones: decimal(38,18) (nullable = true)
 |    |    |    |-- TotalImpuestosRetenidos: decimal(38,18) (nullable = true)
 |    |    |    |-- Deduccion: array (nullable = true)
 |    |    |    |    |-- element: struct (containsNull = true)
 |    |    |    |    |    |-- TipoDeduccion: string (nullable = true)
 |    |    |    |    |    |-- Clave: string (nullable = true)
 |    |    |    |    |    |-- Concepto: string (nullable = true)
 |    |    |    |    |    |-- Importe: decimal(38,18) (nullable = true) 
 |-- fecharecepcion: timestamp (nullable = true)
 |-- p_fecharecepcion: date (nullable = true)
 |-- load_date: string (nullable = true)
 |-- p_fechaemision: date (nullable = true)
```

Lo que necesitamos obtener es una agrupación de varios campos para sumar el campo `Importe`, el punto a destacar es que no necesitamos hacer la agrupación a nivel de fila (y por lo tanto usar las sentencias de Spark `groupBy` y `agg`), la agrupación es al nivel del `payload`, por lo que una alternativa es usar una UDF para evitar que Spark haga agregaciones de cada partición, shuffle, etc.

Para tener más contexto, recordemos que cuando creamos un DataFrame con Spark, la información está distribuida en M particiones en N workers. Ejemplo:

![spark_dataframe_distribution](https://github.com/capemo42/teia-training/assets/89544452/5df179ff-cdfc-43f2-920b-b6a0e1ab191c)

Entonces, si utilizamos un `groupBy`, la información de la tabla `nomina_12` que se encuentra en cada ejecutor, será agrupada y después los ejecutores se comunicaran entre ellos para ordenar la información de particiones iguales. Esta comunicación es costosa.

En cambio, si no usamos un `groupBy` y utilizamos una UDF para que la transformación sea completamente local, los ejecutores no tienen que comunicarse entre sí y con esto obtenemos un mejor rendimiento.

## UDF

La UDF recibe como argumento `(d: mutable.WrappedArray[GenericRowWithSchema])`, que es un arreglo de un StructType el cual desconocemos o no tenemos disponible las case classes.

En este punto le enviamos la variable `payload` y como es un arreglo, usaremos la función `map` para aplicar una función a cada elemento del arreglo.

Lo primero que haremos será extraer varios campos que utilizaremos para la agrupación más adelante:

![image](https://github.com/capemo42/teia-training/assets/89544452/db995d9a-9f68-4e41-846a-665576c60fbd)

En el siguiente paso usamos un for-comprehension para revisar si existen los nodos `Deducciones` y `Deduccion` y si tienen datos lo almacenamos en la variable `deducciones`.

![image](https://github.com/capemo42/teia-training/assets/89544452/922f3f1d-7708-4a81-bf50-63372c573259)

Ahora que tenemos un arreglo de Deducciones, los transformaremos a la case class `Deduccion` para tener más claridad en el código. Después agruparemos por los campos `Clave` y `TipoDeduccion` para realizar la sumatoria del campo `Importe`. Finalmente regresamos la entidad `ResultadoAgrupacionDeduccion`.

![image](https://github.com/capemo42/teia-training/assets/89544452/e68b6f25-42e0-46de-aa00-b7bf9718812f)

Finalmente aplicamos una transformación para eliminar arreglos anidados con la función `flatten`, volvemos a agrupar pero ahora por varios campos y nuevamente sumamos el campo `Importe`.

![image](https://github.com/capemo42/teia-training/assets/89544452/a43dc158-d276-4ebf-ad3e-961e105a4ca9)

Finalmente usamos la función al consumir el DataFrame y obtenemos los campos requeridos:

![image](https://github.com/capemo42/teia-training/assets/89544452/9d8b472a-2362-49e9-9f2a-f3c192324810)
