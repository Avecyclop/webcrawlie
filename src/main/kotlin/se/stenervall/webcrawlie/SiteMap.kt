package se.stenervall.webcrawlie

import java.io.File

class SiteMap(val map: Map<String, Set<String>>) {
    fun writeToFile(file: String) {
        val textMap = map.toSortedMap()
            .map { "${it.key} -> ${it.value}" }
            .joinToString("\n") { it }
        File(file).writeText(textMap)
    }
}
