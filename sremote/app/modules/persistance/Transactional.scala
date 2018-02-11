package modules.persistance

import play.api.mvc._
import play.db.jpa.JPA

trait Transactional {
  
  def transactional(body: => Unit) {
    JPA.withTransaction(new play.libs.F.Callback0() {
      def invoke() = body
    })
  }
  
}
