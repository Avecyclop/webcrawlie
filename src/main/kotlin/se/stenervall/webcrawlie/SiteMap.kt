package se.stenervall.webcrawlie

import net.sourceforge.plantuml.SourceStringReader
import java.io.File
import java.io.FileOutputStream

class SiteMap(val map: Map<String, Set<String>>) {
    companion object {
        private val nonAlphaNumeric = Regex("[^\\w]")
    }

    fun writeToFile(file: String) {
        val textMap = map.toSortedMap()
            .map { "${it.key} -> ${it.value}" }
            .joinToString("\n") { it }
        File(file).writeText(textMap)
    }

    fun graphToFile(file: String) {
        val nodes = map.keys.union(map.values.flatten()).joinToString("\n") {
            "rectangle \"${it}\" as ${it.sanitize()}"
        }
        val relations = map.entries.joinToString("\n") { entry ->
            entry.value.joinToString("\n") { target ->
                "(${entry.key.sanitize()}) --> (${target.sanitize()})"
            }
        }
        val plantUml = """
            @startuml
            scale max 2048 width
            scale max 1024 height
            ${nodes}
            ${relations}
            @enduml
            """

        val diagram = FileOutputStream(file).use {
            SourceStringReader(plantUml).outputImage(it)
        }

        if (diagram?.description == "(Error)") {
            System.err.println("Unknown error when trying to generate graph.")
            System.exit(2)
        }
    }

    private fun String.sanitize() = this.replace(nonAlphaNumeric, "_")
}
