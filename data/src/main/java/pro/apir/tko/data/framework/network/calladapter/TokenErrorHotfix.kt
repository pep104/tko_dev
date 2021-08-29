package pro.apir.tko.data.framework.network.calladapter

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */

private const val TOKEN_ERROR = "Given token not valid for any token type"
private const val TOKEN_ERROR_SECONDARY = "Token is invalid or expired"

//FIXME
internal fun String?.checkTokenError(): Boolean {
    return this != null && (contains(TOKEN_ERROR) || contains(TOKEN_ERROR_SECONDARY))
}