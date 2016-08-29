package ru.izebit.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.izebit.model.Relation.Type
import ru.izebit.model.{Account, Sex}


@Component
class AccountService {

  @Autowired
  var socialNetworkProvider: SocialNetworkProvider = _


  def getRelations(currentId: String, relationType: Type): List[String] = ???

  def changeStatus(id: String, otherId: String, relationType: Type): Boolean = {
    ???
  }

  def getCandidates(id: String, count: Int): List[String] = {
    ???
  }


  def addAccount(id: String) = {
    ???
  }


  def getAccount(id: String): Account = ???

  def getSearchTableName(city: Int, sex: Sex.Type): String = {
    city + "-" + Sex.getOpposite(sex)
  }
}
