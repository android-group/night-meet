package ru.izebit.service

import org.junit.After
import org.mockito.Mockito._
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import ru.izebit.config.BaseConfiguration
import ru.izebit.dao.{AccountDao, SympathyDao}
import ru.izebit.model.Sex.MALE
import ru.izebit.model.{Relation, Sex}


/**
  * Created by Artem Konovalov on 8/29/16.
  */

@ContextConfiguration(
  classes = Array(classOf[BaseConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class AccountServiceTest extends FunSuite with MockitoSugar {

  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Autowired
  var accountService: AccountService = _
  @Autowired
  var accountDao: AccountDao = _
  @Autowired
  var sympathyDao: SympathyDao = _

  @After
  def dropAll(): Unit = {
    accountDao.dropAll()
    sympathyDao.dropAll()
  }


  test("add new account") {
    val id = "1"
    val city = 2
    val sex = MALE

    val socialNetworkProvider = mock[SocialNetworkProvider]
    when(socialNetworkProvider.getInfo(id)).thenReturn((city, sex))
    accountService.socialNetworkProvider = socialNetworkProvider

    accountService.addAccount(id)

    val account = accountService.getAccount(id)

    assert(account.offset == 0)
    assert(account.sex == sex)
    assert(account.city == city)
    assert(account.relations.isEmpty)


    val relations = accountService.getRelations(id, Relation.LIKE)
    assert(relations.isEmpty)


    val peoples = accountDao.getAllFrom(accountService.getSearchTableName(city, Sex.getOpposite(sex)))
    assert(peoples.size == 1)
    assert(peoples.head == id)


    val sympathies = sympathyDao.get(id)
    assert(sympathies.isEmpty)
  }

  test("add old account") {
    val id = "1"
    val firstCity = 2
    val sex = MALE

    var socialNetworkProvider = mock[SocialNetworkProvider]
    when(socialNetworkProvider.getInfo(id)).thenReturn((firstCity, sex))
    accountService.socialNetworkProvider = socialNetworkProvider

    accountService.addAccount(id)

    var account = accountService.getAccount(id)

    assert(account.offset == 0)
    assert(account.sex == sex)
    assert(account.city == firstCity)
    assert(account.relations.isEmpty)

    val relations = accountService.getRelations(id, Relation.LIKE)
    assert(relations.isEmpty)

    var peoples = accountDao.getAllFrom(accountService.getSearchTableName(firstCity, Sex.getOpposite(sex)))
    assert(peoples.size == 1)
    assert(peoples.head == id)

    val otherId = "42"
    accountService.changeStatus(otherId, id, Relation.LIKE)
    var sympathies = sympathyDao.get(id)
    assert(sympathies.size == 1)
    assert(sympathies.head.lovers.contains(otherId))


    val secondCity = 3
    socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider
    when(socialNetworkProvider.getInfo(id)).thenReturn((secondCity, sex))

    account = accountService.getAccount(id)

    assert(account.offset == 0)
    assert(account.sex == sex)
    assert(account.city == secondCity)
    assert(account.relations.isEmpty)


    peoples = accountDao.getAllFrom(accountService.getSearchTableName(firstCity, Sex.getOpposite(sex)))
    assert(peoples.isEmpty)


    peoples = accountDao.getAllFrom(accountService.getSearchTableName(secondCity, Sex.getOpposite(sex)))
    assert(peoples.size == 1)
    assert(peoples.head == id)

    sympathies = sympathyDao.get(id)
    assert(sympathies.isEmpty)
  }
}
