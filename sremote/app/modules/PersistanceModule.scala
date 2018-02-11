package modules

import com.google.inject.AbstractModule
import modules.persistance.EntityManagerProvider
import modules.persistance.EntityManagerProviderImpl
import services.RedisClientProvider

class PersistanceModule extends AbstractModule {
  protected def configure() {
    bind(classOf[EntityManagerProvider]).to(classOf[EntityManagerProviderImpl])
    bind(classOf[RedisClientProvider]).asEagerSingleton() 
  }
}