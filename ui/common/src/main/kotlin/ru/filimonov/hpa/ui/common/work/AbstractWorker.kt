package ru.filimonov.hpa.ui.common.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import ru.filimonov.hpa.ui.common.work.WorkUtils.deserialize
import timber.log.Timber
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.toJavaDuration

abstract class AbstractWorker<Input : Any, Output> protected constructor(
    private val notificationId: Int,
    private val notificationMsg: String,
    private val workName: String,
    private val inputArgClass: KClass<Input>,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    abstract suspend fun action(input: Input): kotlin.Result<Output>

    final override suspend fun doWork(): Result {
        val inputArg: Input = inputData.deserialize(inputArgClass)!!
        Timber.d("'$workName' started with arg: ${inputData.keyValueMap}")
        var result: Result = Result.failure()
        action(inputArg).onFailure {
            if (it is WorkErrorWrapper) {
                Timber.e("'$workName' finished with work error: ${it.workError.name}")
                result = Result.failure(WorkUtils.serialize(it.workError))
            } else {
                val unknownWorkError = WorkError.UnknownWorkError
                Timber.e(it, "'$workName' finished with unknown error")
                result = Result.failure(WorkUtils.serialize(unknownWorkError))
            }
        }.onSuccess {
            Timber.d("'$workName' finished successfully")
            result = if (it is Unit)
                Result.success()
            else
                Result.success(WorkUtils.serialize(it))
        }
        return result
    }

    final override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            notificationId,
            WorkUtils.createNotificationBackgroundWork(
                notificationMsg,
                applicationContext
            )
        )
    }

    companion object {
        const val TAG = "AbstractWorker"
        inline fun <reified Worker : AbstractWorker<Input, Output>, Input, Output> createWorkRequest(
            input: Input,
            tag: String = TAG,
            initialDelay: Duration = Duration.ZERO
        ): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<Worker>()
                .addTag(tag)
                .setInputData(WorkUtils.serialize(input))
                .setInitialDelay(initialDelay.toJavaDuration())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }
}
