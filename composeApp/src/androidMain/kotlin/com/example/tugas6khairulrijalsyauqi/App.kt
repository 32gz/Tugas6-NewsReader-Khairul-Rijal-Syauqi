package com.example.tugas6khairulrijalsyauqi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.* 
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class NewsResponse(
    val status: String,
    val articles: List<Article>
)

@Serializable
data class Article(
    val title: String?,
    val description: String?,
    val urlToImage: String?,
    val content: String? = null,
    val author: String? = null,
    val publishedAt: String? = null
)


sealed class NewsUiState {
    data object Loading : NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}



class NewsRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }


    private val apiKey = "cb5f1a0b55e64145ba48647d51078a32"
    private val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=cb5f1a0b55e64145ba48647d51078a32"

    suspend fun getNews(): Result<List<Article>> {
        return try {
            val response: NewsResponse = client.get(url).body()
            if (response.status == "ok") {
                Result.success(response.articles.filter { it.title != "[Removed]" })
            } else {
                Result.failure(Exception("Gagal mengambil data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


class NewsViewModel(private val repository: NewsRepository) {
    var uiState by mutableStateOf<NewsUiState>(NewsUiState.Loading)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        loadNews()
    }

    fun loadNews() {
        uiState = NewsUiState.Loading
        fetchData()
    }

    fun refresh() {
        isRefreshing = true
        fetchData()
    }

    private fun fetchData() {
        scope.launch {
            repository.getNews()
                .onSuccess {
                    uiState = NewsUiState.Success(it)
                    isRefreshing = false
                }
                .onFailure {
                    uiState = NewsUiState.Error(it.message ?: "Koneksi Bermasalah")
                    isRefreshing = false
                }
        }
    }
}



@Composable
fun App() {
    val repository = remember { NewsRepository() }
    val viewModel = remember { NewsViewModel(repository) }
    var selectedArticle by remember { mutableStateOf<Article?>(null) }

    MaterialTheme {
        if (selectedArticle == null) {
            ListScreen(
                viewModel = viewModel,
                onArticleClick = { selectedArticle = it }
            )
        } else {
            DetailScreen(
                article = selectedArticle!!,
                onBack = { selectedArticle = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(viewModel: NewsViewModel, onArticleClick: (Article) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("News Reader 123140143", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        // State ini sekarang dikenali karena import androidx.compose.material3.pulltorefresh.*
        val pullToRefreshState = rememberPullToRefreshState()

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            PullToRefreshBox(
                isRefreshing = viewModel.isRefreshing,
                onRefresh = { viewModel.refresh() },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = viewModel.uiState) {
                    is NewsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is NewsUiState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.articles) { article ->
                                ArticleCard(article, onArticleClick)
                            }
                        }
                    }
                    is NewsUiState.Error -> {
                        ErrorView(state.message) { viewModel.loadNews() }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article, onClick: (Article) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick(article) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = article.urlToImage ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    article.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    article.description ?: "No Description available.",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(article: Article, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Berita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(article.title ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Author: ${article.author ?: "Unknown"} | ${article.publishedAt?.take(10) ?: ""}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                article.content ?: article.description ?: "Isi berita tidak tersedia.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Text(message, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("Coba Lagi")
        }
    }
}