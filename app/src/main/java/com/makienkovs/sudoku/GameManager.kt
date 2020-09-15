package com.makienkovs.sudoku

class GameManager(private val cells: Array<Array<Cell>>, private val keeper: Keeper) {

    private var gameField = Array(9) { Array(9) { 0 } }
    private var cell: Cell? = null
    private var hints = 0
    private lateinit var game: Game
    private val movies = arrayListOf<String>()
    private val selectedCells = arrayListOf<Cell>()
    var isPen = false
    private var undoMovie = false

    fun newGame(dif: Int) {
        reset()
        hints = 0
        movies.clear()
        selectedCells.clear()
        cell = null
        game = Game()
        gameField = game.arrFinal
        val fieldToOutput = game.fieldToOutput(dif)
        for (i in 0..8) {
            for (j in 0..8) {
                cells[i][j].i = i
                cells[i][j].j = j
                cells[i][j].setOnClickListener {}
                if (fieldToOutput[i][j] == 0) {
                    cells[i][j].setOnClickListener {
                        select(i, j)
                    }
                } else {
                    cells[i][j].amount = fieldToOutput[i][j]
                    cells[i][j].preset = true
                }
            }
        }
        movies.add(gamePositionToString())
    }

    private fun reset() {
        for (i in 0..8) {
            for (j in 0..8) {
                cells[i][j].reset()
            }
        }
    }

    fun loadGame() {
        val gamePosition = keeper.load("GAME_POSITION")
        val gameArray = keeper.load("GAME_ARRAY")
        if (gamePosition != null && gamePosition != "0" && gameArray != null && gameArray != "0") {
            reset()
            loadPosition(gamePosition)
            loadGameArray(gameArray)
            hints = keeper.load("HINTS").toString().toInt()
            cell = null
            selectedCells.clear()
            movies.clear()
            movies.add(gamePosition)
        }
    }

    fun saveGame() {
        keeper.save("GAME_POSITION", gamePositionToString())
        keeper.save("GAME_ARRAY", gameArrayToString())
        keeper.save("HINTS", hints.toString())
    }

    fun setNumber(num: Int) {
        if (cell != null) {
            if (isPen) {
                if (cell!!.amount != 0) {
                    setPen(cell!!.amount)
                    cell!!.amount = 0
                }
                setPen(num)
            } else {
                cell!!.amount = num
                cell!!.setAmountsToNull()
            }
            cell!!.invalidate()
            movies.add(gamePositionToString())
            selectedCells.add(cell!!)
        }
    }

    fun undo() {
        if (movies.size > 1 && selectedCells.size > 0) {
            undoMovie = true
            loadPosition(movies[movies.size - 2])
            select(selectedCells[selectedCells.size - 1].i, selectedCells[selectedCells.size - 1].j)
            cell = selectedCells[selectedCells.size - 1]
            movies.removeAt(movies.size - 1)
            selectedCells.removeAt(movies.size - 1)
        }
    }

    fun delete() {
        if (cell != null) {
            cell!!.amount = 0
            cell!!.setAmountsToNull()
            cell!!.invalidate()
            movies.add(gamePositionToString())
            selectedCells.add(cell!!)
        }
    }

    fun hint(maxHints: Int): Int {
        if (hints == maxHints) {
            return maxHints
        }
        if (cell != null) {
            for (i in 0..8) {
                for (j in 0..8) {
                    if (cell == cells[i][j]) {
                        cells[i][j].preset = true
                        cells[i][j].amount = gameField[i][j]
                        cells[i][j].setOnClickListener {}
                    }
                    cells[i][j].select = false
                    cells[i][j].mainSelect = false
                    cells[i][j].invalidate()
                }
            }
            cell = null
            hints++
            movies.clear()
            selectedCells.clear()
            movies.add(gamePositionToString())
        }
        return hints
    }

    fun pen() {
        isPen = !isPen
    }

    private fun setPen(num: Int) {
        if (cell != null) {
            if (cell!!.amounts[num - 1] == num) {
                cell!!.amounts[num - 1] = 0
            } else {
                cell!!.amounts[num - 1] = num
            }
        }
    }

    private fun select(cellI: Int, cellJ: Int) {

        if (cells[cellI][cellJ].mainSelect && !undoMovie) {
            cell = null
            for (i in 0..8) {
                for (j in 0..8) {
                    cells[i][j].select = false
                    cells[i][j].mainSelect = false
                    cells[i][j].invalidate()
                }
            }
            return
        }

        undoMovie = false

        for (i in 0..8) {
            for (j in 0..8) {
                cells[i][j].select = cellI == i || cellJ == j
                cells[i][j].mainSelect = false
            }
        }

        for (m in 0..8 step 3) {
            for (k in 0..8 step 3) {
                if (cellI in 0 + m..2 + m && cellJ in 0 + k..2 + k) {
                    for (i in 0 + m..2 + m) {
                        for (j in 0 + k..2 + k) {
                            cells[i][j].select = true
                        }
                    }
                }
            }
        }

        cells[cellI][cellJ].mainSelect = true
        cell = cells[cellI][cellJ]

        for (i in 0..8) {
            for (j in 0..8) {
                cells[i][j].invalidate()
            }
        }
    }

