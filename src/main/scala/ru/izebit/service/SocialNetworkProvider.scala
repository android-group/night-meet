package ru.izebit.service

import java.util.concurrent.TimeUnit

import org.apache.http.client.fluent.Request
import org.json.JSONObject
import org.springframework.stereotype.Component
import ru.izebit.model.Sex

import scala.collection.JavaConversions.asScalaIterator


@Component
class SocialNetworkProvider {
  private val API_VERSION = "5.62"

  private val TIMEOUT = TimeUnit.SECONDS.toMillis(5).asInstanceOf[Int]
  private val HOST = "https://api.vk.com/method/"


  /**
    * получение информации о пользователе
    *
    * @see <a href="https://vk.com/dev/users.get">описание метода</a>
    * @param id идентификатор пользователя
    * @return номер города, пол
    */
  def getInfo(id: String): (Int, Sex) = {
    val fields = "city,sex"

    val params = s"users.get?user_ids=$id&fields=$fields&v=$API_VERSION"

    //todo сделать повторную отправку запроса в случае фейла
    val content =
      Request
        .Get(HOST + params)
        .connectTimeout(TIMEOUT)
        .execute().returnContent().asString()

    val jsonResponse = new JSONObject(content).getJSONArray("response").getJSONObject(0)

    (jsonResponse.getJSONObject("city").getInt("id"), Sex(jsonResponse.getInt("sex")))
  }

  /**
    * поиск рандомных анкет из соцсети
    *
    * @see <a href="https://vk.com/dev/users.search">описание api метода</a>
    * @param city  город
    * @param sex   пол
    * @param count количество
    * @return список id
    */
  def search(city: Int, sex: Sex, count: Int, offset: Int, token: String): List[String] = {

    val isEnableExternalSearch = java.lang.Boolean.getBoolean("external-search-enable")
    if (!isEnableExternalSearch)
      return List.empty

    val sortType = 1
    val isHasPhoto = 1
    val online = 1
    val status = 6

    val param = s"users.search?" +
      s"city=$city&sex=$sex&count=$count&offset=$offset" +
      s"&sort=$sortType&status=$status&online=$online&has_photo=$isHasPhoto" +
      s"&access_token=$token&v=$API_VERSION"

    //todo сделать повторную отправку запроса в случае фейла
    val content =
      Request
        .Get(HOST + param)
        .connectTimeout(TIMEOUT)
        .execute().returnContent().asString()

    val jsonResponse = new JSONObject(content)

    jsonResponse
      .getJSONArray("items")
      .iterator()
      .map(e => e.asInstanceOf[JSONObject])
      .map(e => e.getString("id"))
      .toList
  }
}
