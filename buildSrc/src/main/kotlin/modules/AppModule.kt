package modules

object AppModule : BaseModule() {
    override val name: String = "app"
    override val namespace = "ru.filimonov.hpa"
    override val versionCode = 1
    override val versionName = "1.0.0"
    override val properties: Map<String, String> = mapOf(
        "FIREBASE_AUTH_CLIENT_ID" to System.getenv("HPA_ANDROID_AUTH_CLIENT_ID"),
        "API_BASE_URL" to "http://localhost:8080",
        "API_AUTH_HEADER" to "Authorization",
    )
}
