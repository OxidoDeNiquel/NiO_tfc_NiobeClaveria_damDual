package com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.edit_articulo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityEditArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.util.Constants

class EditArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditArticuloBinding
    private lateinit var selectedImageUri: Uri
    private val firebaseUtil = FirebaseUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditArticuloBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        val articuloId = intent.getStringExtra("ARTICULO")
        val tipoBebida = resources.getStringArray(R.array.tipos_de_bebidas)
        val adapter = ArrayAdapter(
            this, R.layout.list_desplegable, tipoBebida
        )

        with(binding.actvTipoBebida) {
            setAdapter(adapter)
        }

        if (articuloId != null) {
            firebaseUtil.getArticulo(articuloId,
                onSuccess = { articulo ->
                    articulo?.let {
                        createUI(articulo)
                    } ?: run {
                        Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this, "Error: No se proporcionó un ID de artículo", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.bConfirmar.setOnClickListener {
            if (articuloId != null) {
                // Verificar si se ha seleccionado una imagen
                if (::selectedImageUri.isInitialized) {
                    // Si se seleccionó una imagen, subir la imagen a Firebase Storage y luego actualizar el artículo
                    uploadImageToFirebaseStorage(articuloId)
                } else {
                    // Si no se seleccionó una imagen, simplemente actualizar el artículo sin modificar la imagen
                    firebaseUtil.getArticulo(articuloId,
                        onSuccess = { articulo ->
                            articulo?.let {
                                updateArticulo(articulo)
                            } ?: run {
                                Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { errorMessage ->
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        binding.bCancelar.setOnClickListener {
            goToGestionArticulosActivity()
        }
        binding.bSeleccionarFoto.setOnClickListener {
            openImagePicker()
        }
    }

    private fun createUI(articulo: Articulo) {
        binding.etNombreArticulo.setText(articulo.nombre)
        binding.actvTipoBebida.setText(articulo.tipo)
        binding.etPrecio.setText(articulo.precio.toString())
        binding.etStock.setText(articulo.stock.toString())
    }

    private fun obtenerNuevoArticulo(): Articulo {
        val nombreArticulo = binding.etNombreArticulo.text.toString()
        val tipoArticulo = binding.actvTipoBebida.text.toString()
        val precioArticulo = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
        val stockArticulo = binding.etStock.text.toString().toIntOrNull() ?: 0

        return Articulo("", nombreArticulo, tipoArticulo, precioArticulo, stockArticulo)
    }

    private fun goToGestionArticulosActivity() {
        val intent = Intent(this, GestionArticulosActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivFotoArticulo.setImageURI(uri) // Mostrar la imagen seleccionada en un ImageView
            }
        }
    }

    private fun updateArticulo(articulo: Articulo) {
        firebaseUtil.actualizarArticulo(articulo.articuloId, articulo,
            onSuccess = {
                Toast.makeText(this, "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()
                goToGestionArticulosActivity()
            },
            onFailure = {
                Toast.makeText(this, "Error al actualizar el artículo", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun uploadImageToFirebaseStorage(articuloId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}")
        storageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Crear un nuevo objeto Articulo con la nueva URL de la imagen
                    val nuevoArticulo = obtenerNuevoArticulo().copy(imagenUrl = imageUrl)
                    // Actualizar el artículo en Firestore con el nuevo objeto Articulo
                    updateArticulo(nuevoArticulo)
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores al cargar la imagen
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }
}
