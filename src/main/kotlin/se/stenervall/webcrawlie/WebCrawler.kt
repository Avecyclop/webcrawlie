package se.stenervall.webcrawlie

class WebCrawler {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val (url, opt) = validate(args)
            val siteMap = WebCrawler().crawl(url)
            if (opt == "graph") {
                siteMap.graphToFile("sitemap-graph.png")
            } else {
                siteMap.writeToFile("sitemap.txt")
            }
        }

        private fun validate(args: Array<String>): Pair<String, String?> {
            return when {
                args.size == 1 -> Pair(args[0], null)
                args.size == 2 && args[1] == "graph" -> Pair(args[0], args[1])
                else -> {
                    System.err.println("Expected arguments: <url> [graph]")
                    System.exit(1)
                    Pair("", "")
                }
            }
        }
    }

    fun crawl(url: String): SiteMap {
        val tracker = Tracker()
        tracker.queue.add(url)

        while (tracker.queue.isNotEmpty()) {
            val nextUrl = tracker.queue.poll()
            when {
                nextUrl.startsWith("//") ->
                    nextUrl.httpGetAndTrackProgress("${url.protocol()}${nextUrl}", tracker)
                nextUrl.startsWith("/") ->
                    nextUrl.httpGetAndTrackProgress("${url}${nextUrl}", tracker)
                nextUrl.startsWith(url) ->
                    nextUrl.httpGetAndTrackProgress(nextUrl, tracker)
            }
        }

        return SiteMap(tracker.visited)
    }

    private fun String.httpGetAndTrackProgress(fullUrl: String, tracker: Tracker) {
        if (this in tracker.visited || this in tracker.unvisitable || this in tracker.queue) {
            return
        }

        val newUrls = fullUrl.httpGetAndFindLinksOrNull()
        if (newUrls == null) {
            tracker.unvisitable.add(this)
        } else {
            tracker.visited[this] = newUrls
            tracker.queue.addAll(newUrls)
        }
    }
}
