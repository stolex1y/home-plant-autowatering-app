package ru.filimonov.hpa.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.domain.model.DomainUserAccount
import ru.filimonov.hpa.domain.service.auth.UserAuthService
import ru.filimonov.hpa.ui.common.udf.IUiState
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    @Named(CoroutineNames.APPLICATION_SCOPE) private val applicationScope: CoroutineScope,
    @Named(CoroutineNames.DEFAULT_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher,
    private val userAuthService: UserAuthService,
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun onStartSigningIn() {
        viewModelScope.launch {
            _state.value = UiState.SigningIn
        }
    }

    fun init() {
        viewModelScope.launch {
            val userAccount = userAuthService.getUserAccount()
            if (userAccount == null) {
                _state.value = UiState.Expired
            } else {
                _state.value = UiState.SignedIn(userAccount)
            }
        }
    }

    fun authenticate(authCredential: AuthCredential) {
        applicationScope.launch(coroutineDispatcher) {
            userAuthService.authenticate(authCredential = authCredential)
                .onFailure {
                    _state.value = UiState.SignedOut
                }.onSuccess {
                    val userAccount = userAuthService.getUserAccount()
                    if (userAccount == null) {
                        _state.value =
                            UiState.Error(ru.filimonov.hpa.ui.common.R.string.internal_error)
                    } else {
                        _state.value = UiState.SignedIn(userAccount)
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userAuthService.cleanSession()
            _state.value = UiState.SignedOut
        }
    }

    sealed interface UiState : IUiState {
        data object Loading : UiState
        data object SigningIn : UiState
        data class SignedIn(
            val userAccount: DomainUserAccount
        ) : UiState

        data object Expired : UiState
        data object SignedOut : UiState
        data class Error(@StringRes val msg: Int) : UiState
    }
}
