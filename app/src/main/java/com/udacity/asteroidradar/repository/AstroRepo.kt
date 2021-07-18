package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.DB.DataBase
import com.udacity.asteroidradar.DB.DataBaseAsteriod
import com.udacity.asteroidradar.DB.asDatabaseModel
import com.udacity.asteroidradar.DB.asDomainModel
import com.udacity.asteroidradar.api.AsteriodApiService

import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AstroRepo(private val database: DataBaseAsteriod)
{
    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().minusDays(7)

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteriodDao.getAsteroids()) {
            it.asDomainModel()
        }




// Courtines
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = AsteriodApiService.ApiAsteriod.retrofitService.getAstroidFeed(API_KEY)
                val result = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteriodDao.insertAll(*result.asDatabaseModel())
                database.asteriodDao.insertAll()
                Log.d("Refresh Asteroids", "Success")
            } catch (err: Exception) {
                Log.e("Failed: AsteroidRepFile", err.message.toString())
            }
        }
    }
}