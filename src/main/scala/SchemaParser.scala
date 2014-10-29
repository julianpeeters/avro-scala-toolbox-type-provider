package com.julianpeeters.avro.toolbox.provider

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.Schema.Type.{ARRAY, RECORD, UNION}
import org.apache.avro.generic.{ GenericDatumReader, GenericRecord}
import org.apache.avro.file.DataFileReader
import scala.collection.JavaConverters._

object SchemaParser { 

  def getSchema(infile: java.io.File): Schema = {
    val schema = infile.getName.split("\\.").last match {
      case "avro" =>
        val gdr = new GenericDatumReader[GenericRecord]
        val dfr = new DataFileReader(infile, gdr)
        dfr.getSchema
      case "avsc" =>
        new Parser().parse(infile)
      case _ => throw new Exception("Invalid file ending. Must be .avsc for plain text json files and .avro for binary files.")
    }
    schema.getType match {
      case UNION  => schema.getTypes.asScala.toList.head //if record is wrapped in a union
      case RECORD => schema
      case _      => error("neither a record nor a union of records, nothing to map to case class.")
    }
  }

  def getNamespace(schema: Schema): Option[String] = {
     schema.getNamespace match {
       case null      => None
       case namespace => Some(namespace.split('.').last)
     }
  }

  def getNestedSchemas(schema: Schema): List[Schema] = {
    val fields: List[org.apache.avro.Schema.Field] = schema.getFields.asScala.toList
    val fieldSchemas:List[org.apache.avro.Schema]  = fields.map(field => field.schema())

    def flattenSchema(fieldSchema: Schema): List[Schema] = {
      fieldSchema.getType match {
        case ARRAY  => flattenSchema(fieldSchema.getElementType)
        case RECORD => getNestedSchemas(fieldSchema); List(fieldSchema)
        case UNION  => fieldSchema.getTypes.asScala.toList.flatMap(x => flattenSchema(x))
        case _      => List(fieldSchema)
      }
    }

    val flatSchema = fieldSchemas.flatMap(fieldSchema => flattenSchema(fieldSchema))
    val nestedRecordSchemas = flatSchema.filter(x => x.getType == RECORD)

    nestedRecordSchemas
  }

}

