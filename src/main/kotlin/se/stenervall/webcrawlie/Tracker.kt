package se.stenervall.webcrawlie

import java.util.ArrayDeque
import java.util.Queue

data class Tracker(
    val queue: Queue<String> = ArrayDeque(),
    val visited: MutableMap<String, Set<String>> = mutableMapOf(),
    val unvisitable: MutableSet<String> = mutableSetOf()
)
