package danvim.qrsign.utils

import java.net.URL

interface PageReaderInterface {
    /**
     * Reads the web page and return the page content.
     * @param url
     */
    fun readPage(url: URL): String
}