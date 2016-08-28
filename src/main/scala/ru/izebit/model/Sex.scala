package ru.izebit.model

/**
  * Created by Artem Konovalov on 8/28/16.
  */
object Sex {

  trait Type

  object MALE extends Type

  object FEMALE extends Type


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
