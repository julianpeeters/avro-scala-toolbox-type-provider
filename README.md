avro-scala-toolbox-type-provider
================================

A runtime type-provider based on Scala reflection.

Parses Avro schemas at runtime, and uses a Scala reflection `ToolBox` to dynamically define and load case classes that represent Avro records.

Depends on [toolbox-type-provider](http://github.com/julianpeeters/toolbox-type-provider)

Usage:
------

Get a `ToolBoxCaseClass` instance that represents an Avro record:

    import com.julianpeeters.avro.toolbox.provider.ToolBoxTypeProvider
    import java.io.File

    val file = new File("path/to/schema.avsc")
    val runtimeClass = ToolBoxTypeProvider.schemaToCaseClass(file)

with which you will be a able to:

    // get a `ClassSymbol` and enter Scala reflection
    runtimeClass.runtimeClassSymbol

    // get an instance of the newly generated class's companion object
    runtimeClass.runtimeCompanionObject

    // get an instance of the newly generated class
    runtimeClass.runtimeInstance

    // get the toolbox's classloader
    runtimeClass.loader

    // get an alias for the runtime type for use as a type parameter (in some contexts, ymmv)
    myParameterizedThing[runtimeClass.TYPE]

Please note:
------------

1) Reflection circumvents type-saftey. If you find yourself here, please consider if you truly need to define classes at runtime. For example, in the example above, the schema file that is accessed at runtime is *also* accessible at compile-time, and therefore is candidate for a macro, which is type-safe (see [avro-scala-macro-annotations](https://github.com/julianpeeters/avro-scala-macro-annotations)).

2) ToolBoxes use a special classloader, and although that classloader is always stored with the generated class, the class may not be usable with libraries that cannot manage classloaders, and it's further reflection must be handled accordingly. 

3) Although ToolBoxes are the official way to define and load classes at runtime, they are still labeled experimental. They are limited to defining one class per package and increasing that number is unlikely until the [scala.meta](http://scalameta.org/) replaces it (scala.meta Beta expected near the release of Scala 2.12). If you need more classes until then, consider using [avro-scala-runtime-type-provider](https://github.com/julianpeeters/avro-scala-runtime-type-provider).


