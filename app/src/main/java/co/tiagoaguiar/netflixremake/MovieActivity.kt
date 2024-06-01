package co.tiagoaguiar.netflixremake

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.model.MovieDetail
import co.tiagoaguiar.netflixremake.util.MovieTask
import com.squareup.picasso.Picasso

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var textTitle: TextView
    private lateinit var textCast: TextView
    private lateinit var textDesc: TextView
    private lateinit var adapter: MovieAdpter
    private lateinit var progress: ProgressBar
    private val movies = mutableListOf<Movie>() // lista de filmes de X filmes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        textTitle = findViewById(R.id.movie_txt_title)
        textCast = findViewById(R.id.movie_txt_cast)
        textDesc = findViewById(R.id.movie_txt_desc)
        progress = findViewById(R.id.movie_progress)

        val rv: RecyclerView = findViewById(R.id.movie_rv_similar)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalAccessException("ID Not Found !")
        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=f2d39a7f-eb23-4986-ba9f-80dd969a1c15"

        MovieTask(this).execute(url)


        adapter = MovieAdpter(movies, R.layout.movie_item_similares)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.btn_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        progress.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }

    override fun onResult(movieDetail: MovieDetail) {
        progress.visibility = View.GONE

        textTitle.text = movieDetail.movie.title
        textDesc.text = movieDetail.movie.desc
        textCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        val coverImg: ImageView = findViewById(R.id.movie_img)

        Picasso.get().load(movieDetail.movie.coverUrl).into(object : com.squareup.picasso.Target {

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

                val errorMessage: String = "Não foi possível carregar a imagem do filme!"
                Toast.makeText(this@MovieActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                progress.visibility = View.VISIBLE
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(
                    this@MovieActivity,
                    R.drawable.shadows
                ) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)

                progress.visibility = View.GONE

                coverImg.setImageDrawable(layerDrawable)
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}