    private fun gamePositionToString(): String {
        val output = StringBuilder()
        for (i in 0..8) {
            for (j in 0..8) {
                if (cells[i][j].amount == 0 && cells[i][j].amountIsNull()) {
                    output.append(0)
                } else if (cells[i][j].amount != 0 && cells[i][j].preset) {
                    output.append('p')
                    output.append(cells[i][j].amount)
                } else if (cells[i][j].amount != 0 && !cells[i][j].preset) {
                    output.append('n')
                    output.append(cells[i][j].amount)
                } else if (!cells[i][j].amountIsNull()) {
                    output.append('a')
                    for (num in cells[i][j].amounts) {
                        output.append(num)
                    }
                }
                output.append('/')
            }
        }
        return output.toString()
    }

    private fun loadPosition(input: String) {
        val stringArray = input.split("/").toTypedArray()
        for (i in 0..8) {
            for (j in 0..8) {
                cells[i][j].i = i
                cells[i][j].j = j
                cells[i][j].setOnClickListener {}
                val currentString = stringArray[i * 9 + j]
                when {
                    currentString == "0" -> {
                        cells[i][j].amount = 0
                        cells[i][j].setAmountsToNull()
                        cells[i][j].setOnClickListener {
                            select(i, j)
                        }
                    }
                    currentString[0] == 'p' -> {
                        cells[i][j].amount = currentString[1].toString().toInt()
                        cells[i][j].preset = true
                    }
                    currentString[0] == 'n' -> {
                        cells[i][j].amount = currentString[1].toString().toInt()
                        cells[i][j].setOnClickListener {
                            select(i, j)
                        }
                    }
                    currentString[0] == 'a' -> {
                        for (k in 0..8) {
                            cells[i][j].amounts[k] = currentString[k + 1].toString().toInt()
                        }
                        cells[i][j].setOnClickListener {
                            select(i, j)
                        }
                    }
                }
                cells[i][j].invalidate()
            }
        }
    }

    private fun gameArrayToString(): String {
        val output = StringBuilder()
        for (i in 0..8) {
            for (j in 0..8) {
                output.append(gameField[i][j])
            }
        }
        return output.toString()
    }

    private fun loadGameArray(input: String) {
        for (i in 0..8) {
            for (j in 0..8) {
                gameField[i][j] = input[i * 9 + j].toString().toInt()
            }
        }
    }

    fun checkWin(): Boolean {
        for (i in 0..8) {
            for (j in 0..8) {
                if (cells[i][j].amount == 0)
                    return false
            }
        }

        for (i in 0..8) {
            var row = ""
            for (j in 0..8) {
                row += cells[i][j].amount.toString()
            }
            if (row.contains("1") &&
                row.contains("2") &&
                row.contains("3") &&
                row.contains("4") &&
                row.contains("5") &&
                row.contains("6") &&
                row.contains("7") &&
                row.contains("8") &&
                row.contains("9")
            ) {
//                println("row ok")
            } else {
                return false
            }

        }

        for (i in 0..8) {
            var column = ""
            for (j in 0..8) {
                column += cells[j][i].amount.toString()
            }
            if (column.contains("1") &&
                column.contains("2") &&
                column.contains("3") &&
                column.contains("4") &&
                column.contains("5") &&
                column.contains("6") &&
                column.contains("7") &&
                column.contains("8") &&
                column.contains("9")
            ) {
//                println("column ok")
            } else {
                return false
            }
        }

        for (m in 0..8 step 3) {
            for (k in 0..8 step 3) {
                var square = ""
                for (i in 0 + m..2 + m) {
                    for (j in 0 + k..2 + k) {
                        square += cells[j][i].amount.toString()
                    }
                }
                if (square.contains("1") &&
                    square.contains("2") &&
                    square.contains("3") &&
                    square.contains("4") &&
                    square.contains("5") &&
                    square.contains("6") &&
                    square.contains("7") &&
                    square.contains("8") &&
                    square.contains("9")
                ) {
//                println("square ok")
                } else {
                    return false
                }
            }
        }
        return true
    }

    fun clearListeners() {
        for (i in 0..8) {
            for (j in 0..8) {
                cells[i][j].setOnClickListener {}
                cells[i][j].select = false
                cells[i][j].mainSelect = false
                cells[i][j].invalidate()
            }
        }
    }
}