package com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCrearArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import java.util.UUID

class CrearArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearArticuloBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearArticuloBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseUtil = FirebaseUtil()
        storage = FirebaseStorage.getInstance()

        // Inicializamos los componentes
        initUI()
    }

    private fun initUI() {
        binding.bCancelar.setOnClickListener { finish() }
        binding.bCrearArticulo.setOnClickListener { crearArticulo() }
        binding.bSeleccionarFoto.setOnClickListener { seleccionarFoto() }

        val tipoBebida = resources.getStringArray(R.array.tipos_de_bebidas)
        val adapter = ArrayAdapter(this, R.layout.list_desplegable, tipoBebida)
        binding.actvTipoBebida.setAdapter(adapter)
    }

    private fun seleccionarFoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.ivFotoArticulo.setImageURI(imageUri)
        }
    }

    private fun crearArticulo() {
        val nombreArticulo = binding.etNombreArticulo.text.toString()
        val tipoArticulo = binding.actvTipoBebida.text.toString()
        val precioArticulo = binding.etPrecio.text.toString().toDoubleOrNull()
        val stockArticulo = binding.etStock.text.toString().toIntOrNull()

        if (nombreArticulo.isNotEmpty() && tipoArticulo.isNotEmpty() && precioArticulo != null && stockArticulo != null) {
            if (imageUri != null) {
                subirImagenYGuardarArticulo(nombreArticulo, tipoArticulo, precioArticulo, stockArticulo)
            } else {
                guardarArticuloEnFirebase(nombreArticulo, tipoArticulo, precioArticulo, stockArticulo, null)
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subirImagenYGuardarArticulo(nombre: String, tipo: String, precio: Double, stock: Int) {
        val storageRef = storage.reference.child("articulos/${UUID.randomUUID()}.jpg")
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    guardarArticuloEnFirebase(nombre, tipo, precio, stock, uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarArticuloEnFirebase(nombre: String, tipo: String, precio: Double, stock: Int, imagenUrl: String?) {
        val articuloId = UUID.randomUUID().toString()
        val articulo = Articulo(articuloId, nombre, tipo, precio, stock, imagenUrl)
        firebaseUtil.guardarArticulo(articuloId, articulo) { success ->
            if (success) {
                Toast.makeText(this, "Artículo creado exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, GestionArticulosActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al crear el artículo", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
