package ru.izebit.dao

import org.springframework.stereotype.Repository
import ru.izebit.model.Sympathy


@Repository
class SympathyDao {
  def dropAll() = ???

  def get(id: String): List[Sympathy] = ???

}
