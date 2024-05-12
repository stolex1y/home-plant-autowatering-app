package ru.filimonov.hpa.ui.common.udf

import androidx.annotation.StringRes

interface IUiState {
    companion object {
        fun <S : IUiState> createFactory(
            loaded: S,
            loading: S,
            init: S = loading,
            error: (error: Int) -> S
        ): Factory<S> {
            return object : Factory<S> {
                override val initState: S = init
                override val loadingState: S = loading
                override val loadedState: S = loaded

                override fun errorState(error: Int): S = error(error)

                override fun isError(state: S): Boolean {
                    return state::class == (error(0)::class)
                }
            }
        }
    }

    interface Factory<S : IUiState> {
        val initState: S
        val loadingState: S
        val loadedState: S
        fun errorState(@StringRes error: Int): S
        fun isError(state: S): Boolean
    }
}
