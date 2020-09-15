package com.makienkovs.sudoku

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var timer: Timer
    private lateinit var gameManager: GameManager
    private lateinit var keeper: Keeper
    private lateinit var level: String
    private var maxHints = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val cells: Array<Array<Cell>> = Array(9) { Array(9) { Cell(this, null) } }
        cells[0] = arrayOf(cell00, cell01, cell02, cell03, cell04, cell05, cell06, cell07, cell08)
        cells[1] = arrayOf(cell10, cell11, cell12, cell13, cell14, cell15, cell16, cell17, cell18)
        cells[2] = arrayOf(cell20, cell21, cell22, cell23, cell24, cell25, cell26, cell27, cell28)
        cells[3] = arrayOf(cell30, cell31, cell32, cell33, cell34, cell35, cell36, cell37, cell38)
        cells[4] = arrayOf(cell40, cell41, cell42, cell43, cell44, cell45, cell46, cell47, cell48)
        cells[5] = arrayOf(cell50, cell51, cell52, cell53, cell54, cell55, cell56, cell57, cell58)
        cells[6] = arrayOf(cell60, cell61, cell62, cell63, cell64, cell65, cell66, cell67, cell68)
        cells[7] = arrayOf(cell70, cell71, cell72, cell73, cell74, cell75, cell76, cell77, cell78)
        cells[8] = arrayOf(cell80, cell81, cell82, cell83, cell84, cell85, cell86, cell87, cell88)
        keeper = Keeper(this)
        gameManager = GameManager(cells, keeper)
        gameManager.loadGame()

        val numberButtons = arrayOf(b1, b2, b3, b4, b5, b6, b7, b8, b9)
        numberButtons.forEach {
            val buttonText = it.text.toString()
            it.setOnClickListener {
                gameManager.setNumber(buttonText.toInt())
                if (gameManager.checkWin()) {
                    win()
                }
            }
        }

        delete.setOnClickListener {
            gameManager.delete()
        }

        hint.setOnClickListener {
            val currentHints = hintsTextView.text[hintsTextView.length() - 3].toString().toInt()
            if (currentHints == maxHints) {
                Toast.makeText(this, R.string.maxHint, Toast.LENGTH_SHORT).show()
            }
            val hintsCount = gameManager.hint(maxHints)
            hintsTextView.text = "${getText(R.string.hints)} $hintsCount/$maxHints"
        }

        pen.setOnClickListener {
            gameManager.pen()
            val colorValue = ContextCompat.getColor(this, R.color.colorSelect)
            val colorDefault = ContextCompat.getColor(this, R.color.colorDefault)
            if (gameManager.isPen) {
                it.backgroundTintList = ColorStateList.valueOf(colorValue)
            } else {
                it.backgroundTintList = ColorStateList.valueOf(colorDefault)
            }
        }

        undo.setOnClickListener {
            gameManager.undo()
        }
    }

    private fun win() {
        timer.stop()
        val view = layoutInflater.inflate(R.layout.win_layout, null)
        saveRating()
        gameManager.clearListeners()
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(view)
            .setPositiveButton(getText(R.string.ok)) { _, _ ->
                run {
                    newGame()
                }
            }
            .create()
            .show()
    }

    private fun saveRating() {
        val levelToSaveString: String = when(level)  {
            getString(R.string.normal) -> "NORMAL"
            getString(R.string.hard) -> "HARD"
            else -> "EASY"
        }
        keeper.save(levelToSaveString, timer.time.toString())
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        gameManager.loadGame()
        timer = Timer(timerTextView, keeper)
        level = keeper.load("GAME_LEVEL").toString()
        if (level == "0" || level == getString(R.string.choose)) {
            level = getString(R.string.choose)
        } else {
            timer.load()
            timer.start()
        }
        maxHints = when (level) {
            getString(R.string.easy) -> 7
            getString(R.string.normal) -> 5
            getString(R.string.hard) -> 3
            else -> 0
        }
        levelTextView.text = level
        hintsTextView.text = "${getText(R.string.hints)} ${gameManager.hint(maxHints)}/$maxHints"
        if (gameManager.checkWin()) {
            win()
        }
    }

    override fun onPause() {
        super.onPause()
        timer.stop()
        gameManager.saveGame()
        keeper.save("GAME_LEVEL", level)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newGame -> newGame()
            R.id.rating -> rating()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun rating() {
        val intent = Intent(this, RatingActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun newGame() {
        val levels =
            arrayOf(getText(R.string.easy), getText(R.string.normal), getText(R.string.hard))
        var choice = 0
        AlertDialog.Builder(this)
            .setTitle(getText(R.string.new_game))
            .setCancelable(false)
            .setSingleChoiceItems(levels, 0) { _, i -> choice = i; level = levels[i].toString() }
            .setPositiveButton(getText(R.string.ok)) { _, _ ->
                run {
                    var dif = 0
                    when (choice) {
                        0 -> {
                            dif = 40
                            level = getText(R.string.easy).toString()
                        }
                        1 -> {
                            dif = 35
                            level = getText(R.string.normal).toString()
                        }
                        2 -> {
                            dif = 30
                            level = getText(R.string.hard).toString()
                        }
                    }
                    gameManager.newGame(dif)
                    maxHints = when (level) {
                        getString(R.string.easy) -> 7
                        getString(R.string.normal) -> 5
                        getString(R.string.hard) -> 3
                        else -> 0
                    }
                    levelTextView.text = level
                    hintsTextView.text =
                        "${getText(R.string.hints)} ${gameManager.hint(maxHints)}/$maxHints"
                    timer.stop()
                    timer = Timer(timerTextView, keeper)
                    timer.start()
                }
            }
            .setNegativeButton(getText(R.string.cancel)) { _, _ -> }
            .create()
            .show()
    }
}