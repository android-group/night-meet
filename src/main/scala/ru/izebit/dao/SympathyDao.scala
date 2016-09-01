package ru.izebit.dao

import java.util.function.Consumer

import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.stereotype.{Component, Repository}
import ru.izebit.model.Sympathy


@Component
class SympathyDao {

  private val sympathyTableName = "sympathies"

  @Autowired
  private var mongoTemplate: MongoTemplate = _

  def dropAll() = {
    val consumer: Consumer[String] = new Consumer[String] {
      override def accept(name: String): Unit = mongoTemplate.dropCollection(name)
    }
    mongoTemplate.getCollectionNames.forEach(consumer)
  }

  def insert(sympathy: Sympathy) =
    mongoTemplate.save(
      new Document()
        .append("_id", sympathy.id)
        .append("sympathy", sympathy),
      sympathyTableName)

  def get(id: String): Sympathy = {
    val document = mongoTemplate.findById(id, classOf[Document], sympathyTableName)

    if (document == null) null
    else document.get("sympathy", classOf[Sympathy])
  }

  def removeFor(id: String) =
    mongoTemplate.remove(new Query().addCriteria(Criteria.where("_id").is(id)), sympathyTableName)
}
