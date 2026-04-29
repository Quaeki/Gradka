package com.example.gradka

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradka.data.GradkaRepositoryImpl

class AuthViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = GradkaRepositoryImpl.getInstance(application)
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(repo) as T
    }
}