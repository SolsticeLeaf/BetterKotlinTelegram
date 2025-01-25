package solstice.telegram.mongodb.db

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.connection.ClusterSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import solstice.telegram.mongodb.builder.MongoConfiguration
import java.util.List

abstract class Mongo(settings: MongoConfiguration) {
    private val logger: Logger = LoggerFactory.getLogger(Mongo::class.java)
    private val settings: MongoConfiguration = settings
    var mongoClient: MongoClient? = null
    var mongoDatabase: MongoDatabase? = null

    fun connect() {
        try {
            logger.info("Connecting to database...")
            val credentials = MongoCredential.createCredential(
                settings.login,
                settings.authDb,
                settings.password.toCharArray()
            )
            val serverAddress: ServerAddress
            if (settings.port.isBlank()) {
                serverAddress = ServerAddress(settings.host)
            } else {
                serverAddress = ServerAddress(settings.host, settings.port.toInt())
            }
            val mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                    .applyToClusterSettings { builder: ClusterSettings.Builder ->
                        builder.hosts(
                            List.of(serverAddress)
                        )
                    }
                    .credential(credentials)
                    .build()
            )
            mongoDatabase = mongoClient.getDatabase(settings.dbName)
            this.mongoClient = mongoClient
            createTables()

            logger.info("Database connected!")
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }

    protected abstract fun createTables()

    protected fun createCollectionIfNotExists(collection: String) {
        if (hasCollection(collection)) {
            return
        }
        mongoDatabase!!.createCollection(collection)
        logger.info("Collection '{}' has been created!", collection)
    }

    private fun hasCollection(collection: String): Boolean {
        for (col in mongoDatabase!!.listCollectionNames()) {
            if (col == collection) {
                return true
            }
        }
        return false
    }
}
