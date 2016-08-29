package ru.izebit.model

import ru.izebit.model.Relation.Type

/**
  * Created by Artem Konovalov on 8/28/16.
  */
case class Relation(id: String, var relationType: Type)

object Relation {

  trait Type

  case object LIKE extends Type

  case object CONNECT extends Type

  case object VIEWED extends Type


  def get(relationType: Int): Type = relationType match {
    case 1 => LIKE
    case 2 => CONNECT
    case 3 => VIEWED
    case _ => throw new IllegalArgumentException(s"$relationType is not valid value")
  }
}
