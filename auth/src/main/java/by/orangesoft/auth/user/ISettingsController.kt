package by.orangesoft.auth.user

interface ISettingsController<T> {

    var settings: T

    fun updateSettings(settings: T)
    fun reloadSettings()
}