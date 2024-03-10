package modules

object AppModule : BaseModule() {
    override val name: String = "app"
    override val namespace = "ru.filimonov.hpa"
    override val versionCode = 1
    override val versionName = "1.0.0"
//    override val testInstrumentationRunner: String = "ru.filimonov.hpa.AndroidJUnitRunnerWithHilt"
}
