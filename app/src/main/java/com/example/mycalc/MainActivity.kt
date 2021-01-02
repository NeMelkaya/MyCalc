package com.example.mycalc

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.example.mycalc.Operation.*
import com.example.mycalc.Operation.Companion.checkOperation
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    private var firstNumber = ""
    private var secondNumber = ""
    private var operation = NULLL
    private var checkComma = 0
    private var onlyEqually = ""
    private var operationForOnlyEqually = NULLL
    private var answer = ""
    private var checkAchievement = 1000
    private var congratulations = 1

    private fun showAchievement() {
        if (checkAchievement > 0 && operation == DIV && secondNumber == "0") {
            if (checkAchievement == 1000) {
                val mpAchievement = MediaPlayer.create(this, R.raw.sound_achiv)
                val mpAchievement1 = MediaPlayer.create(this, R.raw.sound_achiv)

                achievement1.show("Achievement unlocked!", "1000IQ - DURACHOK")
                mpAchievement.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    achievement2.show("Congratulations!", "your IQ is unlocked")
                    mpAchievement1.start()
                    counterIQ.visibility = View.VISIBLE
                    checkAchievement = 100
                }, 1000)

            } else {
                checkAchievement--
                counterIQ.text = getString(R.string.IQ, checkAchievement)
            }
        }
    }

    private fun doOperation(oper: Operation, second: String) = when (oper) {
        PLUS -> (firstNumber.toDouble() + second.toDouble()).toString()
        MINUS -> (firstNumber.toDouble() - second.toDouble()).toString()
        MULT -> (firstNumber.toDouble() * second.toDouble()).toString()
        DIV -> (firstNumber.toDouble() / second.toDouble()).toString()
        else -> {
            checkAchievement--
            counterIQ.text = getString(R.string.IQ, checkAchievement)
            "000"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        group_number.setAllOnClickListener(View.OnClickListener {
            it as Button
            if (answer.replace(",", ".") == firstNumber) {
                frontInputPanel.text = "0"
                checkComma = 0
            }
            answer = ""
            if (it.text == ",") {
                if (checkComma == 0) {
                    frontInputPanel.append(it.text)
                    checkComma = 1
                }
            } else {
                if (frontInputPanel.text.length < 11)
                    if (frontInputPanel.text.toString() == "0")
                        frontInputPanel.text = it.text
                    else
                        frontInputPanel.append(it.text)
            }
            if (operation == NULLL) {
                firstNumber = frontInputPanel.text.toString().replace(",", ".")
            } else {
                secondNumber = frontInputPanel.text.toString().replace(",", ".")
            }
        })

        group_operation.setAllOnClickListener(View.OnClickListener {
            it as Button
            if (firstNumber != "" && secondNumber != "") {
                firstNumber = doOperation(operation, secondNumber)

                showAchievement()

                frontInputPanel.text = ""
                firstNumber = firstNumber.replace(",", ".")
                secondNumber = ""
                operation = checkOperation(it.text.toString())
            } else {
                if (firstNumber != "") {
                    operation = checkOperation(it.text.toString())
                    frontInputPanel.text = ""
                    if (checkComma != 0) {
                        firstNumber = firstNumber.replace(",", ".")
                    }
                    checkComma = 0
                }
            }

        })

        btnEqually.setOnClickListener {
            if (checkComma != 0 && secondNumber != "") {
                secondNumber = secondNumber.replace(",", ".")
                checkComma = 0
            }
            if (secondNumber == "" && firstNumber != "") {
                if (operation != NULLL) {
                    onlyEqually = firstNumber
                    operationForOnlyEqually = operation
                } else {
                    if (onlyEqually == "") {
                        onlyEqually = " "
                    }
                }
            } else
                onlyEqually = ""

            if (onlyEqually == "") {
                answer = doOperation(operation, secondNumber)
                showAchievement()
                secondNumber = ""
            } else {
                if (operationForOnlyEqually != NULLL) {
                    answer = doOperation(operationForOnlyEqually, onlyEqually)
                    showAchievement()
                } else
                    answer = firstNumber.replace(".", ",")
            }
            if (answer.length > 11) {
                if (answer.contains('E')) {
                    answer = answer.replace(",", "")
                    answer = answer.replace("E", "")
                }
                answer = answer.dropLast(answer.length - 11)
            }

            if (answer.contains(',')) {
                while (answer.last() == '0') {
                    answer = answer.dropLast(1)
                }
                if (answer.last() == ',') {
                    answer = answer.dropLast(1)
                    checkComma = 0
                } else {
                    checkComma = 1
                }
            }

            frontInputPanel.text = answer
            firstNumber = answer.replace(",", ".")
            operation = NULLL

            if (counterIQ.visibility == View.VISIBLE) {
                if (checkAchievement <= 0 && congratulations == 1) {
                    congratulations = 0
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=1Bix44C1EzY")
                        )
                    )
                }
            }
        }

        btnPlusMinus.setOnClickListener {
            if (frontInputPanel.text != "" && frontInputPanel.text != "0" && frontInputPanel.text != "0,0") {
                answer = (-frontInputPanel.text.toString().replace(",", ".").toDouble()).toString()
                if (operation == NULLL) {
                    firstNumber = answer
                } else {
                    secondNumber = answer
                }
                frontInputPanel.text = answer.replace(".", ",")
                answer = ""
            } else {
                if (counterIQ.visibility == View.VISIBLE) {
                    checkAchievement--
                    counterIQ.text = getString(R.string.IQ, checkAchievement)
                }
                frontInputPanel.text = ""
            }
        }

        btnPercent.setOnClickListener {
            if (secondNumber != "") {
                if (operation == MULT || operation == DIV)
                    frontInputPanel.text = (secondNumber.toDouble() / 100).toString()
                else
                    frontInputPanel.text =
                        (firstNumber.toDouble() / 100 * secondNumber.toDouble()).toString()
            } else {
                frontInputPanel.text = "0"
            }
            secondNumber = frontInputPanel.text.toString()
        }

        btnAC.setOnClickListener {
            frontInputPanel.text = "0"
            answer = ""
            firstNumber = ""
            secondNumber = ""
            operation = NULLL
            checkComma = 0
        }
    }
}


