package com.example.gradka

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradka.domain.GradkaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthStep { SPLASH, WELCOME, PHONE, OTP, NAME, SUCCESS, RECOVERY }
enum class AuthMode { LOGIN, REGISTER }

data class AuthState(
    val screen: AuthStep = AuthStep.SPLASH,
    val mode: AuthMode = AuthMode.LOGIN,
    val phone: String = "",
    val otp: String = "",
    val name: String = "",
    val otpError: Boolean = false,
    val otpChecking: Boolean = false,
    val otpCountdown: Int = 59,
    val phoneError: String = "",
    val recoveryStep: Int = 0,
    val isAuthenticated: Boolean = false,
)

sealed class AuthEvent {
    object GoToWelcome : AuthEvent()
    data class SelectMode(val mode: AuthMode) : AuthEvent()
    data class PhoneDigit(val d: String) : AuthEvent()
    object PhoneDelete : AuthEvent()
    object PhoneSubmit : AuthEvent()
    data class OtpDigit(val d: String) : AuthEvent()
    object OtpDelete : AuthEvent()
    object OtpResend : AuthEvent()
    object EditPhone : AuthEvent()
    data class NameInput(val name: String) : AuthEvent()
    object NameSubmit : AuthEvent()
    object GoToRecovery : AuthEvent()
    object Back : AuthEvent()
    object Logout : AuthEvent()
}

class AuthViewModel(private val repository: GradkaRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            val session = repository.getSession()
            if (session != null) {
                _state.update {
                    it.copy(
                        phone = session.phone,
                        name = session.name,
                        isAuthenticated = true,
                    )
                }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.GoToWelcome -> _state.update { it.copy(screen = AuthStep.WELCOME) }
            is AuthEvent.SelectMode -> _state.update {
                it.copy(mode = event.mode, screen = AuthStep.PHONE, phone = "", phoneError = "")
            }
            is AuthEvent.PhoneDigit -> _state.update { s ->
                if (s.phone.length < 10) s.copy(phone = s.phone + event.d, phoneError = "") else s
            }
            AuthEvent.PhoneDelete -> _state.update { s ->
                s.copy(phone = s.phone.dropLast(1), phoneError = "")
            }
            AuthEvent.PhoneSubmit -> submitPhone()
            is AuthEvent.OtpDigit -> {
                val current = _state.value.otp
                if (current.length < 6) {
                    val newOtp = current + event.d
                    _state.update { it.copy(otp = newOtp, otpError = false) }
                    if (newOtp.length == 6) verifyOtp(newOtp)
                }
            }
            AuthEvent.OtpDelete -> _state.update { s ->
                s.copy(otp = s.otp.dropLast(1), otpError = false)
            }
            AuthEvent.OtpResend -> {
                _state.update { it.copy(otp = "", otpError = false, otpCountdown = 59) }
                startCountdown()
            }
            AuthEvent.EditPhone -> _state.update { s ->
                when (s.screen) {
                    AuthStep.OTP -> s.copy(screen = AuthStep.PHONE, otp = "", otpError = false)
                    AuthStep.RECOVERY -> s.copy(recoveryStep = 0, otp = "", otpError = false)
                    else -> s
                }
            }
            is AuthEvent.NameInput -> _state.update { it.copy(name = event.name) }
            AuthEvent.NameSubmit -> {
                val s = _state.value
                viewModelScope.launch {
                    repository.saveSession(phone = s.phone, name = s.name)
                }
                _state.update { it.copy(screen = AuthStep.SUCCESS) }
            }
            AuthEvent.GoToRecovery -> _state.update {
                it.copy(screen = AuthStep.RECOVERY, phone = "", otp = "", otpError = false, phoneError = "", recoveryStep = 0)
            }
            AuthEvent.Back -> back()
            AuthEvent.Logout -> viewModelScope.launch {
                repository.clearSession()
                _state.value = AuthState()
            }
        }
    }

    private fun submitPhone() {
        val s = _state.value
        if (s.phone.length < 10) {
            _state.update { it.copy(phoneError = "Введите полный номер телефона") }
            return
        }
        if (s.screen == AuthStep.RECOVERY) {
            _state.update { it.copy(otp = "", otpError = false, otpCountdown = 59, recoveryStep = 1) }
        } else {
            _state.update { it.copy(screen = AuthStep.OTP, otp = "", otpError = false, otpCountdown = 59) }
        }
        startCountdown()
    }

    private fun back() {
        _state.update { s ->
            when {
                s.screen == AuthStep.PHONE -> s.copy(screen = AuthStep.WELCOME, phone = "", phoneError = "")
                s.screen == AuthStep.OTP -> s.copy(screen = AuthStep.PHONE, otp = "", otpError = false)
                s.screen == AuthStep.NAME -> s.copy(screen = AuthStep.OTP, otp = "")
                s.screen == AuthStep.RECOVERY && s.recoveryStep == 0 -> s.copy(screen = AuthStep.WELCOME)
                s.screen == AuthStep.RECOVERY && s.recoveryStep == 1 -> s.copy(recoveryStep = 0, otp = "", otpError = false)
                else -> s
            }
        }
    }

    private fun verifyOtp(code: String) {
        _state.update { it.copy(otpChecking = true) }
        viewModelScope.launch {
            delay(800)
            val s = _state.value
            when {
                s.screen == AuthStep.RECOVERY -> _state.update { it.copy(otpChecking = false, recoveryStep = 2) }
                s.mode == AuthMode.REGISTER -> _state.update { it.copy(otpChecking = false, screen = AuthStep.NAME) }
                else -> {
                    repository.saveSession(phone = s.phone, name = s.name)
                    _state.update { it.copy(otpChecking = false, screen = AuthStep.SUCCESS) }
                }
            }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var t = 59
            while (t > 0) {
                delay(1000)
                t--
                _state.update { it.copy(otpCountdown = t) }
            }
        }
    }
}