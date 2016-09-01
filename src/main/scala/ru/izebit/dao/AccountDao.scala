package ru.izebit.dao

import java.util.function.Consumer

import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.stereotype.{Component, Repository}
import ru.izebit.model.Account

import scala.collection.mutable

@Component
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


  def getFromSearchTable(tableName: String, offset: Int, count: Int): (mutable.Set[String], Int) = {

    val tableSize = mongoTemplate.count(new Query(), classOf[Document], tableName).asInstanceOf[Int]
    if (tableSize < count)
      (getCandidates(tableName, 0, tableSize), tableSize)
    else if (count + offset <= tableSize)
      (getCandidates(tableName, offset, count), offset + count)
    else {
      val size = offset + count - tableSize
      (getCandidates(tableName, offset, tableSize) ++= getCandidates(tableName, 0, size), size)
    }
  }

  private def getCandidates(tableName: String, offset: Int, count: Int): mutable.Set[String] = {
    val query = new Query().skip(offset).limit(count)
    val result = mongoTemplate.find(query, classOf[Document], tableName)

    val candidates: mutable.Set[String] = mutable.Set()
    val consumer: Consumer[Document] = new Consumer[Document] {
      override def accept(doc: Document): Unit = candidates += doc.getString("_id")
    }
    result.forEach(consumer)

    candidates
  }
}
