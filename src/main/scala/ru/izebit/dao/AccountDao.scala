package ru.izebit.dao

import java.util.function.Consumer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.stereotype.Repository
import ru.izebit.model.{Account, Sympathy}

import scala.collection.mutable.ArrayBuffer

@Repository
class AccountDao {

  private val SYMPATHY_TABLE_NAME = "sympathies"
  private val USER_TABLE_NAME = "users"

  @Autowired
  private var mongoTemplate: MongoTemplate = _


  def updateAccount(account: Account) = mongoTemplate.save(account, USER_TABLE_NAME)

  def getAccount(id: String) = mongoTemplate.findById(id, classOf[Account], USER_TABLE_NAME)

  def addAccount(account: Account) = {
    mongoTemplate.insert(account, USER_TABLE_NAME)
  }

  def createTable(name: String) = {
    if (!mongoTemplate.collectionExists(name))
      mongoTemplate.createCollection(name)
  }

  def insertUserInto(tableName: String, id: String) = {
    mongoTemplate.insert(id, tableName)
  }

  def getCandidatesFrom(tableName: String, offset: Int, count: Int): (Iterable[String], Int) = {
    if (!mongoTemplate.collectionExists(tableName))
      return (List.empty, 0)

    val size = mongoTemplate.count(new Query(), tableName)
    val pos = if (size <= offset) 0 else offset

    val result = new ArrayBuffer[String]()

    val consumer: Consumer[String] = new Consumer[String] {
      override def accept(t: String): Unit = result += t
    }
    mongoTemplate
      .find(new Query().skip(pos).limit(count), classOf[String], tableName)
      .forEach(consumer)

    if (size < pos + count)
      mongoTemplate
        .find(new Query().skip(0).limit(result.size), classOf[String], tableName)
        .forEach(consumer)


    (result, (offset + count) % size.asInstanceOf[Int])
  }

  def deleteFrom(tableName: String, id: String) = {
    mongoTemplate.remove(new Query().addCriteria(Criteria.where("id").is(id)))
  }

  /**
    * добавление симпатии
    *
    * @param loverId тот кого полюбили
    * @param id      тот кому понравился
    */
  def addSympathy(loverId: String, id: String) = {
    var sympathy = mongoTemplate.findById(id, classOf[Sympathy], SYMPATHY_TABLE_NAME)
    sympathy = if (sympathy == null) Sympathy(loverId, new scala.collection.mutable.HashSet[String]()) else sympathy
    sympathy.lovers.add(loverId)
    mongoTemplate.save(sympathy, SYMPATHY_TABLE_NAME)
  }

  def getSympathy(id: String): Sympathy = {
    mongoTemplate.findById(id, classOf[Sympathy])
  }

  def updateSympathy(sympathy: Sympathy) = {
    mongoTemplate.save(sympathy, SYMPATHY_TABLE_NAME)
  }
}
