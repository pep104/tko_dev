package pro.apir.tko.domain.failure

import pro.apir.tko.core.exception.Failure

/**
 * Created by antonsarmatin
 * Date: 2020-03-25
 * Project: tko-android
 */
data class RouteTrackingFailure(val code: TrackingFailureCode = TrackingFailureCode.UNKNOWN) : Failure.FeatureFailure()

class RouteTrackingNotExist : Failure.FeatureFailure()

class RouteTrackingNotCompleted : Failure.FeatureFailure()