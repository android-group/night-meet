package ru.izebit.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.izebit.dao.{AccountDao, SympathyDao}
import ru.izebit.model.RelationType.{CONNECTED, LIKE, VIEWED}
import ru.izebit.model._


@Component
class AccountService {

  @Autowired
  var socialNetworkProvider: SocialNetworkProvider = _
  @Autowired
  private var accountDao: AccountDao = _
  @Autowired
  private var sympathyDao: SympathyDao = _


  def getRelations(id: String, relationType: RelationType): List[String] =
    accountDao.getAccount(id).relations
      .filter(relation => relation.relationType == relationType)
      .map(e => e.id)

  def changeStatus(id: String, otherId: String, relationType: RelationType): Boolean = relationType match {
    case LIKE =>
      val account = accountDao.getAccount(id)
      if (isDuplicate(account, otherId, relationType))
        return false

      val otherAccount = accountDao.getAccount(otherId)

      val relation = otherAccount.relations.find(r => r.relationType == LIKE && r.id == id)
      if (relation.isDefined) {
        relation.get.relationType = CONNECTED
        accountDao.insertAccount(otherAccount)

        account.relations = Relation(otherId, CONNECTED) :: account.relations
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
    case relationType@(VIEWED | CONNECTED) =>
      val account = accountDao.getAccount(id)
      if (isDuplicate(account, otherId, relationType))
        return false

      val relation = account.relations.find(r => r.relationType == relationType.prev() && r.id == otherId)
      if (relation.isDefined) {
        relation.get.relationType = relationType
        accountDao.insertAccount(account)

        true
      }
      else
        false
  }

  def isDuplicate(account: Account, otherId: String, relationType: RelationType): Boolean = {
    account.relations.exists(r => r.relationType == relationType && r.id == otherId)
  }

  def getCandidates(id: String, count: Int, token: String): Set[String] = {
    val sympathy = sympathyDao.get(id)
    var candidates: Set[String] = Set.empty

    if (sympathy.lovers.nonEmpty) {
      if (sympathy.lovers.size > count) {
        candidates = sympathy.lovers.slice(0, count)
        sympathy.lovers = sympathy.lovers.slice(count + 1, sympathy.lovers.size)
      } else {
        candidates = sympathy.lovers
        sympathy.lovers = Set()
      }

      sympathyDao.insert(sympathy)
    }

    if (candidates.size == count)
      return candidates

    val account = accountDao.getAccount(id)
    val (ids, offset) = accountDao
      .getFromSearchTable(
        getSearchTableName(account.city, account.sex.opposite()),
        account.internalOffset,
        count - candidates.size)
    if (ids.isEmpty)
      return candidates

    candidates ++= ids
    account.internalOffset = offset

    if (candidates.size < count) {
      val externalCount = count - candidates.size
      //todo корявость
      val ids =
        Option(socialNetworkProvider.search(account.city, account.sex.opposite(), externalCount, account.externalOffset, token))
          .getOrElse(List.empty)

      account.externalOffset = ids.size
      candidates ++= ids
    }

    accountDao.insertAccount(account)

    candidates
  }


  def login(id: String): Unit = {
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

  def getSearchTableName(city: Int, sex: Sex): String = {
    city + "-" + sex.name
  }
}
