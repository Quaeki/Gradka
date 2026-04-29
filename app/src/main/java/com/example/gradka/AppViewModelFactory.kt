package com.example.gradka

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradka.data.GradkaRepositoryImpl
import com.example.gradka.domain.AddAddressUseCase
import com.example.gradka.domain.DeleteAddressUseCase
import com.example.gradka.domain.GetAddressesUseCase
import com.example.gradka.domain.GetOrderUseCase
import com.example.gradka.domain.PlaceOrderUseCase
import com.example.gradka.domain.SetPrimaryAddressUseCase

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = GradkaRepositoryImpl.getInstance(application)
        @Suppress("UNCHECKED_CAST")
        return AppViewModel(
            repository = repo,
            getOrderUseCase = GetOrderUseCase(repo),
            placeOrderUseCase = PlaceOrderUseCase(repo),
            getAddressesUseCase = GetAddressesUseCase(repo),
            addAddressUseCase = AddAddressUseCase(repo),
            setPrimaryAddressUseCase = SetPrimaryAddressUseCase(repo),
            deleteAddressUseCase = DeleteAddressUseCase(repo),
        ) as T
    }
}