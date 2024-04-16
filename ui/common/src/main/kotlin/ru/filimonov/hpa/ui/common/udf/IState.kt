package ru.filimonov.hpa.ui.common.udf

import androidx.annotation.StringRes

interface IState {
    interface Factory<S : IState> {
        val initState: S
        val loadingState: S
        val loadedState: S
        fun errorState(@StringRes error: Int): S
        fun isError(state: S): Boolean
    }
}
