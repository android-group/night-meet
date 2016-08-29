package ru.izebit.service

import org.springframework.stereotype.Component
import ru.izebit.model.Relation.Type


@Component
class AccountService {


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
}
