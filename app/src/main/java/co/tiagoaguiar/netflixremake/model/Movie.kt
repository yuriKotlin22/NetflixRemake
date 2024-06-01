package co.tiagoaguiar.netflixremake.model


data class Movie(
    val id: Int,
    val coverUrl: String,
    val title: String = "",
    val cast: String = "",
    val desc: String = ""
)
