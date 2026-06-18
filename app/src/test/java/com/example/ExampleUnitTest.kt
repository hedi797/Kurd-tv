package com.example

import org.junit.Test
import java.net.URL

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    val playlists = listOf(
      "https://iptv-org.github.io/iptv/countries/iq.m3u",
      "https://iptv-org.github.io/iptv/languages/kur.m3u"
    )
    
    for (url in playlists) {
      try {
        println("=== FETCHING PLAYLIST: $url ===")
        val content = URL(url).readText()
        val lines = content.lines()
        println("Total lines: ${lines.size}")
        
        for (i in lines.indices) {
          val line = lines[i]
          if (line.contains("ava", ignoreCase = true) || line.contains("ئاڤا", ignoreCase = true)) {
            println("Found AVA entry at line $i:")
            println("  Line: $line")
            if (i + 1 < lines.size) {
              println("  Stream URL: ${lines[i + 1]}")
            }
          }
        }
      } catch (e: Exception) {
        println("Failed to fetch $url: ${e.message}")
      }
    }
  }
}
