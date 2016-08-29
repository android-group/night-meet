package ru.izebit.model

import ru.izebit.model.Sex.Type

/**
  * Created by Artem Konovalov on 8/28/16.
  */
case class Account(id: String,var sex: Type, var city: Int) extends Serializable {
  //todo сделать похитрее
  var offset: Int = 0
  var relations = List[Relation]()
}
