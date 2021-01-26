package com.fakir.samples.configuration

import scala.io.Source

object ConfigReader {
  def readConfig(configPath: String): String = {
    Source.fromFile(configPath).getLines().mkString
  }
}
