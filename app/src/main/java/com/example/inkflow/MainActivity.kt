package com.example.inkflow

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var drawingView: DrawingView
    private lateinit var brushSizeButton: ImageButton

    private lateinit var whiteColorButton: ImageButton
    private lateinit var blackColorButton: ImageButton
    private lateinit var redColorButton: ImageButton
    private lateinit var blueColorButton: ImageButton
    private lateinit var orangeColorButton: ImageButton
    private lateinit var greenColorButton: ImageButton
    private lateinit var purpleColorButton: ImageButton

    var size: Float = 0.0f
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        size = drawingView.brushSizeValue

        brushSizeButton = findViewById(R.id.button_brush)

        whiteColorButton = findViewById(R.id.white_button)
        blackColorButton = findViewById(R.id.black_button)
        redColorButton = findViewById(R.id.red_button)
        blueColorButton = findViewById(R.id.blue_button)
        orangeColorButton = findViewById(R.id.orange_button)
        greenColorButton = findViewById(R.id.green_button)
        purpleColorButton = findViewById(R.id.purple_button)

        brushSizeButton.setOnClickListener {
            showSizeDialog()
        }

        whiteColorButton.setOnClickListener(this)
        blackColorButton.setOnClickListener(this)
        redColorButton.setOnClickListener(this)
        blueColorButton.setOnClickListener(this)
        orangeColorButton.setOnClickListener(this)
        greenColorButton.setOnClickListener(this)
        purpleColorButton.setOnClickListener(this)
    }

    private fun showSizeDialog(){
        val sizeDialog = Dialog(this@MainActivity)
        sizeDialog.setContentView(R.layout.dialog_brush)
        val seekBarProgress = sizeDialog.findViewById<SeekBar>(R.id.dialog_seek_bar)
        seekBarProgress.progress = size.toInt()
        val progressValue = sizeDialog.findViewById<TextView>(R.id.dialog_textView)
        progressValue.text = size.toString()

        seekBarProgress.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                drawingView.changeBrushSize(p1.toFloat())

                progressValue.text = p1.toFloat().toString()
                size = p1.toFloat()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
        sizeDialog.show()
    }

    override fun onClick(view: View?) {
        //the android studio color picker gives color in RRGGBBAA format
        //Color.parseColor() expects a #AARRGGBB format
        // FF in alpha channel is for full opacity
        //in color move FF to beginning of the color code and use #
        when(view?.id){
            R.id.white_button ->{
                unselectColorButtons()
                view.isSelected = true
                drawingView.changeBrushColor("#FFFFFFFF")
            }
            R.id.black_button ->{

                unselectColorButtons()
                view.isSelected = true
                drawingView.changeBrushColor("#FF000000")
            }
            R.id.red_button ->{
                unselectColorButtons()
                view.isSelected = true
                drawingView.changeBrushColor("#FFFF4444")
            }
            R.id.blue_button ->{
                unselectColorButtons()
                view.isSelected = true
                drawingView.changeBrushColor("#FF33B5E5")
            }
            R.id.orange_button ->{
                unselectColorButtons()
                view.isSelected = true
                drawingView.changeBrushColor("#FFFFBB33")
            }
            R.id.green_button ->{
                unselectColorButtons()
                view.isSelected = true
                drawingView.changeBrushColor("#FF99CC00")
            }
            R.id.purple_button ->{
            unselectColorButtons()
            view.isSelected = true
            drawingView.changeBrushColor("#FFAA66CC")
            }
        }
    }

    fun unselectColorButtons(){
        whiteColorButton.isSelected = false
        blackColorButton.isSelected = false
        redColorButton.isSelected = false
        blueColorButton.isSelected = false
        orangeColorButton.isSelected = false
        greenColorButton.isSelected = false
        purpleColorButton.isSelected = false
    }


}