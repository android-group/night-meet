package ru.izebit.dao

import java.util.function.Consumer

import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.stereotype.Repository
import ru.izebit.model.Account

@Repository
class AccountDao {


  private val userTableName = "accounts"

  @Autowired
  private var mongoTemplate: MongoTemplate = _

  def dropAll() = {
    val consumer: Consumer[String] = new Consumer[String] {
      override def accept(name: String): Unit = mongoTemplate.dropCollection(name)
    }
    mongoTemplate.getCollectionNames.forEach(consumer)
  }

  def getAllFrom(tableName: String): List[String] = {
    var result: List[String] = Nil
    val consumer: Consumer[Document] = new Consumer[Document] {
      override def accept(document: Document): Unit = result = document.getString("_id") :: result
    }
    mongoTemplate.findAll(classOf[Document], tableName).forEach(consumer)

    result
  }


  def getAccount(id: String): Account = {
    val document = mongoTemplate
      .findById(id, classOf[Document], userTableName)

    if (document == null) null
    else document.get("account", classOf[Account])
  }

  def insertAccount(account: Account) =
    mongoTemplate.save(
      new Document()
        .append("_id", account.id)
        .append("account", account), userTableName)


  def removeFromSearchTable(tableName: String, id: String) =
    mongoTemplate.remove(new Query().addCriteria(Criteria.where("_id").is(id)), tableName)

  def insertToSearchTable(tableName: String, id: String) =
    mongoTemplate.insert(new Document("_id", id), tableName)
}
