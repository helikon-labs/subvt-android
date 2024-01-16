package io.helikon.subvt.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.helikon.subvt.data.dao.NetworkDAO
import io.helikon.subvt.data.model.Network

@Database(
    entities = [(Network::class)],
    version = 1,
    exportSchema = false,
)
abstract class SubVTDatabase : RoomDatabase() {
    abstract fun networkDAO(): NetworkDAO

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
