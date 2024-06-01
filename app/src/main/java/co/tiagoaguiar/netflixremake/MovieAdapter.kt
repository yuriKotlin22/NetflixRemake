package co.tiagoaguiar.netflixremake

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Movie
import com.squareup.picasso.Picasso

class MovieAdpter(
    private val movies: List<Movie>, @LayoutRes private val layoutId: Int,
    private val onItemClickListner: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<MovieAdpter.MovieViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view)

    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)

    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(movie: Movie) { // adicionando o nome da URL no text
            val imageCover: ImageView = itemView.findViewById(R.id.img_cover)

            imageCover.setOnClickListener {
                onItemClickListner?.invoke(movie.id)

            }

            Picasso.get().load(movie.coverUrl).into(imageCover)

        }
    }

}
