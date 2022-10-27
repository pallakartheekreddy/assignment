
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.csv.{CsvMapper, CsvSchema}

import java.io.File
import java.util
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.io.Source

object ScalaAssignment extends App {
  /*
  1. A csv file and a json file has been given with same set of fields
  2. The application should be able to parse both csv and json files
  3. Use inheritance and abstraction to define a structure to write a JSONParser and a CSVParser class
  4. Depending upon the input file type specified, parse the file automatically (Factory design patten)
  5. Define Case Classes matching the fields in both the JSON and CSV files
  6. Read the content of each line in the files as an instance of Case Class.
  7. The application should be extensible to read XML files in the future.
  8. Error handling for format
    1. Gender enum values
    2. Case sensitive check
    3. Missing values
   */
  abstract class Parser {
    def parseData()
  }

  case class employee(firstname: String, lastname: String, gender: String)

  object Parser {

    private class JsonParser(val fileUrl: String) extends Parser {
      override def parseData(): Unit = {
        val mapper = new ObjectMapper
        val urlData = Source.fromFile(fileUrl).mkString
        val jsonStringData = mapper.readValue(urlData, classOf[java.util.Map[String, Object]])
        val employeeInfo = jsonStringData.getOrDefault("employeeInfo", util.Arrays.asList()).asInstanceOf[util.List[util.Map[String, Object]]]
        employeData(employeeInfo)
      }
    }

    private class CSVParser(val fileUrl: String) extends Parser {
      override def parseData(): Unit = {
        val csvMapper = new CsvMapper
        val csvSchema = CsvSchema.builder().setUseHeader(true).build()
        val inputCsvFile = new File(fileUrl)
        val employeeInfo = csvMapper.readerFor(classOf[java.util.Map[String, String]]).`with`(csvSchema).readValues(inputCsvFile).readAll().asInstanceOf[util.List[util.Map[String, Object]]]
        employeData(employeeInfo)
      }
    }

    private class OtherParser(val fileUrl: String) extends Parser {
      override def parseData(): Unit = {
        println("Not Supported")
      }
    }

    def employeData(empData: util.List[util.Map[String, Object]]): Unit = {
      empData.map(a => {
        println(employee(getDataFromObj(a, "firstname"), getDataFromObj(a, "lastname"), getDataFromObj(a, "gender")))
      })
    }

    def getDataFromObj(data: util.Map[String, Object], key: String): String = {
      data.get(key).toString
    }

    def apply(filetype: String, fileUrl: String): Parser = {
      filetype match {
        case "json" => new JsonParser(fileUrl)
        case "csv" => new CSVParser(fileUrl)
        case _ => new OtherParser(fileUrl)
      }
    }
  }

    val parser = Parser("json", "/Users/kartheek/Documents/Workspace/playground/scala/assignment/src/main/resources/data.json")
  //  val parser = Parser("csv", "/Users/kartheek/Documents/Workspace/playground/scala/assignment/src/main/resources/data.csv")
//  val parser = Parser("xml", "abc")
  parser.parseData()
}
