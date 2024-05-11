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
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.CrearUsuarioActivity
import com.niobe.can_i.util.Util

class AdminMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMenuBinding
    private lateinit var firebaseUtil: FirebaseUtil

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
        // Inicializamos Firebase
        firebaseUtil = FirebaseUtil()
        //Inicializamos los componentes
        initUI()
    }
    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        // Configuramos el click listener para el botón
        binding.bGestionArticulos.setOnClickListener {
            // Aquí se ejecutará cuando se presione el botón
            Util.changeActivity(this, GestionArticulosActivity::class.java)
        }
        binding.bGestionEmpleados.setOnClickListener {
            Util.changeActivity(this, CrearUsuarioActivity::class.java)
        }
        binding.bCerrarSesion.setOnClickListener {
            firebaseUtil.cerrarSesion(this)
        }
    }
}