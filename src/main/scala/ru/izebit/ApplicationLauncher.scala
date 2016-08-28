package ru.izebit

import org.springframework.boot.SpringApplication
import ru.izebit.config.BaseConfiguration

object ApplicationLauncher {

  def main(args: Array[String]): Unit = {

    SpringApplication.run(classOf[BaseConfiguration])
  }
}

