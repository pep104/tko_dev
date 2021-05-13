package pro.apir.tko.data.mapper

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonObject
import pro.apir.tko.domain.failure.TrackingFailureCode

@Keep
class TrackingFailureCodeMapperImpl : TrackingFailureCodeMapper {

    override fun getFailureFromJSON(json: String?): TrackingFailureCode {
        return try {
            val errorObject: JsonObject = Gson().fromJson(json, JsonObject::class.java)
            val code = errorObject.get("code").asString
            when (code) {
                "already_entered" -> TrackingFailureCode.ALREADY_ENTERED
                else -> TrackingFailureCode.UNKNOWN
            }
        } catch (e: Exception) {
            Log.e("failure", "Tracking failure mapper e: ${e.message}")
            TrackingFailureCode.UNKNOWN
        }
    }
}