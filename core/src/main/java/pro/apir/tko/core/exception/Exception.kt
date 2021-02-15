package pro.apir.tko.core.exception

import java.io.IOException

/**
 * Created by antonsarmatin
 * Date: 15/02/2021
 * Project: tko
 */
class RefreshTokenExpiredException : IOException("Current refresh token was expired")
