/**
 * Clase de utilidad que almacena todas las constantes de la aplicación.
 */
package com.niobe.can_i.util

object Constants {
    /*
    * Aquí se almacenarán todas las constantes de nuestra app
    * */

    // Instancia de Firebase
    const val INSTANCE = "https://can-i-oxidodeniquel-2024-default-rtdb.europe-west1.firebasedatabase.app"

    // Tipos de artículos
    const val TIPO_ARTICULO_CERVEZA = "Cerveza"
    const val TIPO_ARTICULO_COPA = "Copa"
    const val TIPO_ARTICULO_SIN_ALCOHOL = "Sin alcohol"

    // Códigos de solicitud
    const val REQUEST_CODE_CREAR_ARTICULO = 1001

    const val EXTRA_ID = "extra_id"
    const val EXTRA_TIPO_ARTICULO = "extra_tipo_articulo"
    const val EXTRA_ARTICULOS_BARRA= "extra_articulos_barra"
    const val EXTRA_USUARIO= "extra_usuario"
    const val EXTRA_COMANDA = "extra_comanda"
    const val EXTRA_PRECIO_TOTAL = "extra_precio_total"

    const val PICK_IMAGE_REQUEST = 1

    const val TIPO_USUARIO_ADMINISTRADOR= "Administrador"
    const val TIPO_USUARIO_CAMARERO= "Camarero"
}
