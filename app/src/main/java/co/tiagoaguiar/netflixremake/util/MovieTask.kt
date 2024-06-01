package co.tiagoaguiar.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.model.MovieDetail
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import java.util.jar.JarInputStream
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onPreExecute()
        fun onResult(movieDetail: MovieDetail)
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

                if (statusCode == 400) {
                    stream = urlConection.errorStream
                    jsonAsString = stream.bufferedReader().use { it.readText() } // byters -> String

                    val json = JSONObject(jsonAsString)
                    val message = json.getString("message")

                    throw IOException(message)

                } else if (statusCode > 400) {
                    throw IOException("Communication error with the server")
                }

                stream = urlConection.inputStream
                jsonAsString = stream.bufferedReader().use { it.readText() } // byters -> String

                val movieDetail = toMovieDetail(jsonAsString) // -> função

                handler.post {
                    callback.onResult(movieDetail)
                }


            } catch (e: IOException) {
                val message = e.message ?: "Unknown erro"
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


    private fun toMovieDetail(jsonAsString: String): MovieDetail {
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similars = mutableListOf<Movie>()
        for (i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarCoverUrl)
            similars.add(m)
        }

        val movie = Movie(id, coverUrl, title, cast, desc)
        return MovieDetail(movie, similars)
    }

}