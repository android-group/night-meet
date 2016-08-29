package ru.izebit.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.izebit.dao.{AccountDao, SympathyDao}
import ru.izebit.model.Relation.{CONNECT, LIKE, Type}
import ru.izebit.model.{Account, Relation, Sex, Sympathy}


@Component
class AccountService {

  @Autowired
  var socialNetworkProvider: SocialNetworkProvider = _
  @Autowired
  private var accountDao: AccountDao = _
  @Autowired
  private var sympathyDao: SympathyDao = _


  def getRelations(id: String, relationType: Type): List[String] =
    accountDao.getAccount(id).relations
      .filter(relation => relation.relationType == relationType)
      .map(e => e.id)

  def changeStatus(id: String, otherId: String, relationType: Type): Boolean = relationType match {
    case LIKE => {
      val account = accountDao.getAccount(id)
      val otherAccount = accountDao.getAccount(otherId)

      val relation = otherAccount.relations.find(r => r.relationType == LIKE && r.id == id)
      if (relation.isDefined) {
        relation.get.relationType = CONNECT
        accountDao.insertAccount(otherAccount)

        account.relations = Relation(otherId, CONNECT) :: account.relations
      } else {
        account.relations = Relation(otherId, LIKE) :: account.relations

        var sympathy = sympathyDao.get(otherId)
        if (sympathy == null)
          sympathy = Sympathy(otherId, Set())

        if (!sympathy.lovers(id))
          sympathy.lovers = sympathy.lovers + id

        sympathyDao.insert(sympathy)
      }

      accountDao.insertAccount(account)

      true
    }
  }

  def getCandidates(id: String, count: Int): java.util.List[String] = {
    ???
  }


  def addAccount(id: String) = {
    val (city, sex) = socialNetworkProvider.getInfo(id)
    var account = accountDao.getAccount(id)

    if (account == null) {
      account = Account(id, sex, city)
      accountDao.insertAccount(account)
      accountDao.insertToSearchTable(getSearchTableName(city, sex), id)

    } else if (account.city != city || account.sex != sex) {
      accountDao.removeFromSearchTable(getSearchTableName(account.city, account.sex), id)
      sympathyDao.removeFor(id)

      account.city = city
      account.sex = sex

      accountDao.insertAccount(account)
      accountDao.insertToSearchTable(getSearchTableName(city, sex), id)
    }
  }

  def getSearchTableName(city: Int, sex: Sex.Type): String = {
    city + "-" + sex.name
  }
}
