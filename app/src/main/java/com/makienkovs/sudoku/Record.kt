package com.makienkovs.sudoku

data class Record (val id: String, val name: String, var easy: String, var normal: String, var hard: String) {
    constructor() : this("", "", "", "", "")
}