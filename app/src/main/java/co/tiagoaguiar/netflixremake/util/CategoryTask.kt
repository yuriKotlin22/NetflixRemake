package co.tiagoaguiar.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        callback.onPreExecute()

        executor.execute {
            var urlConection: HttpsURLConnection? = null
            var stream: InputStream? = null
            var jsonAsString: String? = null

            try {
                val requestURL = URL(url)
                urlConection = requestURL.openConnection() as HttpsURLConnection
                urlConection.readTimeout = 2000
                urlConection.connectTimeout = 2000

                val statusCode = urlConection.responseCode

                if (statusCode > 400) {
                    throw IOException("Communication error with the server")
                }

                stream = urlConection.inputStream
                jsonAsString = stream.bufferedReader().use { it.readText() } // byters -> String

                val categories = toCategories(jsonAsString) // -> função

                handler.post {
                    callback.onResult(categories)
                }


            } catch (e: IOException) {
                val message = e.message ?: "Unknown Erro"
                Log.e("Teste", message)

                handler.post {
                    callback.onFailure(message)
                }

            } finally {
                urlConection?.disconnect()
                stream?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String): List<Category> {

        val categories = mutableListOf<Category>()

        val jasonRoot = JSONObject(jsonAsString)
        val jsonCategories = jasonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()) {

            val jsonCategory = jsonCategories.getJSONObject(i) //-> Ele recebe a categoria atual
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>() //-> adicionado filmes e suas propriedades (id, nome)
            for (j in 0 until jsonMovies.length()) {

                val jsonMovie = jsonMovies.getJSONObject(j)//->Lista atual de filmes
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))

        }

        return categories
    }

}