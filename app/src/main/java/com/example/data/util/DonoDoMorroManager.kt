package com.example.data.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Episode

object DonoDoMorroManager {
    private const val PREFS_NAME = "dono_do_morro_prefs"
    private const val KEY_CUSTOM_EPISODE_1 = "custom_episode_1_url"
    private const val KEY_CUSTOM_EPISODE_2 = "custom_episode_2_url"
    private const val KEY_FOLLOWERS_BASE = "followers_base_count"

    // Links padrão oficiais dos episódios
    const val DEFAULT_EPISODE_1_URL = "https://youtu.be/u0WXCHgZxaY?is=bvomW3X72476KQDG"
    const val DEFAULT_EPISODE_2_URL = "https://youtu.be/1KsKnrc7ojM?is=XjVN827OTKU0Yoj7"

    // Perfil Oficial do ADM e Desenvolvedor
    const val OFFICIAL_ADM_NAME = "Harrison Ruffo"
    const val OFFICIAL_ADM_EMAIL = "harrisonruffo@gmail.com"
    const val OFFICIAL_ADM_ROLE = "Desenvolvedor & ADM Oficial"
    const val OFFICIAL_ADM_PHOTO_URL = "https://lh3.googleusercontent.com/d/1VWIfZ8lcuPWCc2ijTwvX6WoWnbqkUpO7"
    // Imagem principal oficial da novela: https://drive.google.com/file/d/1ngEUH5l0R0c58zZ-y26kTDqBwFv5dr64/view?usp=drivesdk
    const val OFFICIAL_SERIES_IMAGE_URL = "https://lh3.googleusercontent.com/d/1ngEUH5l0R0c58zZ-y26kTDqBwFv5dr64"
    const val BASE_FOLLOWERS_COUNT = 28542

    // Informações da Série Brasileira: Ação, Drama, Favela
    const val SERIES_TITLE = "O Dono do Morro"
    const val SERIES_CATEGORY = "Série Brasileira: Ação • Drama • Favela"
    const val SERIES_SYNOPSIS = "Uma emocionante superprodução brasileira onde poder, família, lealdade e uma paixão proibida entram em rota de colisão no comando do morro mais cobiçado da capital."

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

    fun getEpisode2Url(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_EPISODE_2, DEFAULT_EPISODE_2_URL)
            ?: DEFAULT_EPISODE_2_URL
    }

    fun setEpisode2Url(context: Context, url: String) {
        val clean = url.trim()
        getPrefs(context).edit().putString(KEY_CUSTOM_EPISODE_2, clean).apply()
    }

    fun resetEpisode2Url(context: Context) {
        getPrefs(context).edit().putString(KEY_CUSTOM_EPISODE_2, DEFAULT_EPISODE_2_URL).apply()
    }

    fun getFollowersCount(context: Context): Int {
        val extra = getPrefs(context).getInt(KEY_FOLLOWERS_BASE, 0)
        return BASE_FOLLOWERS_COUNT + extra + 1 // +1 for current user auto-follow
    }

    fun registerAppUserFollow(context: Context) {
        val current = getPrefs(context).getInt(KEY_FOLLOWERS_BASE, 0)
        getPrefs(context).edit().putInt(KEY_FOLLOWERS_BASE, current + 1).apply()
    }

    /**
     * 10 Episódios da Série Brasileira Ação Drama Favela:
     * - Episódios 1 a 7: DISPONÍVEIS (isUnlocked = true)
     * - Episódios 8, 9 e 10: COM CADEADO (isUnlocked = false)
     */
    fun getEpisodes(context: Context): List<Episode> {
        val ep1Url = getEpisode1Url(context)
        val ep2Url = getEpisode2Url(context)
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
                likesCount = 5820
            ),
            Episode(
                id = "dono_morro_ep_2",
                episodeNumber = 2,
                title = "Território Dividido",
                duration = "2:10",
                videoUrl = ep2Url,
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "A tensão aumenta quando invasores cercam o mirante. Um segredo do passado vem à tona ameaçando a paz do morro.",
                likesCount = 4915
            ),
            Episode(
                id = "dono_morro_ep_3",
                episodeNumber = 3,
                title = "Segredos do Asfalto",
                duration = "1:55",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "A alta cúpula descobre as visitas frequentes de Clara. Um ultimato coloca a lealdade da família à prova.",
                likesCount = 4120
            ),
            Episode(
                id = "dono_morro_ep_4",
                episodeNumber = 4,
                title = "O Beijo Proibido na Laje",
                duration = "2:30",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "Na laje iluminada pelo luar do Rio, a atração se torna irresistível e quebra todas as regras estabelecidas.",
                likesCount = 3840
            ),
            Episode(
                id = "dono_morro_ep_5",
                episodeNumber = 5,
                title = "Pacto de Sangue e Honra",
                duration = "2:15",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "Uma emboscada na baixada exige uma aliança improvável para salvar a vida de quem ele jurou proteger.",
                likesCount = 3410
            ),
            Episode(
                id = "dono_morro_ep_6",
                episodeNumber = 6,
                title = "Invasão na Madrugada",
                duration = "2:40",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "Tiros ecoam no beco principal. Clara decide enfrentar o perigo para salvar o amor de sua vida.",
                likesCount = 3190
            ),
            Episode(
                id = "dono_morro_ep_7",
                episodeNumber = 7,
                title = "A Fuga pelas Vielas",
                duration = "2:20",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                isUnlocked = true,
                coinsCost = 0,
                synopsis = "Cercados pelos inimigos, uma rota de fuga secreta pelas lajes se torna a única esperança de sobrevivência.",
                likesCount = 2950
            ),
            Episode(
                id = "dono_morro_ep_8",
                episodeNumber = 8,
                title = "Confronto no Mirante",
                duration = "2:35",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                isUnlocked = false, // COM CADEADO
                coinsCost = 15,
                synopsis = "No ponto mais alto da favela, velhas contas são acertadas cara a cara com o traidor do movimento.",
                likesCount = 2710
            ),
            Episode(
                id = "dono_morro_ep_9",
                episodeNumber = 9,
                title = "A Traição Revelada",
                duration = "2:50",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                isUnlocked = false, // COM CADEADO
                coinsCost = 20,
                synopsis = "Uma gravação secreta desmascara quem estava vendendo as informações da comunidade aos rivais.",
                likesCount = 2540
            ),
            Episode(
                id = "dono_morro_ep_10",
                episodeNumber = 10,
                title = "O Julgamento Final do Morro",
                duration = "3:10",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                isUnlocked = false, // COM CADEADO
                coinsCost = 25,
                synopsis = "O grande desfecho da temporada: a comunidade se une para a decisão que mudará o destino de todos.",
                likesCount = 2380
            )
        )
    }
}
