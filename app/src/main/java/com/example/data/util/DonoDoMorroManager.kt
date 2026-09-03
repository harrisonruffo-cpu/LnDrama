package com.example.data.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Episode

object DonoDoMorroManager {
    private const val PREFS_NAME = "dono_do_morro_prefs"
    private const val KEY_CUSTOM_EPISODE_1 = "custom_episode_1_url"
    private const val KEY_FOLLOWERS_BASE = "followers_base_count"

    // Link padrão oficial do Episódio 1 com suporte a YouTube camuflado
    const val DEFAULT_EPISODE_1_URL = "https://youtu.be/u0WXCHgZxaY?is=bvomW3X72476KQDG"

    // Perfil Oficial do ADM e Desenvolvedor
    const val OFFICIAL_ADM_NAME = "Harrison Ruffo"
    const val OFFICIAL_ADM_EMAIL = "harrisonruffo@gmail.com"
    const val OFFICIAL_ADM_ROLE = "Desenvolvedor & ADM Oficial"
    const val OFFICIAL_ADM_PHOTO_URL = "https://lh3.googleusercontent.com/d/1VWIfZ8lcuPWCc2ijTwvX6WoWnbqkUpO7"
    const val BASE_FOLLOWERS_COUNT = 28542

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getEpisode1Url(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_EPISODE_1, DEFAULT_EPISODE_1_URL)
            ?: DEFAULT_EPISODE_1_URL
    }

    fun setEpisode1Url(context: Context, url: String) {
        val clean = url.trim()
        getPrefs(context).edit().putString(KEY_CUSTOM_EPISODE_1, clean).apply()
    }

    fun resetEpisode1Url(context: Context) {
        getPrefs(context).edit().putString(KEY_CUSTOM_EPISODE_1, DEFAULT_EPISODE_1_URL).apply()
    }

    fun getFollowersCount(context: Context): Int {
        val extra = getPrefs(context).getInt(KEY_FOLLOWERS_BASE, 0)
        return BASE_FOLLOWERS_COUNT + extra + 1 // +1 for current user auto-follow
    }

    fun registerAppUserFollow(context: Context) {
        val current = getPrefs(context).getInt(KEY_FOLLOWERS_BASE, 0)
        getPrefs(context).edit().putInt(KEY_FOLLOWERS_BASE, current + 1).apply()
    }

    fun getEpisodes(context: Context): List<Episode> {
        val ep1Url = getEpisode1Url(context)
        return listOf(
            Episode(
                id = "dono_morro_ep_1",
                episodeNumber = 1,
                title = "O Primeiro Olhar - O Encontro no Morro",
                duration = "1:45",
                videoUrl = ep1Url,
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "Clara sobe a comunidade em busca de respostas e cruza olhares pela primeira vez com o homem mais temido e respeitado do Dendê.",
                likesCount = 4820
            ),
            Episode(
                id = "dono_morro_ep_2",
                episodeNumber = 2,
                title = "Território Dividido",
                duration = "2:10",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "A tensão aumenta quando invasores cercam o mirante. Um segredo do passado vem à tona ameaçando a paz do morro.",
                likesCount = 3915
            ),
            Episode(
                id = "dono_morro_ep_3",
                episodeNumber = 3,
                title = "Segredos do Asfalto",
                duration = "1:55",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                isUnlocked = true,
                coinsCost = 10,
                synopsis = "A alta cúpula descobre as visitas frequentes de Clara. Um ultimato coloca a lealdade da família à prova.",
                likesCount = 3120
            ),
            Episode(
                id = "dono_morro_ep_4",
                episodeNumber = 4,
                title = "O Beijo Proibido",
                duration = "2:30",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                isUnlocked = false,
                coinsCost = 15,
                synopsis = "Na laje iluminada pelo luar do Rio, a atração se torna irresistível e quebra todas as regras estabelecidas.",
                likesCount = 2840
            ),
            Episode(
                id = "dono_morro_ep_5",
                episodeNumber = 5,
                title = "Pacto de Sangue",
                duration = "2:15",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                isUnlocked = false,
                coinsCost = 20,
                synopsis = "Uma emboscada na baixada exige uma aliança improvável para salvar a vida de quem ele jurou proteger.",
                likesCount = 2410
            ),
            Episode(
                id = "dono_morro_ep_6",
                episodeNumber = 6,
                title = "A Grande Revelação",
                duration = "2:40",
                videoUrl = ep1Url, // Permite assistir novamente com camuflagem
                isUnlocked = false,
                coinsCost = 25,
                synopsis = "A verdade sobre a herança roubada explode na cara dos vilões em um confronto eletrizante.",
                likesCount = 1990
            )
        )
    }
}
