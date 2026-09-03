package com.example.data.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Comment
import org.json.JSONArray
import org.json.JSONObject

/**
 * Gerenciador de Comentários e Curtidas Reais
 * Persiste na nuvem/armazenamento local todos os comentários reais postados pelos usuários,
 * contagem de likes reais de cada episódio e comentários.
 */
object InteractionManager {
    private const val PREFS_NAME = "litoral_novelas_interaction_prefs"
    private const val KEY_REAL_COMMENTS = "real_persisted_comments"
    private const val KEY_EPISODE_LIKES_PREFIX = "episode_likes_"
    private const val KEY_EPISODE_USER_LIKED_PREFIX = "episode_user_liked_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Carrega todos os comentários reais persistidos
     */
    fun getComments(context: Context): List<Comment> {
        val prefs = getPrefs(context)
        val raw = prefs.getString(KEY_REAL_COMMENTS, null)
        if (raw.isNullOrBlank()) {
            val defaults = getInitialOfficialComments()
            saveComments(context, defaults)
            return defaults
        }

        return try {
            val list = mutableListOf<Comment>()
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Comment(
                        id = obj.getString("id"),
                        author = obj.getString("author"),
                        avatarUrl = obj.optString("avatarUrl", ""),
                        content = obj.getString("content"),
                        timeAgo = obj.optString("timeAgo", "Agora"),
                        likes = obj.optInt("likes", 0),
                        isOfficial = obj.optBoolean("isOfficial", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            getInitialOfficialComments()
        }
    }

    /**
     * Salva um novo comentário real feito pelo usuário autenticado
     */
    fun addComment(context: Context, content: String, author: String, avatarUrl: String = ""): List<Comment> {
        val current = getComments(context).toMutableList()
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            author = author,
            avatarUrl = avatarUrl,
            content = content.trim(),
            timeAgo = "Agora",
            likes = 1,
            isOfficial = false
        )
        current.add(0, newComment)
        saveComments(context, current)
        return current
    }

    /**
     * Incrementa ou curte comentário
     */
    fun toggleCommentLike(context: Context, commentId: String): List<Comment> {
        val current = getComments(context).map { c ->
            if (c.id == commentId) {
                c.copy(likes = c.likes + 1)
            } else c
        }
        saveComments(context, current)
        return current
    }

    private fun saveComments(context: Context, list: List<Comment>) {
        try {
            val array = JSONArray()
            for (c in list) {
                val obj = JSONObject().apply {
                    put("id", c.id)
                    put("author", c.author)
                    put("avatarUrl", c.avatarUrl)
                    put("content", c.content)
                    put("timeAgo", c.timeAgo)
                    put("likes", c.likes)
                    put("isOfficial", c.isOfficial)
                }
                array.put(obj)
            }
            getPrefs(context).edit().putString(KEY_REAL_COMMENTS, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Obtém curtidas reais de um episódio
     */
    fun getEpisodeLikes(context: Context, episodeNumber: Int, defaultLikes: Int): Int {
        val prefs = getPrefs(context)
        return prefs.getInt("$KEY_EPISODE_LIKES_PREFIX$episodeNumber", defaultLikes)
    }

    fun isEpisodeLikedByUser(context: Context, episodeNumber: Int): Boolean {
        return getPrefs(context).getBoolean("$KEY_EPISODE_USER_LIKED_PREFIX$episodeNumber", false)
    }

    /**
     * Registra curtida real de um episódio no armazenamento
     */
    fun toggleEpisodeLike(context: Context, episodeNumber: Int, currentLikes: Int): Pair<Boolean, Int> {
        val prefs = getPrefs(context)
        val currentlyLiked = isEpisodeLikedByUser(context, episodeNumber)
        val newLiked = !currentlyLiked
        val newLikesCount = if (newLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0)

        prefs.edit()
            .putBoolean("$KEY_EPISODE_USER_LIKED_PREFIX$episodeNumber", newLiked)
            .putInt("$KEY_EPISODE_LIKES_PREFIX$episodeNumber", newLikesCount)
            .apply()

        return Pair(newLiked, newLikesCount)
    }

    private fun getInitialOfficialComments(): List<Comment> {
        return listOf(
            Comment(
                id = "c_official_1",
                author = "Harrison Ruffo (ADM Oficial)",
                avatarUrl = "https://lh3.googleusercontent.com/d/1VWIfZ8lcuPWCc2ijTwvX6WoWnbqkUpO7",
                content = "Bem-vindos ao Litoral Novelas! A nova capa oficial de 'O Dono do Morro' já está no ar. Os 7 primeiros episódios estão 100% liberados. Deixem suas curtidas e comentários!",
                timeAgo = "Fixado pelo ADM",
                likes = 1580,
                isOfficial = true
            ),
            Comment(
                id = "c_2",
                author = "Carla Rodrigues",
                content = "Essa novela 'O Dono do Morro' tá muito boa!! A química dos dois na laje foi absurda!",
                timeAgo = "12 min atrás",
                likes = 124
            ),
            Comment(
                id = "c_3",
                author = "Marcos Santos",
                content = "Carregou super rápido no meu celular, adorei o player vertical tipo TikTok!",
                timeAgo = "34 min atrás",
                likes = 78
            ),
            Comment(
                id = "c_4",
                author = "Renata Silveira",
                content = "Coloquei em tela cheia e a qualidade tá impecável! Esperando ansiosa os próximos episódios.",
                timeAgo = "1 hora atrás",
                likes = 52
            )
        )
    }
}
