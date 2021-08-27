package pro.apir.tko.data.framework.manager.host

import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.domain.model.host.Host
import javax.inject.Inject

class HostManagerImpl @Inject constructor(
    private val preferences: PreferencesManager,
) : HostManager {

    private val HOST_KEY = "host_key"

    override fun saveHost(host: Host) {
        preferences.saveString(HOST_KEY, host.name)
    }

    override fun getHost(): Host {
       return Host.valueOf(preferences.getString(HOST_KEY, Host.APIR.name))
    }

}