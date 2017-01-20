package ru.izebit.service

import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import ru.izebit.config.BaseConfiguration
import ru.izebit.dao.{AccountDao, SympathyDao}
import ru.izebit.model.{Account, Sex}
import ru.izebit.model.Relation.{CONNECT, LIKE, VIEWED}
import ru.izebit.model.Sex.{FEMALE, MALE}


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

  private def dropAll() = {
    accountDao.dropAll()
    sympathyDao.dropAll()
  }


  test("add new account") {
    dropAll()

    val id = "1"
    val city = 2
    val sex = MALE

    val socialNetworkProvider = mock[SocialNetworkProvider]
    when(socialNetworkProvider.getInfo(id)).thenReturn((city, sex))
    accountService.socialNetworkProvider = socialNetworkProvider

    accountService.login(id)

    val account: Account = accountDao.getAccount(id)

    assert(account.internalOffset == 0)
    assert(account.sex == sex)
    assert(account.city == city)
    assert(account.relations.isEmpty)


    val relations = accountService.getRelations(id, LIKE)
    assert(relations.isEmpty)


    val peoples = accountDao.getAllFrom(accountService.getSearchTableName(city, sex))
    assert(peoples.size == 1)
    assert(peoples.head == id)


    val sympathies = sympathyDao.get(id)
    assert(sympathies.lovers.isEmpty)
  }

  test("add old account") {
    dropAll()

    val id = "1"
    val firstCity = 2
    val sex = MALE

    var socialNetworkProvider = mock[SocialNetworkProvider]
    when(socialNetworkProvider.getInfo(id)).thenReturn((firstCity, sex))
    accountService.socialNetworkProvider = socialNetworkProvider

    accountService.login(id)

    var account = accountDao.getAccount(id)

    assert(account.internalOffset == 0)
    assert(account.sex == sex)
    assert(account.city == firstCity)
    assert(account.relations.isEmpty)

    val relations = accountService.getRelations(id, LIKE)
    assert(relations.isEmpty)

    var peoples = accountDao.getAllFrom(accountService.getSearchTableName(firstCity, sex))
    assert(peoples.size == 1)
    assert(peoples.head == id)

    val otherId = "42"
    accountDao.insertAccount(Account(otherId, MALE, 10))
    accountService.changeStatus(otherId, id, LIKE)
    var sympathy = sympathyDao.get(id)
    assert(sympathy.lovers.size == 1)
    assert(sympathy.lovers.contains(otherId))


    val secondCity = 3
    socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider
    when(socialNetworkProvider.getInfo(id)).thenReturn((secondCity, sex))
    accountService.login(id)

    account = accountDao.getAccount(id)

    assert(account.internalOffset == 0)
    assert(account.sex == sex)
    assert(account.city == secondCity)
    assert(account.relations.isEmpty)


    peoples = accountDao.getAllFrom(accountService.getSearchTableName(firstCity, sex))
    assert(peoples.isEmpty)


    peoples = accountDao.getAllFrom(accountService.getSearchTableName(secondCity, sex))
    assert(peoples.size == 1)
    assert(peoples.head == id)

    sympathy = sympathyDao.get(id)
    assert(sympathy.lovers.isEmpty)
  }


  test("get candidates when sympathy is empty") {
    dropAll()

    val token = "hello world"
    val city = 1
    val account = Account("1", MALE, city)

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", FEMALE, city)
    val thirdAcc = Account("4", FEMALE, city)

    val fourthAcc = Account("5", MALE, city)
    val fifthAcc = Account("6", FEMALE, city + 1)


    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(account, firstAcc, secondAcc, thirdAcc, fourthAcc, fifthAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    val candidates = accountService.getCandidates(account.id, 10, token)
    assert(candidates.size == 3)
    assert(candidates.contains(firstAcc.id))
    assert(candidates.contains(secondAcc.id))
    assert(candidates.contains(thirdAcc.id))

    assert(accountDao.getAccount(account.id).internalOffset == 3)
  }



  test("get candidates with padding when sympathy is empty") {
    dropAll()

    val token = "hello world"

    val city = 1
    val account = Account("1", MALE, city)

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", FEMALE, city)
    val thirdAcc = Account("4", FEMALE, city)

    val fourthAcc = Account("5", MALE, city)
    val fifthAcc = Account("6", FEMALE, city + 1)


    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(account, firstAcc, secondAcc, thirdAcc, fourthAcc, fifthAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    var candidates = accountService.getCandidates(account.id, 1, token)
    assert(candidates.size == 1)
    assert(candidates.contains(firstAcc.id))
    assert(accountDao.getAccount(account.id).internalOffset == 1)

    candidates = accountService.getCandidates(account.id, 2, token)
    assert(candidates.size == 2)
    assert(candidates.contains(secondAcc.id))
    assert(candidates.contains(thirdAcc.id))
    assert(accountDao.getAccount(account.id).internalOffset == 3)
  }


  test("get candidates with padding when sympathy is not empty") {
    dropAll()

    val token = "hello world"

    val city = 1
    val account = Account("1", MALE, city)

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", FEMALE, city)
    val thirdAcc = Account("4", FEMALE, city)

    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(account, firstAcc, secondAcc, thirdAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    accountService.changeStatus(thirdAcc.id, account.id, LIKE)

    val candidates = accountService.getCandidates(account.id, 2, token)
    assert(candidates.size == 2)
    assert(candidates.contains(firstAcc.id))
    assert(candidates.contains(thirdAcc.id))
    assert(accountDao.getAccount(account.id).internalOffset == 1)
  }

  test("get candidates when sympathy is has more") {
    dropAll()

    val token = "hello world"
    val city = 1
    val account = Account("1", MALE, city)

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", FEMALE, city)
    val thirdAcc = Account("4", FEMALE, city)

    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(account, firstAcc, secondAcc, thirdAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    accountService.changeStatus(firstAcc.id, account.id, LIKE)
    accountService.changeStatus(thirdAcc.id, account.id, LIKE)
    accountService.changeStatus(secondAcc.id, account.id, LIKE)

    val candidates = accountService.getCandidates(account.id, 2, token)

    assert(candidates.size == 2)
    assert(candidates.contains(firstAcc.id))
    assert(candidates.contains(thirdAcc.id))
    assert(accountDao.getAccount(account.id).internalOffset == 0)
  }

  test("get candidates with padding") {
    dropAll()

    val token = "hello world"
    val city = 1
    val account = Account("1", MALE, city)

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", FEMALE, city)
    val thirdAcc = Account("4", FEMALE, city)

    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(account, firstAcc, secondAcc, thirdAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    account.internalOffset = 1
    accountDao.insertAccount(account)

    val candidates = accountService.getCandidates(account.id, 10, token)
    assert(candidates.size == 3)
    assert(candidates.contains(firstAcc.id))
    assert(candidates.contains(secondAcc.id))
    assert(candidates.contains(thirdAcc.id))
    assert(accountDao.getAccount(account.id).internalOffset == candidates.size)
  }

  test("change status like and connect") {
    dropAll()

    val city = 1

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", MALE, city)

    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(firstAcc, secondAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    accountService.changeStatus(firstAcc.id, secondAcc.id, LIKE)
    var relations = accountService.getRelations(firstAcc.id, LIKE)
    assert(relations.size == 1)
    assert(relations.contains(secondAcc.id))

    var sympathy = sympathyDao.get(secondAcc.id)
    assert(sympathy != null)
    assert(sympathy.lovers.contains(firstAcc.id))


    accountService.changeStatus(secondAcc.id, firstAcc.id, LIKE)
    relations = accountService.getRelations(firstAcc.id, LIKE)
    assert(relations.isEmpty)
    relations = accountService.getRelations(firstAcc.id, CONNECT)
    assert(relations.size == 1)
    assert(relations.contains(secondAcc.id))

    sympathy = sympathyDao.get(firstAcc.id)
    assert(sympathy.lovers.isEmpty)

    relations = accountService.getRelations(secondAcc.id, CONNECT)
    assert(relations.size == 1)
    assert(relations.contains(firstAcc.id))
  }

  test("change status view") {
    dropAll()

    val city = 1

    val firstAcc = Account("2", FEMALE, city)
    val secondAcc = Account("3", MALE, city)

    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(firstAcc, secondAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    accountService.changeStatus(firstAcc.id, secondAcc.id, VIEWED)
    var relations = accountService.getRelations(firstAcc.id, VIEWED)
    assert(relations.isEmpty)
    accountService.changeStatus(firstAcc.id, firstAcc.id, CONNECT)
    relations = accountService.getRelations(firstAcc.id, CONNECT)
    assert(relations.isEmpty)

    accountService.changeStatus(secondAcc.id, firstAcc.id, LIKE)
    accountService.changeStatus(secondAcc.id, firstAcc.id, CONNECT)
    relations = accountService.getRelations(secondAcc.id, CONNECT)
    assert(relations.size == 1)
    assert(relations.contains(firstAcc.id))

    accountService.changeStatus(secondAcc.id, firstAcc.id, VIEWED)
    relations = accountService.getRelations(secondAcc.id, CONNECT)
    assert(relations.isEmpty)
    relations = accountService.getRelations(secondAcc.id, VIEWED)
    assert(relations.size == 1)
    assert(relations.contains(firstAcc.id))
  }



  test("change status - duplication check") {

    val city = 1

    val firstAcc = Account("4", FEMALE, city)
    val secondAcc = Account("5", MALE, city)

    val socialNetworkProvider = mock[SocialNetworkProvider]
    accountService.socialNetworkProvider = socialNetworkProvider

    val accounts = List(firstAcc, secondAcc)
    accounts.foreach(ac => when(socialNetworkProvider.getInfo(ac.id)).thenReturn((ac.city, ac.sex)))
    accounts.foreach(ac => accountService.login(ac.id))

    accountService.changeStatus(secondAcc.id, firstAcc.id, LIKE)
    var relations = accountService.getRelations(secondAcc.id, LIKE)
    assert(relations.size == 1)
    accountService.changeStatus(secondAcc.id, firstAcc.id, LIKE)
    relations = accountService.getRelations(secondAcc.id, LIKE)
    assert(relations.size == 1)
  }
}
