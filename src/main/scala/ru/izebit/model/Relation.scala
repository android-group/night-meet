package ru.izebit.model

/**
  * Created by Artem Konovalov on 8/28/16.
  */
object Relation {

  trait Type

  case object LIKE extends Type

  case object HATE extends Type

  case object VIEW extends Type


  def get(relationType: Int): Type = relationType match {
    case 1 => LIKE
    case 2 => HATE
    case 3 => VIEW
    case _ => throw new IllegalArgumentException(s"$relationType is not valid value")
  }
}
