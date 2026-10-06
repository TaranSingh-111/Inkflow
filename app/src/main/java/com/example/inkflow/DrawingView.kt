package com.example.inkflow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.Path
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.core.graphics.toColorInt

class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {

    //drawing path that follows the finger
    private lateinit var drawPath: FingerPath

    //what to draw
    private lateinit var canvasPaint: Paint

    //how to draw
    private lateinit var drawPaint: Paint
    private var color = Color.BLACK
    private lateinit var canvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private var brushSize: Float = 0.toFloat()
    private val paths = mutableListOf<FingerPath>()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        setupDrawing()
    }

    //2: View size is given
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    //3: Touch Happens
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action){
            //pressing on screen
            MotionEvent.ACTION_DOWN ->{
                drawPath.color = color
                drawPath.brushThickness = brushSize

                drawPath.reset()
                drawPath.moveTo(touchX!!, touchY!!)
            }
            //moving on screen, called continuously
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
            }
            //removing finger from screen
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath) //saving the drawn path to screen
                drawPath = FingerPath(color, brushSize)
            }
            else -> return false
        }
        invalidate() // tells that view has been changed draw it again
        return true
    }

    //4: Display the path
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap, 0f, 0f, drawPaint)
        //drawing the previously saved paths
        for(path in paths){
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas.drawPath(path, drawPaint)
        }
        //drawing the current path
        if(!drawPath.isEmpty){
            drawPaint.strokeWidth = drawPath.brushThickness
            drawPaint.color = drawPath.color
            canvas.drawPath(drawPath, drawPaint)

        }
    }

    //1: Values are initialized
    private fun setupDrawing(){
        drawPath = FingerPath(color, brushSize)
        drawPaint = Paint()
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND

        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20.toFloat()

    }


    //brush size changer
    fun changeBrushSize(newSize: Float){
        brushSize = newSize
    }


    fun changeBrushColor(newColor: Any){
        if(newColor is String){
            color = Color.parseColor(newColor)
            drawPaint.color = color
        }else{
            color = newColor as Int
            drawPaint.color = color
        }
    }

    fun undoPath(){
        if(paths.isNotEmpty()){
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    val brushSizeValue: Float get() = brushSize

    internal inner class FingerPath(var color: Int, var brushThickness: Float): Path()
}