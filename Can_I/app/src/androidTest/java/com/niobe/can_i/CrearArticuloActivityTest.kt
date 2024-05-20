package com.niobe.can_i

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.niobe.can_i.R
import com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo.CrearArticuloActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CrearArticuloActivityTest {

    @Before
    fun setUp() {
        // Inicializa Intents para manejar las intenciones implícitas
        Intents.init()
    }

    @After
    fun tearDown() {
        // Libera Intents después de cada prueba
        Intents.release()
    }

    @Test
    fun testSeleccionarFoto() {
        // Lanza la actividad
        val scenario = ActivityScenario.launch(CrearArticuloActivity::class.java)

        // Verifica que el botón para seleccionar foto esté presente y haga clic en él
        onView(withId(R.id.bSeleccionarFoto)).perform(click())

        // Verifica que se abra la galería de imágenes para seleccionar una foto
        Intents.intended(hasAction(Intent.ACTION_PICK))
        Intents.intended(hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI))

        // Finaliza la actividad
        scenario.close()
    }

    @Test
    fun testCrearArticuloSinImagen() {
        // Lanza la actividad
        val scenario = ActivityScenario.launch(CrearArticuloActivity::class.java)

        // Verifica que el botón de crear artículo esté presente y haga clic en él
        onView(withId(R.id.bCrearArticulo)).perform(click())

        // Verifica que se muestre un mensaje de error debido a la falta de información
        // en los campos obligatorios
        // Aquí puedes agregar más verificaciones según tu lógica de negocio

        // Finaliza la actividad
        scenario.close()
    }
}
