package ru.izebit.contoller

import org.json.{JSONArray, JSONObject}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._
import ru.izebit.model.Relation
import ru.izebit.service.AccountService


@RestController
@RequestMapping(value = Array("/api/v1"))
class BaseController {
  @Autowired
  private var accountService: AccountService = _


  @RequestMapping(value = Array("/account/{id}"), method = Array(RequestMethod.POST))
  def addAccount(@PathVariable(value = "id") id: String): String = {

    val response = new JSONObject()

    try {
      accountService.addAccount(id)
      response.put("result", "ok")

    } catch {
      case e: _ =>
        response.put("result", "error")
        response.put("description", s"internal error: ${e.getMessage}")
    }

    response.toString
  }

  @RequestMapping(value = Array("/account/{id}/candidates"), method = Array(RequestMethod.GET))
  def getCandidates(@PathVariable(value = "id") id: String,
                    @RequestParam(value = "count", defaultValue = "10") count: Int): String = {


    val response = new JSONObject()

    try {

      val result = accountService.getCandidates(id, count)
      response.put("result", new JSONArray(result))

    } catch {
      case e: _ =>
        response.put("result", "error")
        response.put("description", s"internal error: ${e.getMessage}")
    }

    response.toString
  }

  @RequestMapping(value = Array("/account/{id}/relations/{other_id}/{type}"), method = Array(RequestMethod.PUT))
  def changeStatus(@PathVariable(value = "id") currentId: String,
                   @PathVariable(value = "other_id") otherId: String,
                   @PathVariable(value = "type") relationType: Int): String = {

    val response = new JSONObject()

    try {
      val result = accountService.changeStatus(currentId, otherId, Relation.get(relationType))
      response.put("result", if (result) "ok" else "error")

    } catch {
      case e: IllegalArgumentException =>
        response.put("result", "error")
        response.put("description", s"bad params: ${e.getMessage}")
      case _ =>
        response.put("result", "error")
        response.put("description", "internal error")
    }

    response.toString
  }

  @RequestMapping(value = Array("/account/{id}/relations/{type}"), method = Array(RequestMethod.GET))
  def getSympathies(@PathVariable(value = "id") currentId: String,
                    @PathVariable(value = "type") relationType: Int): String = {


    val response = new JSONObject()

    try {

      val result = accountService.getSympathies(currentId, Relation.get(relationType))
      response.put("result", new JSONArray(result))

    } catch {
      case e: IllegalArgumentException =>
        response.put("result", "error")
        response.put("description", s"bad params: ${e.getMessage}")
      case _ =>
        response.put("result", "error")
        response.put("description", "internal error")
    }

    response.toString
  }
}
