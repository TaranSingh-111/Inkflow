package com.example.inkflow

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var brushSizeButton: ImageButton

    var size: Float = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        size = drawingView.brushSizeValue

        brushSizeButton = findViewById(R.id.button_brush_size)

        brushSizeButton.setOnClickListener {
            showSizeDialog()
        }
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
}