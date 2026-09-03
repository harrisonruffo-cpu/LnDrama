package com.example.data.repository

import com.example.data.model.Comment
import com.example.data.model.Drama
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NovelaRepository {

    private val _dramas = MutableStateFlow<List<Drama>>(emptyList())
    val dramas: StateFlow<List<Drama>> = _dramas.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastUpdatedTime = MutableStateFlow("Recém atualizado")
    val lastUpdatedTime: StateFlow<String> = _lastUpdatedTime.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(setOf("dono_do_morro", "vidas_cruzadas"))
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    init {
        loadInitialCatalog()
        loadInitialComments()
    }

    private fun loadInitialCatalog() {
        _dramas.value = listOf(
            Drama(
                id = "dono_do_morro",
                title = "O Dono do Morro",
                synopsis = "No comando do morro mais cobiçado da capital, um líder implacável vê seu império balançar quando uma médica da zona sul invade seu destino.",
                coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1200&auto=format&fit=crop&q=80",
                category = "Ação & Romance",
                episodesCount = 12,
                rating = "4.9",
                views = "2.8M",
                isFeatured = true,
                isTrending = true,
                badge = "DESTAQUE VIP"
            ),
            Drama(
                id = "vidas_cruzadas",
                title = "Vidas Cruzadas no Litoral",
                synopsis = "Duas irmãs separadas na infância descobrem que amam o mesmo homem, um herdeiro de marinas luxuosas no litoral norte.",
                coverUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800&auto=format&fit=crop&q=80",
                category = "Romance & Traição",
                episodesCount = 18,
                rating = "4.8",
                views = "1.5M",
                isTrending = true,
                badge = "POPULAR"
            ),
            Drama(
                id = "heranca_oculta",
                title = "Herança de Sangue",
                synopsis = "Após o desaparecimento misterioso de um magnata, seus herdeiros entram em uma guerra sangrenta por poder e segredos de família.",
                coverUrl = "https://images.unsplash.com/photo-1509114397022-ed747cca3f65?w=800&auto=format&fit=crop&q=80",
                category = "Suspense & Vingança",
                episodesCount = 15,
                rating = "4.9",
                views = "980K",
                isTrending = true,
                badge = "ESTREIA"
            ),
            Drama(
                id = "amor_na_praia",
                title = "Amor Proibido na Praia",
                synopsis = "O campeão de surf local e a filha de um promotor rigoroso vivem uma paixão ardente que enfrenta o preconceito da alta sociedade.",
                coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
                category = "Comédia Romântica",
                episodesCount = 14,
                rating = "4.7",
                views = "820K",
                badge = "NOVO EPISÓDIO"
            ),
            Drama(
                id = "destinos_em_chamas",
                title = "Destinos em Chamas",
                synopsis = "Uma perseguição policial de tirar o fôlego desencadeia uma teia de intrigas onde ninguém é quem realmente parece ser.",
                coverUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=800&auto=format&fit=crop&q=80",
                category = "Ação Criminal",
                episodesCount = 10,
                rating = "4.8",
                views = "1.1M",
                badge = "BOMBA"
            )
        )
    }

    private fun loadInitialComments() {
        _comments.value = listOf(
            Comment(
                id = "c_official_1",
                author = "Harrison Ruffo (ADM Oficial)",
                avatarUrl = "https://lh3.googleusercontent.com/d/1VWIfZ8lcuPWCc2ijTwvX6WoWnbqkUpO7",
                content = "Bem-vindos ao Litoral Novelas! O Ep 1 de 'O Dono do Morro' está liberado com suporte especial anti-bloqueio. Deixem suas opiniões!",
                timeAgo = "Fixado pelo ADM",
                likes = 1240,
                isOfficial = true
            ),
            Comment(
                id = "c_2",
                author = "Carla Rodrigues",
                content = "Essa novela 'O Dono do Morro' tá muito boa!! A química dos dois na laje foi absurda!",
                timeAgo = "12 min atrás",
                likes = 89
            ),
            Comment(
                id = "c_3",
                author = "Marcos Santos",
                content = "Carregou super rápido no meu celular, adorei o player vertical tipo TikTok!",
                timeAgo = "34 min atrás",
                likes = 54
            )
        )
    }

    suspend fun refreshCatalog() {
        _isRefreshing.value = true
        delay(950) // Simulate network fetch latency

        // Simulate updated views, random new episode badge, and latest timestamp
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        _lastUpdatedTime.value = "Atualizado às ${timeFormat.format(Date())}"

        val currentList = _dramas.value
        _dramas.value = currentList.map { drama ->
            if (drama.id == "dono_do_morro") {
                val viewsInt = (2800 + (10..50).random())
                drama.copy(views = "${viewsInt / 1000.0}M", badge = "🔥 EPISÓDIO ATUALIZADO")
            } else {
                drama
            }
        }
        _isRefreshing.value = false
    }

    fun toggleFavorite(dramaId: String) {
        val set = _favorites.value.toMutableSet()
        if (set.contains(dramaId)) {
            set.remove(dramaId)
        } else {
            set.add(dramaId)
        }
        _favorites.value = set
    }

    fun addComment(content: String, author: String = "Você (Seguidor Oficial)") {
        if (content.isBlank()) return
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            author = author,
            content = content.trim(),
            timeAgo = "Agora",
            likes = 1
        )
        _comments.value = listOf(newComment) + _comments.value
    }
}
