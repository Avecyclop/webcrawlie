package se.stenervall.webcrawlie

import com.github.kittinunf.fuel.httpGet
import java.util.*
import java.util.regex.Pattern

class WebCrawler {
    val hrefRegex = Pattern.compile("href=\"([^\"]+)\"")!!

    fun crawl(url: String): Set<String> {
        val queue = ArrayDeque<String>()
        val visited = mutableSetOf<String>()
        queue.add("/")

        while (queue.isNotEmpty()) {
            val nextUrl = queue.pop()
            if(nextUrl.startsWith("/")){
                val newUrls = "${url}${nextUrl}".httpGetAndFindLinks()
                visited.add(nextUrl)
                queue.addAll(newUrls)
            }
        }

        return visited.toSet()
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
}
