package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo.CrearArticuloActivity

class AdminGArticulosActivity : AppCompatActivity() {

    private lateinit var botonCrearArticulos : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_garticulos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Inicializamos los componentes
        initUI()

        botonCrearArticulos.setOnClickListener {
            // Aquí se ejecutará cuando se presione el botón
            goToCrearArticuloActivity()
        }
    }
    //Función para inicializar los componentes del layout
    private fun initUI(){
        botonCrearArticulos = findViewById(R.id.bCrearArticulo)
    }
    // Función para navegar a CrearArticuloActivity
    private fun goToCrearArticuloActivity(){
        // Iniciamos la actividad de destino
        val intent = Intent(this, CrearArticuloActivity::class.java)
        startActivity(intent)
    }

}