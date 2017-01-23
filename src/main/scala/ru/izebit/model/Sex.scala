package ru.izebit.model

/**
  * Created by Artem Konovalov on 8/28/16.
  */

case class Sex(val name: String, val number: Int) {
  override def equals(obj: Any): Boolean = obj match {
    case obj: Sex => this.name == obj.name
    case _ => false
  }

  def opposite(): Sex = Sex((this.number + 1) % 2)
}

object Sex {

  val FEMALE = new Sex("FEMALE", 1)
  val MALE = new Sex("MALE", 2)

  def apply(number: Int): Sex = number match {
    case FEMALE.number => FEMALE
    case MALE.number => MALE
  }
}
