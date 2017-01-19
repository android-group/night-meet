package ru.izebit.model

/**
  * Created by Artem Konovalov on 8/28/16.
  */
object Sex {

  trait Type {
    val name: String


    override def equals(obj: scala.Any): Boolean = obj match {
      case x: Type => this.name == x.name
      case _ => false
    }
  }

  object MALE extends Type {
    override val name = "male"
  }

  object FEMALE extends Type {
    override val name = "female"
  }


  def get(order: Int) = order match {
    case 1 => FEMALE
    case 2 => MALE
    case _ => throw new IllegalArgumentException
  }


  def getOpposite(sex: Type) = sex match {
    case MALE => FEMALE
    case FEMALE => MALE
  }
}
