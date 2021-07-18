package com.udacity.asteroidradar.DB

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid


@Dao
interface AsteriodDao {
    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<DataBase>>

    @Query("SELECT * FROM asteroids WHERE closeApproachDate = :startDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDay(startDate: String): LiveData<List<DataBase>>

    @Query("SELECT * FROM asteroids WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDate(startDate: String, endDate: String): LiveData<List<DataBase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DataBase)
}

@Database(entities = [DataBase::class], version = 1, exportSchema = false)
abstract class DataBaseAsteriod : RoomDatabase() {
    abstract val asteriodDao : AsteriodDao
}

// creating singleton to access database
private lateinit var INSTANCE: DataBaseAsteriod

fun getDatabase(context: Context): DataBaseAsteriod {
    synchronized(DataBaseAsteriod::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                DataBaseAsteriod::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}

