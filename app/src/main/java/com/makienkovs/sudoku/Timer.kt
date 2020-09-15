package com.makienkovs.sudoku

import android.widget.TextView
import java.util.concurrent.TimeUnit

class Timer(private val view: TextView, private val keeper: Keeper) {
    private var isEnd = false
    var time = 0L

    fun start() {
        val startTime = System.currentTimeMillis() - time
        Thread {
            try {
                do {
                    TimeUnit.MILLISECONDS.sleep(50)
                    time = System.currentTimeMillis() - startTime
                    view.post {
                        view.text = String.format(
                            "%02d:%02d:%02d",
                            time / 3600 / 1000,
                            time / 1000 / 60 % 60,
                            time / 1000 % 60
                        )
                    }
                } while (!isEnd)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun stop() {
        keeper.save("TIME", time.toString())
        isEnd = true
    }

    fun load() {
        time = keeper.load("TIME")!!.toLong()
    }
}