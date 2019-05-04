package se.stenervall.webcrawlie

import com.github.kittinunf.fuel.httpGet
import java.io.File
import java.util.*
import java.util.regex.Pattern

class WebCrawler {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                System.err.println("Expected single argument: <url>")
                System.exit(1)
            }
            val crawl = WebCrawler().crawl(args[0])
            val textMap = crawl.toSortedMap()
                .map { "${it.key} -> ${it.value}" }
                .joinToString("\n") { it }
            File("sitemap.txt").writeText(textMap)
        }
    }

    private val hrefRegex = Pattern.compile("href=\"([^\"]+)\"")!!

    fun crawl(url: String): Map<String, Set<String>> {
        val queue = ArrayDeque<String>()
        val visited = mutableMapOf<String, Set<String>>()
        queue.add("/")

        while (queue.isNotEmpty()) {
            val nextUrl = queue.pop()
            when {
                nextUrl.startsWith("//") ->
                    "${url.protocol()}${nextUrl}".httpGetAndFindLinksAndQueueNewLinks(nextUrl, visited, queue)
                nextUrl.startsWith("/") ->
                    "${url}${nextUrl}".httpGetAndFindLinksAndQueueNewLinks(nextUrl, visited, queue)
                nextUrl.startsWith(url) ->
                    nextUrl.httpGetAndFindLinksAndQueueNewLinks(nextUrl, visited, queue)
            }
        }

        return visited.toMap()
    }

    private fun String.httpGetAndFindLinksAndQueueNewLinks(
        nextUrl: String,
        visited: MutableMap<String, Set<String>>,
        queue: ArrayDeque<String>
    ) {
        val newUrls = this.httpGetAndFindLinks()
        visited[nextUrl] = newUrls
        queue.addAll(newUrls)
    }

    private fun String.httpGetAndFindLinks(): Set<String> {
        val (_, _, result) = this.httpGet().responseString()
        result.fold(
            success = { html ->
                return html.findHrefs()
            },
            failure = {
                System.err.println("Failed to get ${this}, continuing...")
                return emptySet()
            }
        )
    }

    private fun String.findHrefs(): Set<String> {
        val matcher = hrefRegex.matcher(this)
        val hrefs = mutableSetOf<String>()
        while (matcher.find()) {
            hrefs.add(matcher.group(1))
        }
        return hrefs.toSet()
    }

    private fun String.protocol() = this.split("//").first()
}
