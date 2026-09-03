package com.example

import com.example.data.util.DonoDoMorroManager
import com.example.data.util.YouTubeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testExtractVideoIdFromUserLink() {
    val userLink = "https://youtu.be/u0WXCHgZxaY?is=bvomW3X72476KQDG"
    val videoId = YouTubeHelper.extractVideoId(userLink)
    assertEquals("u0WXCHgZxaY", videoId)
  }

  @Test
  fun testDefaultEpisode1Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_1_URL
    assertTrue(defaultUrl.contains("u0WXCHgZxaY"))
    val html = YouTubeHelper.buildCamouflagedHtml("u0WXCHgZxaY")
    assertNotNull(html)
    assertTrue(html.contains("youtube-nocookie.com/embed/u0WXCHgZxaY"))
    assertTrue(html.contains("modestbranding=1"))
  }
}

