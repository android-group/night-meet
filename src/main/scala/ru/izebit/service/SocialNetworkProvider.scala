package ru.izebit.service

import java.util.concurrent.TimeUnit

import org.apache.http.client.fluent.Request
import org.json.JSONObject
import org.springframework.stereotype.Component
import ru.izebit.model.Sex


@Component
class SocialNetworkProvider {
  private val TIMEOUT = TimeUnit.SECONDS.toMillis(5).asInstanceOf[Int]
  private val HOST = "https://api.vk.com/method/"


  /**
    * получение информации о пользователе
    *
    * @param id идентификатор пользователя
    * @return номер города, пол
    */
  def getInfo(id: String): (Int, Sex.Type) = {
    val params = s"users.get?user_ids=$id&fields=city,sex"

    val content =
      Request
        .Get(HOST + params)
        .connectTimeout(TIMEOUT)
        .execute().returnContent().asString()

    val response = new JSONObject(content).getJSONArray("response").getJSONObject(0)
    (response.getInt("city"), Sex.get(response.getInt("sex")))
  }

  /**
    * поиск рандомных анкет из соцсети
    *
    * @param city  город
    * @param sex   пол
    * @param count количество
    * @return список id
    */
  def search(city: Int, sex: Sex.Type, count: Int): List[String] = {
    //todo выборка из соцсети, подумать как лучше сделать
    ???
  }
}
