package ru.filimonov.hpa.ui.common.udf

sealed interface SimpleLoadingState : IState {
    data object Initial : SimpleLoadingState
    data object Loading : SimpleLoadingState
    data object Loaded : SimpleLoadingState
    data class Error(val error: Int) : SimpleLoadingState

    companion object {
        val factory = object : IState.Factory<SimpleLoadingState> {
            override val initState: SimpleLoadingState = Initial
            override val loadingState: SimpleLoadingState = Loading
            override val loadedState: SimpleLoadingState = Loaded

            override fun errorState(error: Int): SimpleLoadingState =
                Error(error)

            override fun isError(state: SimpleLoadingState): Boolean = state is Error
        }
    }
}
