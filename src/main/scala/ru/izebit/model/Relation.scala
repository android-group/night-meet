package ru.izebit.model

import ru.izebit.model.Relation.Type

/**
  * Created by Artem Konovalov on 8/28/16.
  */
case class Relation(id: String, var relationType: Type)

object Relation {

  trait Type {
    val name: String

    override def equals(obj: scala.Any): Boolean = obj match {
      case x: Type => name == x.name
      case _ => false
    }


    def prev() = this match {
      case CONNECT => LIKE
      case VIEWED => CONNECT
      case LIKE => null
    }
  }

  case object LIKE extends Type {
    override val name: String = "like"
  }

  case object CONNECT extends Type {
    override val name: String = "connect"
  }

  case object VIEWED extends Type {
    override val name: String = "viewed"
  }

  def getType(relationType: Int): Type = relationType match {
    case 1 => LIKE
    case 2 => CONNECT
    case 3 => VIEWED
    case _ => throw new IllegalArgumentException(s"$relationType is not valid value")
  }

}
