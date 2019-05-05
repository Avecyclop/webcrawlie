package se.stenervall.webcrawlie

class WebCrawler {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                System.err.println("Expected single argument: <url>")
                System.exit(1)
            }
            val siteMap = WebCrawler().crawl(args[0])
            siteMap.writeToFile("sitemap.txt")
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
        if (this in tracker.visited.keys || this in tracker.unvisitable || this in tracker.queue) {
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
