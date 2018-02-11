package modules.persistance

import javax.persistence.EntityManager
import play.db.jpa.JPA

class EntityManagerProviderImpl extends EntityManagerProvider {
  
  def get = JPA.em()
  
  def apply() = JPA.em()

}