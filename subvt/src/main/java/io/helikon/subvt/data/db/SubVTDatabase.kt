package io.helikon.subvt.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.helikon.subvt.data.dao.NetworkDAO
import io.helikon.subvt.data.dao.NotificationDAO
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.Notification

@Database(
    entities = [Network::class, Notification::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(RoomConverters::class)
abstract class SubVTDatabase : RoomDatabase() {
    abstract fun networkDAO(): NetworkDAO

    abstract fun notificationDAO(): NotificationDAO

    companion object {
        @Volatile
        private var instance: SubVTDatabase? = null

        fun getInstance(context: Context): SubVTDatabase {
            // only one thread of execution at a time can enter this block of code
            synchronized(this) {
                if (instance == null) {
                    instance =
                        Room
                            .databaseBuilder(
                                context.applicationContext,
                                SubVTDatabase::class.java,
                                "subvt_database",
                            )
                            .fallbackToDestructiveMigration()
                            .build()
                }
                return instance!!
            }
        }
    }
}
