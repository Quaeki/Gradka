package com.example.gradka

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradka.data.GradkaRepositoryImpl
import com.example.gradka.domain.AddAddressUseCase
import com.example.gradka.domain.AddNoteUseCase
import com.example.gradka.domain.AddPaymentMethodUseCase
import com.example.gradka.domain.AddSubscriptionUseCase
import com.example.gradka.domain.CalculateCartSummaryUseCase
import com.example.gradka.domain.CalculateSubscriptionSummaryUseCase
import com.example.gradka.domain.DeleteAddressUseCase
import com.example.gradka.domain.DeleteNoteUseCase
import com.example.gradka.domain.DeletePaymentMethodUseCase
import com.example.gradka.domain.DeleteSubscriptionUseCase
import com.example.gradka.domain.EditNoteUseCase
import com.example.gradka.domain.GetAddressesUseCase
import com.example.gradka.domain.GetAllNoteUseCase
import com.example.gradka.domain.GetOrderUseCase
import com.example.gradka.domain.GetPaymentMethodsUseCase
import com.example.gradka.domain.GetSubscriptionsUseCase
import com.example.gradka.domain.PlaceOrderUseCase
import com.example.gradka.domain.ReverseGeocodeUseCase
import com.example.gradka.domain.SetPrimaryAddressUseCase
import com.example.gradka.domain.SuggestAddressesUseCase
import com.example.gradka.domain.UpdateSubscriptionUseCase

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = GradkaRepositoryImpl.getInstance(application)
        @Suppress("UNCHECKED_CAST")
        return AppViewModel(
            getOrderUseCase = GetOrderUseCase(repo),
            placeOrderUseCase = PlaceOrderUseCase(repo),
            getAddressesUseCase = GetAddressesUseCase(repo),
            addAddressUseCase = AddAddressUseCase(repo),
            setPrimaryAddressUseCase = SetPrimaryAddressUseCase(repo),
            deleteAddressUseCase = DeleteAddressUseCase(repo),
            suggestAddressesUseCase = SuggestAddressesUseCase(repo),
            reverseGeocodeUseCase = ReverseGeocodeUseCase(repo),
            addNoteUseCase = AddNoteUseCase(repo),
            deleteNoteUseCase = DeleteNoteUseCase(repo),
            editNoteUseCase = EditNoteUseCase(repo),
            getAllNoteUseCase = GetAllNoteUseCase(repo),
            getSubscriptionsUseCase = GetSubscriptionsUseCase(repo),
            addSubscriptionUseCase = AddSubscriptionUseCase(repo),
            updateSubscriptionUseCase = UpdateSubscriptionUseCase(repo),
            deleteSubscriptionUseCase = DeleteSubscriptionUseCase(repo),
            calculateSubscriptionSummaryUseCase = CalculateSubscriptionSummaryUseCase(),
            getPaymentMethodsUseCase = GetPaymentMethodsUseCase(repo),
            addPaymentMethodUseCase = AddPaymentMethodUseCase(repo),
            deletePaymentMethodUseCase = DeletePaymentMethodUseCase(repo),
            calculateCartSummaryUseCase = CalculateCartSummaryUseCase(),
        ) as T
    }
}
