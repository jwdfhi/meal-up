package com.jwdfhi.meal_up.screens.like

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jwdfhi.meal_up.models.CustomViewModel
import com.jwdfhi.meal_up.models.DataOrException
import com.jwdfhi.meal_up.models.DataOrExceptionStatus
import com.jwdfhi.meal_up.models.MealModel
import com.jwdfhi.meal_up.repositories.MealDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    public val context: Context,
    private val mealDatabaseRepository: MealDatabaseRepository
) : ViewModel(), CustomViewModel<Nothing, Nothing, Nothing> {

     init {
         getLikedMeals()
     }

    override fun initState() {
        _mealsDataOrException.value = DataOrException(status = DataOrExceptionStatus.Loading)

        _mealsDataOrException.value = DataOrException(
            data = _storedMealList.value.toMutableList(),
            status = DataOrExceptionStatus.Success
        )
    }

    override fun onBackPressed(navController: NavController) {
        navController.popBackStack()
    }

    private val _mealsDataOrException = MutableStateFlow<DataOrException<MutableList<MealModel>>>(
        DataOrException(status = DataOrExceptionStatus.Loading)
    )
    val mealsDataOrException = _mealsDataOrException.asStateFlow()

    private val _storedMealList = MutableStateFlow<List<MealModel>>(emptyList())
    private fun getLikedMeals() {
        viewModelScope.launch(Dispatchers.IO) {
            mealDatabaseRepository.getMeals().collect {
                _storedMealList.value = it.filter { storedMeal -> storedMeal.isLiked }
                return@collect
            }
        }
    }

    fun searchMealByName(value: String) {
        val existedMealList = _mealsDataOrException.value.data

        _mealsDataOrException.value = DataOrException(status = DataOrExceptionStatus.Loading)

        if (value.trim().isEmpty()) {
            _mealsDataOrException.value = DataOrException(
                data = _storedMealList.value.toMutableList(),
                status = DataOrExceptionStatus.Success
            )
            return
        }

        val filteredByNameMeals = mutableListOf<MealModel>()
        existedMealList?.forEach { storedMeal ->
            if (storedMeal.strMeal.lowercase(Locale.getDefault()).contains(value.lowercase(Locale.getDefault()))) {
                filteredByNameMeals.add(storedMeal)
            }
        }

        _mealsDataOrException.value = DataOrException(
            data = filteredByNameMeals,
            status = DataOrExceptionStatus.Success
        )
    }

}