package com.example.pizzaparty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import kotlin.math.ceil
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts



const val TAG = "MainActivity"
const val GAME_STATE = "gameState"

class MainActivity : AppCompatActivity() {

    private lateinit var numAttendEditText: EditText
    private lateinit var numPizzasTextView: TextView
    private lateinit var howHungryRadioGroup: RadioGroup
    private lateinit var game: LightsOutGame
    private lateinit var lightGridLayout: GridLayout
    private var lightOnColor = 0
    private var lightOffColor = 0
    private var lightOnColorId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate was called")
        lightOnColorId = R.color.yellow
        lightGridLayout = findViewById(R.id.light_grid)
        numAttendEditText = findViewById(R.id.num_attend_edit_text)
        numPizzasTextView = findViewById(R.id.num_pizzas_text_view)
        howHungryRadioGroup = findViewById(R.id.hungry_radio_group)

        for (gridButton in lightGridLayout.children) {
            gridButton.setOnClickListener(this::onLightButtonClick)
        }

        lightOnColor = ContextCompat.getColor(this, R.color.yellow)
        lightOffColor = ContextCompat.getColor(this, R.color.black)

        game = LightsOutGame()
        if (savedInstanceState == null) {
            startGame()
        }
        else {
            game.state = savedInstanceState.getString(GAME_STATE)!!
            setButtonColors()
        }
        fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString(GAME_STATE, game.state)
        }
    }
    private fun startGame() {
        game.newGame()
        setButtonColors()
    }
    fun onLightsOutButtonClick(view: View)
    {
        val intent = Intent(this, LightsOutGame::class.java)
        startActivity(intent)
    }
    fun onChangeColorClick(view: View) {
        val intent = Intent(this, ColorActivity::class.java)
        colorResultLauncher.launch(intent)
        intent.putExtra(EXTRA_COLOR, lightOnColorId)
    }
    val colorResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val colorId = result.data!!.getIntExtra(EXTRA_COLOR, R.color.yellow)
            lightOnColor = ContextCompat.getColor(this, colorId)
            lightOnColorId = result.data!!.getIntExtra(EXTRA_COLOR, R.color.yellow)
            lightOnColor = ContextCompat.getColor(this, lightOnColorId)
            setButtonColors()
        }
    }
    private fun onLightButtonClick(view: View) {

        // Find the button's row and col
        val buttonIndex = lightGridLayout.indexOfChild(view)
        val row = buttonIndex / GRID_SIZE
        val col = buttonIndex % GRID_SIZE

        game.selectLight(row, col)
        setButtonColors()

        // Congratulate the user if the game is over
        if (game.isGameOver) {
            Toast.makeText(this, R.string.congrats, Toast.LENGTH_SHORT).show()
        }
    }
    private fun setButtonColors() {

        // Set all buttons' background color
        for (buttonIndex in 0 until lightGridLayout.childCount) {
            val gridButton = lightGridLayout.getChildAt(buttonIndex)

            // Find the button's row and col
            val row = buttonIndex / GRID_SIZE
            val col = buttonIndex % GRID_SIZE

            if (game.isLightOn(row, col)) {
                gridButton.setBackgroundColor(lightOnColor)
            } else {
                gridButton.setBackgroundColor(lightOffColor)
            }
        }
    }
    fun onHelpClick(view: View) {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }
    fun onNewGameClick(view: View) {
        startGame()
    }



    fun calculateClick(view: View) {

        // Get the text that was typed into the EditText
        val numAttendStr = numAttendEditText.text.toString()

        // Convert the text into an integer
        val numAttend = numAttendStr.toIntOrNull() ?: 0

        // Get hunger level selection
        val slicesPerPerson = when (howHungryRadioGroup.checkedRadioButtonId) {
            R.id.light_radio_button -> 2
            R.id.medium_radio_button -> 3
            else -> 4
        }

        // Get the number of pizzas needed
        val totalPizzas = ceil(numAttend * slicesPerPerson / SLICES_PER_PIZZA.toDouble()).toInt()
        numPizzasTextView.text = "Total pizzas: $totalPizzas"


        // Place totalPizzas into the string resource and display


    }


    }
