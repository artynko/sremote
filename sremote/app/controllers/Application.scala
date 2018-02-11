package controllers

import com.fasterxml.jackson.annotation.JsonAutoDetect
import domain.Video
import javax.inject.Inject
import javax.persistence.Entity
import modules.persistance.EntityManagerProvider
import play.api.mvc.Action
import play.api.mvc.Controller
import modules.persistance.Transactional

class Application @Inject() (val em: EntityManagerProvider) extends Controller with Transactional {

  def index = Action {
	  Ok("keke")
  }

}