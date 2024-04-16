package ru.filimonov.hpa.ui.device.adding.model

import ru.filimonov.hpa.ui.common.work.WorkError

object AddingErrors {
    val DeviceAlreadyAddedError = WorkError(name = "device_already_added")
    val InvalidDeviceConfigurationError = WorkError(name = "invalid_device_configuration")
}
