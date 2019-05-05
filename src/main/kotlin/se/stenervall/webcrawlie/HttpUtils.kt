package se.stenervall.webcrawlie

import com.github.kittinunf.fuel.httpGet
import java.util.regex.Pattern

private val hrefRegex = Pattern.compile("href=\"([^\"]+)\"")!!

internal fun String.protocol() = this.split("//").first()

internal fun String.httpGetAndFindLinksOrNull(): Set<String>? {
    val (_, response, result) = this.httpGet().responseString()
    result.fold(
        success = { html ->
            return html.findHrefs()
        },
        failure = {
            System.err.println("Failed to get ${this} (${response.statusCode}), continuing...")
            return null
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