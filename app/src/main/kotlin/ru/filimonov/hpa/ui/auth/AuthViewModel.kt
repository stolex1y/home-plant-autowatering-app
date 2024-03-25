package ru.filimonov.hpa.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.domain.auth.GoogleAuthTokenService
import ru.filimonov.hpa.domain.auth.UserAuthService
import ru.filimonov.hpa.ui.common.udf.IState
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    @Named(CoroutineNames.APPLICATION_SCOPE) private val applicationScope: CoroutineScope,
    @Named(CoroutineNames.DEFAULT_DISPATCHER) private val coroutineDispatcher: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val userAuthService: UserAuthService,
    private val googleAuthTokenService: GoogleAuthTokenService,
) : ViewModel() {

    private val _state: MutableStateFlow<State> =
        MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun onStartSigningIn() {
        viewModelScope.launch {
            _state.value = State.SigningIn
        }
    }

    fun init() {
        viewModelScope.launch {
            val userAccount = userAuthService.getUserAccount()
            if (userAccount == null) {
                _state.value = State.Expired
            } else {
                _state.value = State.SignedIn
            }
        }
    }

    fun onSuccessSignedIn(idToken: String) {
        applicationScope.launch(coroutineDispatcher) {
            firebaseAuth.signInWithCredential(
                GoogleAuthProvider.getCredential(idToken, null)
            ).await()
            googleAuthTokenService.setIdToken(idToken = idToken)
            //TODO(добавить получение refreshToken с сервера)
            _state.value = State.SignedIn
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userAuthService.cleanSession()
            _state.value = State.SignedOut
        }
    }

    sealed interface State : IState {
        data object Loading : State
        data object SigningIn : State
        data object SignedIn : State
        data object Expired : State
        data object SignedOut : State
    }
}