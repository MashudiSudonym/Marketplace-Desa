package c.m.lapaksembakodonorojo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MarketplaceDesaDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao

    companion object {
        @Volatile
        private var INSTANCE: MarketplaceDesaDatabase? = null

        fun getDatabase(context: Context): MarketplaceDesaDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarketplaceDesaDatabase::class.java,
                    "Marketplace Desa Database"
                ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}