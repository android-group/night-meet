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
  def login(@PathVariable(value = "id") id: String): String = {

    responseWrapper(
      response => {
        accountService.login(id)
        response.put("result", "ok")
      }
    )
  }

  @RequestMapping(value = Array("/account/{id}/candidates"), method = Array(RequestMethod.GET))
  def getCandidates(@PathVariable(value = "id") id: String,
                    @RequestParam(value = "token") token: String,
                    @RequestParam(value = "count", defaultValue = "10") count: Int): String = {


    responseWrapper(
      response => {
        val ids = accountService.getCandidates(id, count, token)
        val candidates = ids.foldRight(new JSONArray())((id, array) => array.put(id))
        response.put("result", "ok")
        response.put("account_ids", candidates)
      })
  }

  @RequestMapping(value = Array("/account/{id}/relations/{other_id}/{type}"), method = Array(RequestMethod.PUT))
  def changeStatus(@PathVariable(value = "id") currentId: String,
                   @PathVariable(value = "other_id") otherId: String,
                   @PathVariable(value = "type") relationNumber: Int): String = {

    responseWrapper(
      response => {
        require(currentId != otherId)

        val result = accountService.changeStatus(currentId, otherId, Relation.getType(relationNumber))
        response.put("result", if (result) "ok" else "error")
      }
    )
  }

  @RequestMapping(value = Array("/account/{id}/relations/{type}"), method = Array(RequestMethod.GET))
  def getRelations(@PathVariable(value = "id") currentId: String,
                   @PathVariable(value = "type") relationType: Int): String = {


    responseWrapper(
      response => {
        val result: List[String] = accountService.getRelations(currentId, Relation.getType(relationType))
        val ids = result.foldRight(new JSONArray())((id, array) => array.put(id))
        response.put("result", "ok")
        response.put("account_ids", ids)
      }
    )
  }

  private def responseWrapper(logic: JSONObject => Unit) = {
    val response = new JSONObject()

    try {

      logic(response)

    } catch {
      case e: IllegalArgumentException =>
        response.put("result", "error")
        response.put("description", s"bad params: ${e.getMessage}")
      case e: Exception =>
        response.put("result", "error")
        response.put("description", s"internal error: ${e.getMessage}")
    }

    response.toString
  }
}
