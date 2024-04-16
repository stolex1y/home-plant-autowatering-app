package ru.filimonov.hpa.ui.common.work

data class WorkError(
    val name: String
) {
    companion object {
        val UnknownWorkError = WorkError("unknown_work_error")
    }
}
