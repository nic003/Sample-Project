package com.exam.kotlin.sample.exemption

/**
 * Created by msaycon on 01,Oct,2019
 */

sealed class Failure {
    object NetworkConnection : Failure()
    object ServerError : Failure()
    object PermissionError : Failure()

    abstract class FeatureFailure: Failure()
}