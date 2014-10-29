package com.julianpeeters.avro.toolbox.provider

import com.julianpeeters.toolbox.provider._
import models.FieldData
import stores.ClassFieldStore
import org.apache.avro.Schema
import scala.collection.JavaConversions._

object AvroTypeMatcher {

  def parseField(field: Schema.Field): FieldData = {

    def avroToScalaType(schema: org.apache.avro.Schema): String = {
      schema.getType match { 
        case Schema.Type.ARRAY    => "List[" + avroToScalaType(schema.getElementType) + "]"
        case Schema.Type.BOOLEAN  => "Boolean"
        //case Schema.Type.BYTES    => //TODO
        case Schema.Type.DOUBLE   => "Double"
        //case Schema.Type.FIXED    => //TODO
        case Schema.Type.FLOAT    => "Float"
        case Schema.Type.LONG     => "Long"
        case Schema.Type.INT      => "Int"
        //case Schema.Type.MAP      => //TODO
        case Schema.Type.NULL     => "Null"
        case Schema.Type.RECORD   => { 

          field.schema.getName match {
            //cases where a record is found as a field vs found as a member of a union vs found as an element of an array

            case "array" | "union" => schema.getName
            case recordName        => recordName 
          }

        }
        case Schema.Type.STRING   => "String"
        case Schema.Type.UNION    => { 

          val unionSchemas = schema.getTypes.toList

          if (unionSchemas.length == 2 && unionSchemas.exists(schema => schema.getType == Schema.Type.NULL)) {
            val maybeSchema = unionSchemas.find(schema => schema.getType != Schema.Type.NULL)
            if (maybeSchema.isDefined ) "Option[" + avroToScalaType(maybeSchema.get) + "]"
            else error("no avro type found in this union")  
          }
          else error("not a union field")
        }
        case x                    => error( x +  " is not a valid Avro type")
      }
    }

    FieldData(field.name, avroToScalaType(field.schema))
  }
}
