package pro.apir.tko.data.framework.manager.credentials

/**
 * Created by antonsarmatin
 * Date: 01/09/2021
 * Project: tko
 */
interface EncryptedCredentials {

    fun clear()

    fun saveCredentials(login: String, password: String)

    fun getCredentials(): Pair<String, String>

}