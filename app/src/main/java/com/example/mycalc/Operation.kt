package com.example.mycalc

enum class Operation {
    PLUS,
    MINUS,
    MULT,
    DIV,
    NULLL;

    companion object {
        fun checkOperation(x: String): Operation {
            return when (x) {
                "+" -> PLUS
                "-" -> MINUS
                "X" -> MULT
                "÷" -> DIV
                "" -> NULLL

                else -> throw IllegalStateException()
            }
        }
    }
}
