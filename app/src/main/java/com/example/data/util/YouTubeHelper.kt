package com.example.data.util

object YouTubeHelper {

    fun extractVideoId(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val clean = url.trim()

        val patterns = listOf(
            Regex("""(?:youtu\.be/|youtube\.com/watch\?v=|youtube\.com/shorts/|youtube\.com/embed/)([a-zA-Z0-9_-]{11})"""),
            Regex("""[?&]v=([a-zA-Z0-9_-]{11})"""),
            Regex("""^[a-zA-Z0-9_-]{11}$""")
        )

        for (pattern in patterns) {
            val match = pattern.find(clean)
            if (match != null) {
                return match.groupValues.lastOrNull() ?: match.value
            }
        }
        return null
    }

    fun isYouTubeUrl(url: String?): Boolean {
        return extractVideoId(url) != null
    }

    fun buildCamouflagedHtml(videoId: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                    background-color: #000000;
                    overflow: hidden;
                    -webkit-touch-callout: none;
                    -webkit-user-select: none;
                    user-select: none;
                }
                html, body {
                    width: 100%;
                    height: 100%;
                    background: #000000;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                #player-wrapper {
                    position: relative;
                    width: 100%;
                    height: 100%;
                    overflow: hidden;
                }
                iframe {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    border: 0;
                    object-fit: cover;
                }
                /* Anti-branding overlay to protect against accidental external clicks */
                .protection-shield {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 60px;
                    z-index: 10;
                    background: transparent;
                }
            </style>
        </head>
        <body>
            <div id="player-wrapper">
                <div class="protection-shield"></div>
                <iframe 
                    id="litoral-player"
                    src="https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&mute=0&controls=0&modestbranding=1&rel=0&playsinline=1&iv_load_policy=3&showinfo=0&fs=0&enablejsapi=1"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowfullscreen>
                </iframe>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}
