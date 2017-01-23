package ru.izebit.model

import ru.izebit.model.Sex

/**
  * Created by Artem Konovalov on 8/28/16.
  */
case class Account(id: String, var sex: Sex, var city: Int) extends Serializable {
  //todo сделать похитрее
  var externalOffset = 0
  var internalOffset: Int = 0

  var relations: List[Relation] = List.empty
}
