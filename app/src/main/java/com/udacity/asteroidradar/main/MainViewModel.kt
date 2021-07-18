package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.DB.getDatabase
import com.udacity.asteroidradar.Fitler
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteriodApiService
import com.udacity.asteroidradar.repository.AstroRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AstroRepo(database)

    private val _PicOfTheday = MutableLiveData<PictureOfDay>()
    val picoftheDay: LiveData<PictureOfDay>
        get() = _PicOfTheday

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid>()
    val navigateToDetailAsteroid: LiveData<Asteroid>
        get() = _navigateToDetailAsteroid

    private var _filterAsteroid = MutableLiveData(Fitler.ALL)

    val asteroidList = Transformations.switchMap(_filterAsteroid) {
        when (it!!) {
            else -> asteroidRepository.allAsteroids
        }
    }

    //courtines
    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            picOftheDayRefresh()
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetailAsteroid.value = null
    }

    fun onChangeFilter(filter: Fitler) {
        _filterAsteroid.postValue(filter)
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }

    private suspend fun picOftheDayRefresh() {
        withContext(Dispatchers.IO) {
            try {
                _PicOfTheday.postValue(
                    AsteriodApiService.ApiAsteriod.retrofitService.getPicOftheDay(API_KEY)
                )
            } catch (err: Exception) {
                Log.e("picOftheDayRefresh", err.printStackTrace().toString())
            }
        }
    }
}