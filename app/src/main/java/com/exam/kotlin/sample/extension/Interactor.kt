package com.exam.kotlin.sample.extension

import com.exam.kotlin.sample.exemption.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by msaycon on 01,Oct,2019
 */
abstract class Interactor<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Response<Failure, Type>

    operator fun invoke(params: Params, onResult: (Response<Failure, Type>) -> Unit = {}) {
        val job = GlobalScope.async {
            run(params)
        }

        GlobalScope.launch(Dispatchers.Main) {
            onResult(job.await())
        }
    }

    class None
}