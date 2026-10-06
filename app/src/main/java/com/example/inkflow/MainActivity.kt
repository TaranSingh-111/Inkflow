package com.example.inkflow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var drawingView: DrawingView
    private lateinit var brushSizeButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var undoButton: ImageButton

    private lateinit var whiteColorButton: ImageButton
    private lateinit var blackColorButton: ImageButton
    private lateinit var redColorButton: ImageButton
    private lateinit var blueColorButton: ImageButton
    private lateinit var orangeColorButton: ImageButton
    private lateinit var greenColorButton: ImageButton
    private lateinit var purpleColorButton: ImageButton

    private lateinit var colorPickerButton: ImageButton

    private var cameraImageUri: Uri? = null
    var size: Float = 0.0f

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            findViewById<ImageView>(R.id.image_layer).setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {granted ->
            if(granted){
                cameraImageUri = createImageUri()
                takePicture.launch(cameraImageUri!!)
            }else{
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()){success ->
            if(success){
                cameraImageUri?.let{ uri ->
                    findViewById<ImageView>(R.id.image_layer).setImageURI(uri)
                }
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        size = drawingView.brushSizeValue

        brushSizeButton = findViewById(R.id.button_brush)
        galleryButton = findViewById(R.id.button_gallery)
        undoButton = findViewById(R.id.button_undo)
        whiteColorButton = findViewById(R.id.white_button)
        blackColorButton = findViewById(R.id.black_button)
        redColorButton = findViewById(R.id.red_button)
        blueColorButton = findViewById(R.id.blue_button)
        orangeColorButton = findViewById(R.id.orange_button)
        greenColorButton = findViewById(R.id.green_button)
        purpleColorButton = findViewById(R.id.purple_button)
        colorPickerButton = findViewById(R.id.button_color_picker)


        brushSizeButton.setOnClickListener(this)
        galleryButton.setOnClickListener(this)
        undoButton.setOnClickListener(this)
        whiteColorButton.setOnClickListener(this)
        blackColorButton.setOnClickListener(this)
        redColorButton.setOnClickListener(this)
        blueColorButton.setOnClickListener(this)
        orangeColorButton.setOnClickListener(this)
        greenColorButton.setOnClickListener(this)
        purpleColorButton.setOnClickListener(this)
        colorPickerButton.setOnClickListener(this)

        blackColorButton.isSelected = true
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

    private fun showImageSourceDialog(){
        val sourceDialog = Dialog(this@MainActivity)
        sourceDialog.setContentView(R.layout.dialog_image_source_selector)

        val cameraTextView: TextView = sourceDialog.findViewById<TextView>(R.id.camera_textview)
        val galleryTextView: TextView = sourceDialog.findViewById<TextView>(R.id.gallery_textview)

        cameraTextView.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED){
                cameraImageUri = createImageUri()
                takePicture.launch(cameraImageUri!!)
            }else{
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
            sourceDialog.hide()
        }

        galleryTextView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            sourceDialog.hide()
        }

        sourceDialog.show()
    }

    private fun createImageUri() : Uri{
        val imageFile = File(
            externalCacheDir,
            "camera_${System.currentTimeMillis()}.jpg"
        )

        return FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            imageFile
        )
    }
    private fun showColorPickerDialog(){
        //using a custom library
        val dialog = AmbilWarnaDialog(this, Color.BLACK, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(p0: AmbilWarnaDialog?) {

            }

            override fun onOk(p0: AmbilWarnaDialog?, p1: Int) {
                unselectColorButtons()
                drawingView.changeBrushColor(p1)
            }
        })
        dialog.show()
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
            R.id.button_color_picker ->{
                showColorPickerDialog()
            }
            R.id.button_brush ->{
                showSizeDialog()
            }
            R.id.button_gallery ->{
                showImageSourceDialog()
            }
            R.id.button_undo ->{
                drawingView.undoPath()
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