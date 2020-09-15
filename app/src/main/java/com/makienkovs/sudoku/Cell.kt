package com.makienkovs.sudoku

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class Cell(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    var amount = 0
    var amounts = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    var preset = false
    var select = false
    var mainSelect = false
    var i = 0
    var j = 0

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = Rect(1, 1, width - 1, height - 1)
        paint.strokeWidth = 2F
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.textAlign = Paint.Align.CENTER
        paint.isAntiAlias = true
        canvas.drawRect(rect, paint)

        if (select)
            canvas.drawARGB(80, 0, 0, 255)
        if (mainSelect)
            canvas.drawARGB(80, 255, 0, 255)

        paint.style = Paint.Style.FILL_AND_STROKE

        if (amount != 0) {
            paint.textSize = height * 0.5f
            val xPos = (width / 2).toFloat()
            val yPos = height / 2 - ((paint.descent() + paint.ascent()) / 2)
            if (preset) paint.color = Color.RED
            else paint.color = Color.BLACK
            canvas.drawText("$amount", xPos, yPos, paint)
        } else if (!amountIsNull()) {
            paint.textSize = height * 0.25f
            paint.color = Color.BLACK
            if (amounts[0] != 0) {
                val xPos = width * 0.2f
                val yPos = height * 0.2f - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[0]}", xPos, yPos, paint)
            }
            if (amounts[1] != 0) {
                val xPos = (width / 2).toFloat()
                val yPos = height * 0.2f - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[1]}", xPos, yPos, paint)
            }
            if (amounts[2] != 0) {
                val xPos = width * 0.8f
                val yPos = height * 0.2f - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[2]}", xPos, yPos, paint)
            }
            if (amounts[3] != 0) {
                val xPos = width * 0.2f
                val yPos = height / 2 - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[3]}", xPos, yPos, paint)
            }
            if (amounts[4] != 0) {
                val xPos = (width / 2).toFloat()
                val yPos = height / 2 - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[4]}", xPos, yPos, paint)
            }
            if (amounts[5] != 0) {
                val xPos = width * 0.8f
                val yPos = height / 2 - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[5]}", xPos, yPos, paint)
            }
            if (amounts[6] != 0) {
                val xPos = width * 0.2f
                val yPos = height * 0.8f - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[6]}", xPos, yPos, paint)
            }
            if (amounts[7] != 0) {
                val xPos = (width / 2).toFloat()
                val yPos = height * 0.8f - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[7]}", xPos, yPos, paint)
            }
            if (amounts[8] != 0) {
                val xPos = width * 0.8f
                val yPos = height * 0.8f - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText("${amounts[8]}", xPos, yPos, paint)
            }
        }
    }

    fun amountIsNull(): Boolean {
        var count = 0
        for (i in amounts) {
            if (i == 0) count++
        }
        return (count == 9)
    }

    fun setAmountsToNull() {
        amounts = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    }

    fun reset() {
        amount = 0
        setAmountsToNull()
        preset = false
        select = false
        mainSelect = false
        invalidate()
    }
}