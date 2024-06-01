package co.tiagoaguiar.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie

class CategoryAdpter(private val categories: List<Category>,
    private val onItemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<CategoryAdpter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)

    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: Category) {
            val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
            txtTitle.text = category.title

            val rvCategory: RecyclerView = itemView.findViewById(R.id.rv_category)
            rvCategory.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            rvCategory.adapter = MovieAdpter(category.movies, R.layout.movie_item, onItemClickListener)
        // -> Lista de filmes e o layout que eu quero inflar



        }
    }

}
