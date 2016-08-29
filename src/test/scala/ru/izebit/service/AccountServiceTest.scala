package ru.izebit.service

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.mockito.MockitoSugar
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import ru.izebit.config.BaseConfiguration

/**
  * Created by Artem Konovalov on 8/29/16.
  */

@ContextConfiguration(
  classes = Array(classOf[BaseConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class AccountServiceTest extends AssertionsForJUnit with MockitoSugar {

  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Test
  def addAccount(): Unit = {


  }
}
