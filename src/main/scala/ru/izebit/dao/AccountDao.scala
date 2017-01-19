package ru.izebit.dao

import java.util
import java.util.function.Consumer

import scala.collection.JavaConversions.{asScalaBuffer, asScalaSet}
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

  def dropAll(): Unit = {
    mongoTemplate.getCollectionNames.foreach(mongoTemplate.dropCollection)
  }

  def getAllFrom(tableName: String): List[String] = {
    mongoTemplate.findAll(classOf[Document], tableName).map(document => document.getString("_id")).toList
  }


  def getAccount(id: String): Account = {
    val document = mongoTemplate
      .findById(id, classOf[Document], userTableName)

    if (document == null)
      null
    else
      document.get("account", classOf[Account])
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
    val result = new util.HashSet[Document](mongoTemplate.find(query, classOf[Document], tableName))
    result.map(document => document.getString("_id"))
  }
}
