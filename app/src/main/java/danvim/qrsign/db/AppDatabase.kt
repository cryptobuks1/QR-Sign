package danvim.qrsign.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import danvim.qrsign.db.dao.KeyPairDao
import danvim.qrsign.db.dao.MessageDao
import danvim.qrsign.db.entities.KeyPair
import danvim.qrsign.db.entities.Message

@Database(entities = [
    KeyPair::class,
    Message::class
], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun keyPairDao(): KeyPairDao
    abstract fun messageDao(): MessageDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "QRSignDB"
                    ).build()
                }
            }

            return INSTANCE
        }

        fun destroyDatabase() {
            INSTANCE = null
        }
    }
}