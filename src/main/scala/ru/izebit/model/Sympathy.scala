package ru.izebit.model

/**
  * объект содержащий информацию о том, кому понравился данный пользователь
  */
case class Sympathy(id: String, lovers: scala.collection.mutable.Set[String]) extends Serializable

