package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        OrderFileEntity::class,
        PaymentEntity::class,
        NotificationEntity::class,
        AdminSettingEntity::class,
        OfferEntity::class,
        OfferItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HafsaDatabase : RoomDatabase() {

    abstract fun hafsaDao(): HafsaDao

    companion object {
        @Volatile
        private var INSTANCE: HafsaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HafsaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HafsaDatabase::class.java,
                    "hafsa_traders_v2_db"
                )
                .addCallback(HafsaDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class HafsaDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDatabase(database.hafsaDao())
                    }
                }
            }
        }

        suspend fun populateInitialDatabase(dao: HafsaDao) {
            // Only seed basic shop settings (Shop name, Admin PIN, Contact details).
            // Zero fake categories, zero fake items, zero fake orders, zero fake notifications!
            dao.insertSettings(SeedDataProvider.getDefaultSettings())
        }
    }
}
