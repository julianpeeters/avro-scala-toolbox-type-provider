
import org.specs2.mutable.Specification
import com.julianpeeters.avro.toolbox.provider.ToolBoxTypeProvider
import java.io.File
import com.novus.salat._
import global._
import com.mongodb.casbah.Imports._

class AvroSchemaFileSpec extends Specification {

  "A case class that was generated at runtime from the schema in an Avro Schema File" should {
    "serialize and deserialize correctly with a Salat" in {

      val file = new File("src/test/resources/AvroTypeProviderTestSchemaFile.avsc")
      val runtimeClass = ToolBoxTypeProvider.schemaToCaseClass(file)
      val record = runtimeClass.runtimeInstance
      type MyAVSCRecord = record.type

      ctx.registerClassLoader(runtimeClass.loader)
      val dbo = grater[MyAVSCRecord].asDBObject(record)
      val sameRecord = grater[MyAVSCRecord].asObject(dbo)

      sameRecord must ===(record)
    }
  }
}
