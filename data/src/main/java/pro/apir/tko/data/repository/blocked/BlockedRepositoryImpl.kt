package pro.apir.tko.data.repository.blocked

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import pro.apir.tko.data.R
import pro.apir.tko.domain.repository.blocked.BlockedRepository
import javax.inject.Inject

class BlockedRepositoryImpl @Inject constructor(): BlockedRepository {

    private val IS_BLOCKED_KEY = "is_blocked"

    private val remoteConfig = Firebase.remoteConfig

    private val _blocked = MutableStateFlow(false)

    init {
        fetchConfig()
    }

    override fun getBlock() = _blocked

    override fun fetch() {
        fetchConfig()
    }

    private fun fetchConfig() {
        Log.e("blocked","fetchConfig")
        remoteConfig.setDefaultsAsync(R.xml.config_defaults)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            Log.e("blocked","taskComplete")
            if (task.isSuccessful) {
                Log.e("blocked","taskSuccessful")
                updateBlocked()
            }
        }
    }

    private fun updateBlocked() {
        val isBlocked = remoteConfig.getBoolean(IS_BLOCKED_KEY)
        Log.e("blocked","updateBlocked with $isBlocked")
        _blocked.tryEmit(isBlocked)
    }

}