package org.scalafmt

import scala.collection.mutable

import java.util.concurrent.TimeUnit

import org.scalafmt.internal.FormatOps
import org.scalafmt.internal.FormatToken
import org.scalafmt.internal.Split
import org.scalafmt.internal.State

/**
  * (ugly) Utility to collect data about formatter.
  *
  * Only used during development.
  */
object Debug {

  val formatTokenExplored =
    mutable.Map.empty[FormatToken, Int].withDefaultValue(0)
  val enqueuedSplits = mutable.Set.empty[Split]
  var formatOps: FormatOps = _
  var lastTestExplored = 0
  var explored = 0
  var state = State.start
  def tokens = formatOps.tokens.arr
  var startTime = System.nanoTime()

  def newTest(): Unit = {
    startTime = System.nanoTime()
    lastTestExplored = explored
  }

  def ns2ms(nanoseconds: Long): Long =
    TimeUnit.MILLISECONDS.convert(nanoseconds, TimeUnit.NANOSECONDS)

  def elapsedNs = System.nanoTime() - startTime

  def exploredInTest = explored - lastTestExplored

  def maxVisitedToken: Int = {
    if (tokens.isEmpty) 0
    else {
      val maxTok = tokens.maxBy(x => formatTokenExplored.getOrElse(x, 0))
      formatTokenExplored.getOrElse(maxTok, -1)
    }
  }

  def enqueued(split: Split): Unit = {
    enqueuedSplits += split
  }

  def visit(token: FormatToken): Unit = {
    val visits = formatTokenExplored.getOrElse(token, 0) + 1
    formatTokenExplored += token -> visits
  }

}
