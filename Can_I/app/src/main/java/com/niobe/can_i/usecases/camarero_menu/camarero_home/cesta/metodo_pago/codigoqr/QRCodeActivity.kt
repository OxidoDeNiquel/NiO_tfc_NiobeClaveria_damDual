package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.codigoqr

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.niobe.can_i.databinding.ActivityQrcodeBinding
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.resultado_operacion.OK_Activity
import com.niobe.can_i.util.Constants

class QRCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrcodeBinding
    private lateinit var ivQrCode: ImageView
    private var idComanda: String? = null
    private var idCamarero: String? = null
    private var precioTotal: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ivQrCode = binding.ivQrCode

        idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)
        precioTotal = intent.getDoubleExtra(Constants.EXTRA_PRECIO_TOTAL, 0.00)

        idComanda?.let {
            val paymentUrl = generatePaymentUrl(it, precioTotal)
            generateQRCode(paymentUrl)
        }
        binding.btnDone.setOnClickListener {
            navigateToOK()
        }
    }

    private fun navigateToOK() {
        val intent = Intent(this, OK_Activity::class.java)
        intent.putExtra(Constants.EXTRA_PRECIO_TOTAL, precioTotal)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
        finish()
    }

    private fun generatePaymentUrl(idComanda: String, precioTotal: Double): String {
        // Aquí construyes la URL de la página de éxito de pago con los parámetros si los necesitas
        // Por simplicidad, estamos dirigiendo directamente a la página de éxito
        val baseUrl = "https://oxidodeniquel.github.io/NiO_tfc_NiobeClaveria_damDual/"
        val uri = Uri.parse(baseUrl)
            .buildUpon()
            .appendQueryParameter("idComanda", idComanda)
            .appendQueryParameter("precioTotal", precioTotal.toString())
            .build()
        return uri.toString()
    }

    private fun generateQRCode(content: String) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) -0x1000000 else -0x1)
                }
            }
            ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
