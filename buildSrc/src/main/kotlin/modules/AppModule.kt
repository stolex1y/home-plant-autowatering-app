package modules

object AppModule : BaseModule() {
    override val name: String = "app"
    override val namespace = "ru.filimonov.hpa"
    override val versionCode = 1
    override val versionName = "1.0.0"
    override val properties: Map<String, String> = mapOf(
        "FIREBASE_AUTH_CLIENT_ID" to System.getenv("HPA_ANDROID_AUTH_CLIENT_ID"),
        "API_BASE_URL" to "http://192.168.0.50:8080",
        "API_AUTH_HEADER" to "Authorization",
        "SYNC_DELAY_MS" to (1000L * 30L).toString(),
        "DEVICE_BASE_URL" to "http://192.168.100.100:8080",
        "MQTT_URL" to System.getenv("HPA_DEVICE_MQTT_URL"),
        "MQTT_DEVICE_USERNAME" to System.getenv("HPA_DEVICE_MQTT_USERNAME"),
        "MQTT_DEVICE_PASSWORD" to System.getenv("HPA_DEVICE_MQTT_PASSWORD"),
    )
}
