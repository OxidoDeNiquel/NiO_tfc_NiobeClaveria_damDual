package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityAdminMenuBinding
import com.niobe.can_i.databinding.ActivityGestionArticulosBinding
import com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo.CrearArticuloActivity

class GestionArticulosActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGestionArticulosBinding
    private lateinit var botonCrear: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Inicializamos el binding
        binding = ActivityGestionArticulosBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Inicializamos los componentes
        initUI()
    }

    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        botonCrear = binding.bAnadirArticulo
        // Configuramos el click listener para el botón
        botonCrear.setOnClickListener {
            goToCrearArticulosActivity()
        }
    }

    // Función para navegar a AdminGArticulosActivity
    private fun goToCrearArticulosActivity(){
        // Iniciamos la actividad de destino
        val intent = Intent(this, CrearArticuloActivity::class.java)
        startActivity(intent)
    }
}