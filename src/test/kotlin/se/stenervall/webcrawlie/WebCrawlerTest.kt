package se.stenervall.webcrawlie

import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.semantics.Action.resourceContent
import com.xebialabs.restito.semantics.Condition.get
import com.xebialabs.restito.server.StubServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WebCrawlerTest {
    private lateinit var site: StubServer
    private lateinit var url: String

    private val target = WebCrawler()

    @Before
    fun setUp() {
        site = StubServer(58080).run()
        url = "http://localhost:${site.port}"
        whenHttp(site).match(get("/")).then(resourceContent("index.html"))
        whenHttp(site).match(get("/about.html")).then(resourceContent("about.html"))
        whenHttp(site).match(get("/news.html")).then(resourceContent("news.html"))
        whenHttp(site).match(get("/news1.html")).then(resourceContent("news1.html"))
        whenHttp(site).match(get("/news2.html")).then(resourceContent("news2.html"))
        whenHttp(site).match(get("/news3.html")).then(resourceContent("news3.html"))
    }

    @After
    fun tearDown() {
        site.stop()
    }

    @Test
    fun crawlSimple() {
        val result = target.crawl(url)

        assertEquals(result.toString(), 6, result.size)
        assertTrue(result.contains("/"))
        assertTrue(result.contains("/about.html"))
        assertTrue(result.contains("/news.html"))
        assertTrue(result.contains("/news1.html"))
        assertTrue(result.contains("/news2.html"))
        assertTrue(result.contains("${url}/news3.html"))

        assertTrue(result["/"]!!.contains("/about.html"))
        assertTrue(result["/"]!!.contains("/news.html"))
    }

    @Test
    fun followAbsoluteLinksOnSameDomain() {
        val result = target.crawl(url)

        assertTrue(result.contains("/news.html"))
        assertTrue(result["/news.html"]!!.contains("${url}/news3.html"))
    }
}
