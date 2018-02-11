package modules.persistance

import javax.persistence.EntityManager

trait EntityManagerProvider {
  def get: EntityManager
  def apply(): EntityManager
}