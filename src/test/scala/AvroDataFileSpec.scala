
import org.specs2.mutable.Specification
import com.julianpeeters.avro.toolbox.provider.ToolBoxTypeProvider
import java.io.File
import com.novus.salat._
import global._
import com.mongodb.casbah.Imports._

class AvroDataFileSpec extends Specification {

  "A case class that was generated at runtime from the schema of an Avro Datafile" should {
    "serialize and deserialize correctly with a Salat" in {

      val file = new File("src/test/resources/twitter.avro")
      val runtimeClass = ToolBoxTypeProvider.schemaToCaseClass(file)
      val record = runtimeClass.runtimeInstance
      type MyAvroRecord = record.type

      ctx.registerClassLoader(runtimeClass.loader)
      val dbo = grater[MyAvroRecord].asDBObject(record)
      val sameRecord = grater[MyAvroRecord].asObject(dbo)

      sameRecord must ===(record)
    }
  }
}
