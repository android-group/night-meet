package ru.izebit.model

/**
  * Created by Artem Konovalov on 8/28/16.
  */
case class Relation(id: String, var relationType: RelationType)


object RelationType {

  val LIKE = RelationType("like", 1)
  val CONNECTED = RelationType("connected", 2)
  val VIEWED = RelationType("viewed", 3)


  def by(relationType: Int): RelationType = relationType match {
    case LIKE.number => LIKE
    case CONNECTED.number => CONNECTED
    case VIEWED.number => VIEWED
    case _ => throw new IllegalArgumentException(s"$relationType is not valid value")
  }
}

case class RelationType(name: String, number: Int) {

  import ru.izebit.model.RelationType.{CONNECTED, LIKE, VIEWED}

  def prev():RelationType = this match {
    case CONNECTED => LIKE
    case VIEWED => CONNECTED
    case LIKE => null
  }

  override def equals(obj: Any): Boolean = obj match {
    case x: RelationType => this.name == x.name
    case _ => false
  }
}
