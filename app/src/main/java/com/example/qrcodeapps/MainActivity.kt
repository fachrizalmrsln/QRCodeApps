package com.example.qrcodeapps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private lateinit var buttonGenerate: Button
    private lateinit var buttonScan: Button
    private lateinit var imageView: ImageView
    private lateinit var editText: EditText
    private lateinit var textView: TextView

    private var stringText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
    }

    private fun initializeViews() {
        buttonGenerate = findViewById(R.id.button_generate)
        buttonScan = findViewById(R.id.button_scan)
        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        textView = findViewById(R.id.textView)

        buttonGenerate()
        buttonScan()
    }

    private fun buttonGenerate() {
        buttonGenerate.setOnClickListener {
            stringText = editText.text.toString().trim()
            if (stringText.isEmpty()){
                toast("Field cannot be empty !")
            } else {
                toast("QR Code Successfully Generated !")
                val bitmap = generatedQR(stringText)
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun buttonScan(){
        buttonScan.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(true)
            scanner.initiateScan()
        }
    }

    private fun generatedQR(value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.QR_CODE,
                    500, 500, null
            )

        } catch (e: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width

        val bitMatrixHeight = bitMatrix.height

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {

                pixels[offset + x] = if (bitMatrix.get(x, y))
                    ContextCompat.getColor(this, R.color.black)
                else
                    ContextCompat.getColor(this, R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888)

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    toast("Cancelled !")
                } else {
                    textView.text = "Your data from scanner is ${result.contents}"
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}
