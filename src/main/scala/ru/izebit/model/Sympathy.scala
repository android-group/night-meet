package ru.izebit.model

/**
  * объект содержащий информацию о том, кому понравился данный пользователь
  */
case class Sympathy(id: String, var lovers: Set[String]) extends Serializable

