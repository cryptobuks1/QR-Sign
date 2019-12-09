package danvim.qrsign.utils

import java.net.URL

class PageReader: PageReaderInterface {
    override fun readPage(url: URL): String {
        return url.readText()
    }
}