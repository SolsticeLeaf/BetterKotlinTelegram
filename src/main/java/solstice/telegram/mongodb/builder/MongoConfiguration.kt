package solstice.telegram.mongodb.builder

data class MongoConfiguration(val host: String, val port: String, val login: String, val password: String, val dbName: String, val authDb: String)
