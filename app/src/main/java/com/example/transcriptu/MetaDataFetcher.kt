package com.example.transcriptu

import com.example.transcriptu.data.modal.MetaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MetaDataFetcher {

    suspend fun getMetadata(url: String): MetaData{
       return withContext(Dispatchers.IO){
            val document = Jsoup.connect(url)
                .timeout(60000)
                .get()
           parseHtml(document.html(), url)
       }
    }


    private fun parseHtml(html : String, url: String): MetaData{

        val document = Jsoup.parse(html)

        val title = document.select("meta[property=og:title]").first()?.attr("content")
        val description = document.select("meta[property=og:description]").first()?.attr("content")
        val thumbnailUrl = document.select("meta[property=og:image]").first()?.attr("content")
        return MetaData(
            title = title?.ifEmpty { null },
            description = description?.ifEmpty { null },
            thumbnailUrl = thumbnailUrl?.ifEmpty { null },
            videoUrl = url
        )

    }
}