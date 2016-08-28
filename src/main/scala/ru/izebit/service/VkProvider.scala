package ru.izebit.service

import java.util.concurrent.TimeUnit

import org.apache.http.client.fluent.Request
import org.json.JSONObject
import ru.izebit.model.Sex

/**
  * Created by Artem Konovalov on 8/28/16.
  */

object VkProvider {
  private val TIMEOUT = TimeUnit.SECONDS.toMillis(5).asInstanceOf[Int]
  private val HOST = "https://api.vk.com/method/"


  def getInfo(id: String): (Int, Sex.Type) = {
    val params = s"users.get?user_ids=$id&fields=city,sex"

    val content = Request.Get(HOST + params)
      .connectTimeout(TIMEOUT)
      .execute().returnContent().asString()

    val response = new JSONObject(content).getJSONObject("response")
    (response.getInt("city"), Sex.get(response.getInt("sex")))
  }
}
