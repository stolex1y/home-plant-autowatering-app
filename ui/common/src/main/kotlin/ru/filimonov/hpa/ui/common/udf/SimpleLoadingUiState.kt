package ru.filimonov.hpa.ui.common.udf

sealed interface SimpleLoadingUiState : IUiState {
    data object Initial : SimpleLoadingUiState
    data object Loading : SimpleLoadingUiState
    data object Loaded : SimpleLoadingUiState
    data class Error(val error: Int) : SimpleLoadingUiState

    companion object {
        val factory = object : IUiState.Factory<SimpleLoadingUiState> {
            override val initState: SimpleLoadingUiState = Initial
            override val loadingState: SimpleLoadingUiState = Loading
            override val loadedState: SimpleLoadingUiState = Loaded

            override fun errorState(error: Int): SimpleLoadingUiState =
                Error(error)

            override fun isError(state: SimpleLoadingUiState): Boolean = state is Error
        }
    }
}
