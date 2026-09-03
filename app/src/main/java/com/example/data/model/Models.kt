package com.example.data.model

data class Drama(
    val id: String,
    val title: String,
    val synopsis: String,
    val coverUrl: String,
    val bannerUrl: String = "",
    val category: String,
    val episodesCount: Int,
    val rating: String = "4.9",
    val views: String = "1.2M",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val badge: String = "NOVO"
)

data class Episode(
    val id: String,
    val episodeNumber: Int,
    val title: String,
    val duration: String,
    val videoUrl: String,
    val isUnlocked: Boolean = true,
    val coinsCost: Int = 0,
    val synopsis: String = "",
    val likesCount: Int = 1240
)

data class Comment(
    val id: String,
    val author: String,
    val avatarUrl: String = "",
    val content: String,
    val timeAgo: String,
    val likes: Int = 0,
    val isOfficial: Boolean = false
)

data class UserProfile(
    val id: String = "user_current",
    val name: String = "Convidado VIP",
    val email: String = "usuario@litoralnovelas.com",
    val photoUrl: String = "",
    val isAdm: Boolean = false,
    val isDeveloper: Boolean = false,
    val isVip: Boolean = true,
    val coins: Int = 250,
    val isFollowingHarrison: Boolean = true
)
