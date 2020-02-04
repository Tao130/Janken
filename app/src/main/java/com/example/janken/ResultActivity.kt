package com.example.janken

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings.Global.putInt
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val id = intent.getIntExtra("MY_HAND", 0)

        val myHand: Int

        myHand = when(id) {
            R.id.gu -> {
                myHandImage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.choki -> {
                myHandImage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.pa -> {
                myHandImage.setImageResource(R.drawable.pa)
                pa
            }
            else -> gu
        }

        val comHand = getHand()
        when(comHand) {
            gu -> comHandImage.setImageResource(R.drawable.com_gu)
            choki -> comHandImage.setImageResource(R.drawable.com_choki)
            pa -> comHandImage.setImageResource(R.drawable.com_pa)
        }

        val gameResult = (comHand - myHand + 3) % 3
        when(gameResult) {
            0 -> resultLabel.setText(R.string.result_draw)
            1 -> resultLabel.setText(R.string.result_win)
            2 -> resultLabel.setText(R.string.result_lose)
        }

        backButton.setOnClickListener { finish() }
        saveData(myHand, comHand, gameResult)
    }

    private fun saveData(myHand: Int, comHand: Int, gameResult: Int) {
        val sharedPref = getSharedPreferences(
            "com.example.janken.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE
        )
        val gameCount = sharedPref.getInt("GAME_COUNT", 0)
        val winningStreakCount = sharedPref.getInt("WINNING_STREAK_COUNT", 0)
        val lastComHand = sharedPref.getInt("LAST_COM_HAND", 0)
        val lastGameResult = sharedPref.getInt("GAME_RESULT", -1)

        val editWinningStreakCount: Int =
            when {
                lastGameResult == 2 && gameResult == 2 ->
                    winningStreakCount + 1
                else ->
                    0
            }

        sharedPref.edit {
            putInt("GAME_COUNT", gameCount + 1)
            putInt("WINNING_STREAK_COUNT", editWinningStreakCount)
            putInt("LAST_MY_HAND", myHand)
            putInt("LAST_COM_HAND", comHand)
            putInt("BEFORE_LAST_COM_HAND", lastComHand)
            putInt("GAME_RESULT", gameResult)
        }
    }

    private fun getHand(): Int {
        var hand = (Math.random() * 3 ).toInt()
        val sharedPref = getSharedPreferences(
            "com.example.janken.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE
        )
        val gameCount = sharedPref.getInt("GAME_COUNT", 0)
        val winningStreakCount = sharedPref.getInt("WINNING_STREAK_COUNT", 0)
        val lastMyHand = sharedPref.getInt("LAST_MY_HAND", 0)
        val lastComHand = sharedPref.getInt("LAST_COM_HAND", 0)
        val beforeLastComHand = sharedPref.getInt("BEFORE_LAST_COM_HAND", 0)
        val gameResult = sharedPref.getInt("GAME_RESULT", -1)

        if (gameCount == 1) {
            if (gameResult == 2) {
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            } else if (gameResult == 1) {
                hand = (lastMyHand - 1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            }
        }
        return hand
    }
}
