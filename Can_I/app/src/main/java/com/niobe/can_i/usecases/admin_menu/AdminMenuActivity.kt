package com.niobe.can_i.usecases.admin_menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.usecases.admin_menu.admin_articulos.AdminGArticulosActivity

class AdminMenuActivity : AppCompatActivity() {

    private lateinit var botonGestionArticulos : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu)
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

        }
    }
    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        botonGestionArticulos = findViewById(R.id.bGestionArticulos)
    }
    // Función para navegar a AdminGArticulosActivity
    private fun goToGestionArticulosActivity(){
        // Iniciamos la actividad de destino
        val intent = Intent(this, AdminGArticulosActivity::class.java)
        startActivity(intent)
    }

}