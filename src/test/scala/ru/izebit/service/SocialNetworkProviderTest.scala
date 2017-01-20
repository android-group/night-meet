package ru.izebit.service

import org.scalatest.FunSuite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.AnnotationConfigContextLoader
import ru.izebit.config.BaseConfiguration

/**
  * Created by artem on 1/20/17.
  */
@ContextConfiguration(
  classes = Array(classOf[BaseConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class SocialNetworkProviderTest extends FunSuite {

  @Autowired
  var socialNetworkProvider: SocialNetworkProvider = _

  test("test token") {
    //todo как то нужно протестить
  }

}
