package com.makienkovs.sudoku

import kotlin.math.floor
import kotlin.math.roundToInt

class Game {

    var arrFinal: Array<Array<Int>>

    init {
        val arrDefault: Array<Array<Int>> = Array(9) { Array(9) { 0 } }
        arrDefault[0] = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        arrDefault[1] = arrayOf(4, 5, 6, 7, 8, 9, 1, 2, 3)
        arrDefault[2] = arrayOf(7, 8, 9, 1, 2, 3, 4, 5, 6)
        arrDefault[3] = arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 1)
        arrDefault[4] = arrayOf(5, 6, 7, 8, 9, 1, 2, 3, 4)
        arrDefault[5] = arrayOf(8, 9, 1, 2, 3, 4, 5, 6, 7)
        arrDefault[6] = arrayOf(3, 4, 5, 6, 7, 8, 9, 1, 2)
        arrDefault[7] = arrayOf(6, 7, 8, 9, 1, 2, 3, 4, 5)
        arrDefault[8] = arrayOf(9, 1, 2, 3, 4, 5, 6, 7, 8)

        val arrOfChoices: Array<Array<Int>> = Array(6) { Array(3) { 0 } }
        arrOfChoices[0] = arrayOf(0, 1, 2)
        arrOfChoices[1] = arrayOf(0, 2, 1)
        arrOfChoices[2] = arrayOf(1, 0, 2)
        arrOfChoices[3] = arrayOf(1, 2, 0)
        arrOfChoices[4] = arrayOf(2, 1, 0)
        arrOfChoices[5] = arrayOf(2, 0, 1)

        var choiceBlock1 = arrOfChoices[floor(Math.random() * 6).toInt()]
        var choiceBlock2 = arrOfChoices[floor(Math.random() * 6).toInt()]
        var choiceBlock3 = arrOfChoices[floor(Math.random() * 6).toInt()]
        var choiceBlocks = floor(Math.random() * 6).toInt()

        var block1: Array<Array<Int>> = Array(3) { Array(9) { 0 } }
        block1[0] = arrDefault[0]
        block1[1] = arrDefault[1]
        block1[2] = arrDefault[2]

        var block2: Array<Array<Int>> = Array(3) { Array(9) { 0 } }
        block2[0] = arrDefault[3]
        block2[1] = arrDefault[4]
        block2[2] = arrDefault[5]

        var block3: Array<Array<Int>> = Array(3) { Array(9) { 0 } }
        block3[0] = arrDefault[6]
        block3[1] = arrDefault[7]
        block3[2] = arrDefault[8]

        block1 = sortArr(block1, choiceBlock1)
        block2 = sortArr(block2, choiceBlock2)
        block3 = sortArr(block3, choiceBlock3)

        val arrPreFinal: Array<Array<Int>> = sortBlocks(block1, block2, block3, choiceBlocks)

        val arrDefaultSortedTrans: Array<Array<Int>> = Array(9) { Array(9) { 0 } }

        for (i in 0..8) {
            for (j in 0..8) {
                arrDefaultSortedTrans[i][j] = arrPreFinal[j][i]
            }
        }

        choiceBlock1 = arrOfChoices[floor(Math.random() * 6).toInt()]
        choiceBlock2 = arrOfChoices[floor(Math.random() * 6).toInt()]
        choiceBlock3 = arrOfChoices[floor(Math.random() * 6).toInt()]
        choiceBlocks = floor(Math.random() * 6).toInt()

        block1[0] = arrDefaultSortedTrans[0]
        block1[1] = arrDefaultSortedTrans[1]
        block1[2] = arrDefaultSortedTrans[2]

        block2[0] = arrDefaultSortedTrans[3]
        block2[1] = arrDefaultSortedTrans[4]
        block2[2] = arrDefaultSortedTrans[5]

        block3[0] = arrDefaultSortedTrans[6]
        block3[1] = arrDefaultSortedTrans[7]
        block3[2] = arrDefaultSortedTrans[8]

        block1 = sortArr(block1, choiceBlock1)
        block2 = sortArr(block2, choiceBlock2)
        block3 = sortArr(block3, choiceBlock3)

        val arrFinalNo = sortBlocks(block1, block2, block3, choiceBlocks)
        val arrFinalYes: Array<Array<Int>> = Array(9) { Array(9) { 0 } }

        for (i in 0..8) {
            for (j in 0..8) {
                arrFinalYes[i][j] = arrFinalNo[j][i]
            }
        }

        arrFinal = if (Math.random() > 0.5)
            arrFinalYes
        else
            arrFinalNo
    }

    private fun sortArr(arrToSort: Array<Array<Int>>, arrMask: Array<Int>): Array<Array<Int>> {
        val arrToReturn: Array<Array<Int>> = Array(3) { Array(9) { 0 } }
        for (i in 0..2) {
            val arr = arrToSort[arrMask[i]]
            arrToReturn[i] = arr
        }
        return arrToReturn
    }

    private fun sortBlocks(
        block1: Array<Array<Int>>,
        block2: Array<Array<Int>>,
        block3: Array<Array<Int>>,
        choiceBlocks: Int
    ): Array<Array<Int>> {
        return when (choiceBlocks) {
            1 -> fromBlocksToArr(block1, block3, block2)
            2 -> fromBlocksToArr(block2, block1, block3)
            3 -> fromBlocksToArr(block2, block3, block1)
            4 -> fromBlocksToArr(block3, block2, block1)
            5 -> fromBlocksToArr(block3, block1, block2)
            else -> fromBlocksToArr(block1, block2, block3)
        }
    }

    private fun fromBlocksToArr(
        block1: Array<Array<Int>>,
        block2: Array<Array<Int>>,
        block3: Array<Array<Int>>
    ): Array<Array<Int>> {
        val arrToReturn: Array<Array<Int>> = Array(9) { Array(9) { 0 } }
        arrToReturn[0] = block1[0]
        arrToReturn[1] = block1[1]
        arrToReturn[2] = block1[2]
        arrToReturn[3] = block2[0]
        arrToReturn[4] = block2[1]
        arrToReturn[5] = block2[2]
        arrToReturn[6] = block3[0]
        arrToReturn[7] = block3[1]
        arrToReturn[8] = block3[2]
        return arrToReturn
    }

    fun fieldToOutput(dif: Int): Array<Array<Int>> {
        val arrOutput: Array<Array<Int>> = Array(9) { Array(9) { 0 } }
        var count = 0
        while (count < dif) {
            val i = (Math.random() * 8).roundToInt()
            val j = (Math.random() * 8).roundToInt()
            if (arrOutput[i][j] == 0) {
                arrOutput[i][j] = arrFinal[i][j]
                count++
            }
        }
        return arrOutput
    }
}