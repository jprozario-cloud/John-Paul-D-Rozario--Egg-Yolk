package com.example.speech

import kotlin.random.Random

object YolkVoiceLines {

  fun get5MinuteAlert(taskTitle: String): String {
    val cleanTitle = if (taskTitle.isBlank()) "your next task" else taskTitle
    val templates = listOf(
      "Egg-cuse me! $cleanTitle begins in just 5 minutes! Let's get cracking and do this!",
      "Heads up, superstar! $cleanTitle is in 5 minutes! You're going to crush it!",
      "Ding ding! $cleanTitle starts in 5 minutes! Time to shine sunny-side up!",
      "Attention champ! Just 5 minutes until $cleanTitle! Let's roll into action!",
      "Egg-stra! Egg-stra! $cleanTitle kicks off in 5 minutes! You've totally got this!",
      "Tick-tock egg-clock! $cleanTitle is happening in 5 minutes! I believe in you!"
    )
    return templates[Random.nextInt(templates.size)]
  }

  fun getTaskCompleted(): String {
    val lines = listOf(
      "Egg-straordinary job! You totally scrambled that to-do item!",
      "Woohoo! Another win in the basket! You're on fire today!",
      "Sunny-side victory! You checked that off like an absolute champion!",
      "That was egg-ceptional! I'm so proud of your awesome focus!",
      "Boom! Task conquered! Keep that golden energy flowing!"
    )
    return lines[Random.nextInt(lines.size)]
  }

  fun getPokeReaction(): String {
    val lines = listOf(
      "Hehe, that tickles! Don't worry, your trusty egg buddy is on duty!",
      "Egg-cellent tap! Let's stay energized and keep rolling forward!",
      "Boing! Feeling 100 percent sunny-side up and ready for action!",
      "Cracking good to see you! You make every single moment brighter!",
      "Whoa! I'm bouncy, I'm sunny, and I've got your schedule covered!"
    )
    return lines[Random.nextInt(lines.size)]
  }

  fun getGreeting(): String {
    val lines = listOf(
      "Sunny day, rockstar! I'm watching the clock so you can stay in the zone!",
      "Rise and shine, superstar! Today is bursting with egg-citing potential!",
      "Golden vibes only! Let's make today an egg-ceptional masterpiece!"
    )
    return lines[Random.nextInt(lines.size)]
  }

  fun getAllTasksDone(): String {
    return "Egg-emplary work, bestie! Everything is finished and you are pure gold!"
  }
}
