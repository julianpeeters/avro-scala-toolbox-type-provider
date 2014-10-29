package com.julianpeeters.avro.toolbox.provider

import java.io.File
import org.apache.avro.Schema
import scala.collection.JavaConverters._

import com.julianpeeters.toolbox.provider._
import models.{ FieldData, ClassData }

object ToolBoxTypeProvider {

  def schemaToCaseClass(infile: File): ToolBoxCaseClass = {
    val schema: Schema = SchemaParser.getSchema(infile)
    val recordSchemas: List[Schema] = schema::(SchemaParser.getNestedSchemas(schema))
    val namespace: Option[String] = SchemaParser.getNamespace(schema)
    val classData: List[ClassData] = recordSchemas.map(s => {
      ClassData(namespace, s.getName, s.getFields.asScala.toList.map(field => {
        AvroTypeMatcher.parseField(field)
      }))
    })
    val typeTemplate = classData.reverse.map(cd => new ToolBoxCaseClass(cd)).last
    typeTemplate
  }

}
