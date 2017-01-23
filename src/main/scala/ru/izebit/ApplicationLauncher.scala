package ru.izebit

import org.springframework.boot.SpringApplication
import ru.izebit.config.BaseConfiguration

object ApplicationLauncher extends App{
    SpringApplication.run(classOf[BaseConfiguration])
}

