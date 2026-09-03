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

  @Test
  fun testDefaultEpisode2Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_2_URL
    val videoId = YouTubeHelper.extractVideoId(defaultUrl)
    assertEquals("1KsKnrc7ojM", videoId)
  }

  @Test
  fun testDefaultEpisode3Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_3_URL
    val videoId = YouTubeHelper.extractVideoId(defaultUrl)
    assertEquals("nfVYJ6jFvRA", videoId)
    val html = YouTubeHelper.buildCamouflagedHtml(videoId!!)
    assertNotNull(html)
    assertTrue(html.contains("youtube-nocookie.com/embed/nfVYJ6jFvRA"))
  }

  @Test
  fun testDefaultEpisode4Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_4_URL
    val videoId = YouTubeHelper.extractVideoId(defaultUrl)
    assertEquals("MjHWLBEyPuA", videoId)
    val html = YouTubeHelper.buildCamouflagedHtml(videoId!!)
    assertNotNull(html)
    assertTrue(html.contains("youtube-nocookie.com/embed/MjHWLBEyPuA"))
  }

  @Test
  fun testDefaultEpisode5Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_5_URL
    val videoId = YouTubeHelper.extractVideoId(defaultUrl)
    assertEquals("okmDuMzlbWM", videoId)
    val html = YouTubeHelper.buildCamouflagedHtml(videoId!!)
    assertNotNull(html)
    assertTrue(html.contains("youtube-nocookie.com/embed/okmDuMzlbWM"))
  }

  @Test
  fun testDefaultEpisode6Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_6_URL
    val videoId = YouTubeHelper.extractVideoId(defaultUrl)
    assertEquals("-dQl0VDN07c", videoId)
    val html = YouTubeHelper.buildCamouflagedHtml(videoId!!)
    assertNotNull(html)
    assertTrue(html.contains("youtube-nocookie.com/embed/-dQl0VDN07c"))
  }

  @Test
  fun testDefaultEpisode7Url() {
    val defaultUrl = DonoDoMorroManager.DEFAULT_EPISODE_7_URL
    val videoId = YouTubeHelper.extractVideoId(defaultUrl)
    assertEquals("4KE0NczMVwI", videoId)
    val html = YouTubeHelper.buildCamouflagedHtml(videoId!!)
    assertNotNull(html)
    assertTrue(html.contains("youtube-nocookie.com/embed/4KE0NczMVwI"))
  }

  @Test
  fun testYouTubeThumbnailGenerators() {
    val thumb = YouTubeHelper.getThumbnailUrl(DonoDoMorroManager.DEFAULT_EPISODE_1_URL)
    assertEquals("https://img.youtube.com/vi/u0WXCHgZxaY/maxresdefault.jpg", thumb)

    val hqThumb = YouTubeHelper.getHqThumbnailUrl(DonoDoMorroManager.DEFAULT_EPISODE_7_URL)
    assertEquals("https://img.youtube.com/vi/4KE0NczMVwI/hqdefault.jpg", hqThumb)
  }
}

