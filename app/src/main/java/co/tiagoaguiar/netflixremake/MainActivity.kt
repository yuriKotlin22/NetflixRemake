package co.tiagoaguiar.netflixremake

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progressBar: ProgressBar
    private var categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdpter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.mainProgressBar)

        adapter = CategoryAdpter(categories) {id ->
            val intent = Intent(this@MainActivity,MovieActivity ::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

        }
        val rvMain: RecyclerView = findViewById(R.id.rv_main)
        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=f2d39a7f-eb23-4986-ba9f-80dd969a1c15")

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE

    }

    override fun onResult(categories: List<Category>) {
        progressBar.visibility = View.GONE
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }
}