package com.niobe.can_i.usecases.admin_menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityAdminMenuBinding
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity

class AdminMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMenuBinding

    private lateinit var botonGestionArticulos : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Inicializamos el binding
        binding = ActivityAdminMenuBinding.inflate(layoutInflater)

        //Asignamos el contentView al binding
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Inicializamos los componentes
        initUI()

        // Configuramos el click listener para el botón
        botonGestionArticulos.setOnClickListener {
            // Aquí se ejecutará cuando se presione el botón
            goToGestionArticulosActivity()
        }
    }
    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        botonGestionArticulos = binding.bGestionArticulos
    }
    // Función para navegar a AdminGArticulosActivity
    private fun goToGestionArticulosActivity(){
        // Iniciamos la actividad de destino
        val intent = Intent(this, GestionArticulosActivity::class.java)
        startActivity(intent)
    }

}