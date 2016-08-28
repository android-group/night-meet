package ru.izebit.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.izebit.dao.AccountDao
import ru.izebit.model.Relation.Type
import ru.izebit.model.{Account, Sex}


@Component
class AccountService {
  @Autowired
  private var accountDao: AccountDao = _


  def getSympathies(currentId: String, value: Type): Iterable[String] = {
    ???
  }

  def changeStatus(currentId: String, otherId: String, relationType: Type): Boolean = {
    ???

  }

  def getCandidates(id: String, count: Int): Iterable[String] = {
    var result: Iterable[String] = List.empty

    //получаем тех кто меня лайкнул
    val sympathy = accountDao.getSympathy(id)
    if (sympathy != null) {
      result = sympathy.lovers.drop(count)
      if (result.nonEmpty) accountDao.updateSympathy(sympathy)
    }

    if (result.size == count)
      return result

    //получаем тех кто не лайкнул
    val account = accountDao.getAccount(id)
    val (candidates, offset) = accountDao.getCandidatesFrom(getTableName(account), account.offset, count)
    if (candidates.nonEmpty) {
      result = result ++ candidates
      account.offset = offset
      accountDao.updateAccount(account)
    }

    if (result.size == count)
      return result


    //получаем рандомных из соцсети


    result
  }


  def addAccount(id: String) = {

    val (city, sex) = VkProvider.getInfo(id)
    val account = Account(id, sex, city)

    accountDao.addAccount(account)
  }

  private def getTableName(account: Account) = {
    val city = account.city
    val sex = Sex.getOpposite(account.sex)
    city + "_" + sex
  }
}
