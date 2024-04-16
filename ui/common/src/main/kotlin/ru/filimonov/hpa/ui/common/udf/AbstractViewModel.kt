package ru.filimonov.hpa.ui.common.udf

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import ru.filimonov.hpa.common.coroutine.CoroutineNames.APPLICATION_SCOPE
import ru.filimonov.hpa.common.exception.ServerIsNotAvailableException
import ru.filimonov.hpa.ui.common.R
import ru.filimonov.hpa.ui.common.work.WorkError
import ru.filimonov.hpa.ui.common.work.WorkUtils.deserialize
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider

abstract class AbstractViewModel<E : IEvent, D : IData, S : IState>(
    private val initData: D,
    private val stateFactory: IState.Factory<S>,
    @Named(APPLICATION_SCOPE) protected val applicationScope: CoroutineScope,
    private val workManager: Provider<WorkManager>,
) : ViewModel() {

    private val reloadEvent: Channel<Unit> = Channel()

    private val _state: MutableStateFlow<S> = MutableStateFlow(stateFactory.initState)
    val state: StateFlow<S> = _state.asStateFlow()
    var prevState: S = stateFactory.initState
        private set

    val data: StateFlow<D> = initDataLoading()

    private var work: StateFlow<WorkInfo>? = null
    protected var lastFinishedWork: WorkInfo? = null
        private set

    init {
        reloadData()
    }

    protected fun reloadData() {
        viewModelScope.launch {
            reloadEvent.send(Unit)
        }
    }

    abstract fun dispatchEvent(event: E)

    protected abstract fun loadData(): Flow<Result<D>>

    @StringRes
    protected open fun parseError(error: Throwable): Int {
        return when (error) {
            is ServerIsNotAvailableException -> R.string.server_is_not_available
            else -> R.string.internal_error
        }
    }

    @StringRes
    protected open fun parseWorkError(workError: WorkError): Int {
        return R.string.internal_error
    }

    protected fun updateState(state: S) {
        prevState = _state.value
        _state.value = state
    }

    protected fun updateState(error: Throwable) {
        Timber.e(error, "Updated state with error:")
        setErrorStateWith(parseError(error))
    }

    protected fun startWork(
        workRequest: WorkRequest,
        loadingState: S = stateFactory.loadingState,
        finishState: (WorkInfo) -> S = { stateFactory.loadedState },
        canceledState: S = stateFactory.loadedState,
    ) {
        viewModelScope.launch {
            updateState(loadingState)
            val finishedWorkInfo = startWork(workRequest)

            when (finishedWorkInfo.state) {
                WorkInfo.State.FAILED -> updateState(
                    finishedWorkInfo.outputData.deserialize(WorkError::class)!!
                )

                WorkInfo.State.CANCELLED -> updateState(canceledState)

                else -> updateState(finishState(finishedWorkInfo))
            }
        }
    }

    protected suspend fun startWork(
        workRequest: WorkRequest,
    ): WorkInfo {
        workManager.get().run {
            enqueue(workRequest)
            val currentWork = getWorkInfoByIdFlow(workRequest.id)
                .stateIn(viewModelScope)
            work = currentWork
            currentWork.takeWhile { !it.state.isFinished }.collect()
            val finishedWorkInfo = currentWork.value
            lastFinishedWork = finishedWorkInfo
            return finishedWorkInfo
        }
    }

    private fun setErrorStateWith(@StringRes errorMsg: Int) {
        updateState(stateFactory.errorState(errorMsg))
    }

    private fun updateState(workError: WorkError) {
        Timber.e("Updated state with work error: ${workError.name}")
        setErrorStateWith(parseWorkError(workError))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initDataLoading(): StateFlow<D> {
        return reloadEvent.consumeAsFlow().flatMapLatest {
            loadData()
        }
            .handleError()
            .mapNotNull { it.getOrNull() }
            .onEach {
                if (_state.value == stateFactory.initState || stateFactory.isError(_state.value)) {
                    _state.value = stateFactory.loadedState
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initData
            )
    }

    private fun <T> Flow<Result<T>>.handleError(): Flow<Result<T>> {
        return this.onEach {
            if (it.isFailure) {
                Timber.e(it.exceptionOrNull()!!, "error in flow")
                updateState(it.exceptionOrNull()!!)
            }
        }
    }
}
