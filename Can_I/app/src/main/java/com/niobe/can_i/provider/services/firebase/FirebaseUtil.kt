package com.niobe.can_i.provider.services.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.model.Barra
import com.niobe.can_i.model.Camarero
import com.niobe.can_i.model.Comanda
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.usecases.login.LogInActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUtil {

    /**
     * Clase de utilidad para interactuar con Firebase.
     */

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Cierra la sesión del usuario y lo redirige a la pantalla de inicio de sesión.
     *
     * @param context Contexto de la aplicación.
     */
    fun cerrarSesion(context: Context) {
        auth.signOut()
        // Redirigir al usuario de vuelta a la pantalla de inicio de sesión
        val intent = Intent(context, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    /**
     * Lee los artículos de Firestore filtrados por tipo de artículo.
     *
     * @param tipoArticulo Tipo de artículo a filtrar.
     * @param callback Función de retorno que proporciona la lista de artículos.
     */
    fun leerArticulos(tipoArticulo: String, callback: (List<Articulo>) -> Unit) {
        firestore.collection("articulos")
            .whereEqualTo("tipo", tipoArticulo)
            .get()
            .addOnSuccessListener { result ->
                val articulos: MutableList<Articulo> = mutableListOf()
                for (document in result) {
                    val articulo = document.toObject(Articulo::class.java)
                    articulos.add(articulo)
                }
                callback(articulos)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al leer datos de Firestore: $exception")
                callback(emptyList())
            }
    }

    /**
     * Busca artículos por tipo y nombre en Firestore.
     *
     * @param tipoArticulo Tipo de artículo a buscar.
     * @param query Texto de búsqueda para el nombre del artículo.
     * @param callback Función de retorno que recibe la lista de artículos encontrados.
     */
    fun buscarArticulosPorTipoYNombre(tipoArticulo: String, query: String, callback: (List<Articulo>) -> Unit) {
        firestore.collection("articulos")
            .whereEqualTo("tipo", tipoArticulo)
            .get()
            .addOnSuccessListener { result ->
                val articulos: MutableList<Articulo> = mutableListOf()
                for (document in result) {
                    val articulo = document.toObject(Articulo::class.java)
                    if (articulo.nombre.contains(query, ignoreCase = true)) {
                        articulos.add(articulo)
                    }
                }
                callback(articulos)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al buscar datos en Firestore: $exception")
                callback(emptyList())
            }
    }


    /**
     * Elimina un artículo de Firestore.
     *
     * @param articuloId ID del artículo a eliminar.
     * @param callback Función de retorno que indica si la operación fue exitosa.
     */
    fun eliminarArticulo(articuloId: String, callback: (Boolean) -> Unit) {
        firestore.collection("articulos")
            .whereEqualTo("articuloId", articuloId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("SUCCESS", "Documento eliminado correctamente")
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("ERROR", "Error al eliminar documento", e)
                            callback(false)
                        }
                } else {
                    Log.e("ERROR", "No se encontró ningún artículo con el ID $articuloId")
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al obtener artículo: $exception")
                callback(false)
            }
    }

    /**
     * Actualiza un artículo en Firestore.
     *
     * @param articuloId ID del artículo a actualizar.
     * @param articulo Nuevo objeto Articulo con los datos actualizados.
     * @param onSuccess Función de retorno que se llama si la actualización es exitosa.
     * @param onFailure Función de retorno que se llama si la actualización falla.
     */
    fun actualizarArticulo(articuloId: String, articulo: Articulo, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection("articulos").document(articuloId).set(articulo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    /**
     * Sube una imagen al almacenamiento de Firebase.
     *
     * @param imageUri URI de la imagen a subir.
     * @param onSuccess Función de retorno que proporciona la URL de la imagen subida.
     * @param onFailure Función de retorno que se llama si la carga de la imagen falla.
     */
    fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Obtiene un artículo de Firestore por su ID.
     *
     * @param articuloId ID del artículo a obtener.
     * @param onSuccess Función de retorno que proporciona el objeto Articulo si se encuentra.
     * @param onFailure Función de retorno que se llama si la operación falla.
     */
    fun getArticulo(articuloId: String, onSuccess: (Articulo?) -> Unit, onFailure: (String) -> Unit) {
        val articuloRef = firestore.collection("articulos").document(articuloId)
        articuloRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val articulo = document.toObject(Articulo::class.java)
                    onSuccess(articulo)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al obtener el artículo")
            }
    }

    /**
     * Guarda un artículo en Firestore.
     *
     * @param articuloId ID del artículo a guardar.
     * @param articulo Objeto Articulo a guardar en Firestore.
     * @param callback Función de retorno que indica si la operación fue exitosa.
     */
    fun guardarArticulo(articuloId: String, articulo: Articulo, callback: (Boolean) -> Unit) {
        firestore.collection("articulos").document(articuloId).set(articulo)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    /**
     * Obtiene una barra de Firestore por su ID.
     *
     * @param idBarra ID de la barra a obtener.
     * @param onSuccess Función de retorno que proporciona el objeto Barra si se encuentra.
     * @param onFailure Función de retorno que se llama si la operación falla.
     */
    fun getBarra(idBarra: String, onSuccess: (Barra?) -> Unit, onFailure: (String) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("barras")
            .document(idBarra)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val barra = documentSnapshot.toObject(Barra::class.java)
                    onSuccess(barra)
                } else {
                    onFailure("No se encontró ninguna barra con el ID $idBarra")
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error al obtener la barra: ${exception.message}")
            }
    }

    /**
     * Borra una barra de Firestore por su ID.
     *
     * @param idBarra ID de la barra a borrar.
     * @param onSuccess Función de retorno que se llama si la operación de borrado es exitosa.
     * @param onFailure Función de retorno que se llama si la operación de borrado falla.
     */
    fun borrarBarra(idBarra: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val barrasCollection = firestore.collection("barras")

        barrasCollection.document(idBarra)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure("Error al borrar la barra: ${exception.message}")
            }
    }

    /**
     * Lee los artículos de Firestore asociados a una barra específica por su ID.
     *
     * @param idBarra ID de la barra cuyos artículos se desean leer.
     * @param callback Función de retorno que proporciona la lista de artículos asociados a la barra.
     */
    fun leerArticulosPorIdBarra(idBarra: String, callback: (List<Articulo>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val articulosCollection = firestore.collection("articulos_barra")

        articulosCollection.whereEqualTo("idBarra", idBarra)
            .get()
            .addOnSuccessListener { result ->
                val articulos = mutableListOf<Articulo>()
                for (document in result) {
                    val articulo = document.toObject(Articulo::class.java)
                    articulos.add(articulo)
                }
                callback(articulos)
            }
            .addOnFailureListener { exception ->
                // Manejar errores de base de datos aquí
                Log.e("FirebaseUtil", "Error al leer artículos: $exception")
            }
    }

    /**
     * Lee los usuarios de Firestore filtrados por rol.
     *
     * @param rol Rol de los usuarios a filtrar.
     * @param callback Función de retorno que proporciona la lista de usuarios filtrados por rol.
     */
    fun leerUsuariosPorRol(rol: String, callback: (List<Usuario>) -> Unit) {
        firestore.collection("usuarios")
            .whereEqualTo("rol", rol)
            .get()
            .addOnSuccessListener { result ->
                val usuarios: MutableList<Usuario> = mutableListOf()
                for (document in result) {
                    val usuario = document.toObject(Usuario::class.java)
                    usuarios.add(usuario)
                }
                callback(usuarios)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al leer datos de Firestore: $exception")
                callback(emptyList())
            }
    }

    /**
     * Elimina un usuario de Firestore por su ID.
     *
     * @param documentId ID del documento de usuario a eliminar.
     * @param onSuccess Función de retorno que se llama si la operación de eliminación es exitosa.
     * @param onFailure Función de retorno que se llama si la operación de eliminación falla.
     */
    fun eliminarUsuario(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rol = document.getString("rol")
                    db.collection("usuarios").document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            when (rol) {
                                Constants.TIPO_USUARIO_ADMINISTRADOR -> {
                                    db.collection("administradores").document(documentId)
                                        .delete()
                                        .addOnSuccessListener {
                                            onSuccess()
                                        }
                                        .addOnFailureListener { exception ->
                                            onFailure(exception)
                                        }
                                }
                                Constants.TIPO_USUARIO_CAMARERO -> {
                                    db.collection("camareros").document(documentId)
                                        .delete()
                                        .addOnSuccessListener {
                                            onSuccess()
                                        }
                                        .addOnFailureListener { exception ->
                                            onFailure(exception)
                                        }
                                }
                                else -> {
                                    onSuccess()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                } else {
                    onFailure(Exception("El documento no existe"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    /**
     * Obtiene un usuario de Firestore por su ID.
     *
     * @param documentId ID del documento de usuario a obtener.
     * @param onSuccess Función de retorno que proporciona el objeto Usuario si se encuentra.
     * @param onFailure Función de retorno que se llama si la operación falla.
     */
    fun obtenerUsuarioPorId(documentId: String, onSuccess: (Usuario?) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    onSuccess(usuario)
                } else {
                    // El documento no existe, devolvemos null
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Crea una nueva comanda en Firestore.
     *
     * @param idCamarero Objeto Camarero que representa al camarero que realiza la comanda.
     * @param onSuccess Función de retorno que proporciona el ID de la comanda creada si la operación es exitosa.
     * @param onFailure Función de retorno que se llama si la operación falla.
     */
    fun crearComanda(idCamarero: Camarero, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        // Genera un ID aleatorio para la comanda
        val idComanda = UUID.randomUUID().toString()

        // Obtén la fecha y hora actual
        val fechaHoraActual = Util.obtenerFechaHoraActual()
        val pagada = false

        // Crea una instancia de Comanda con los datos proporcionados
        val comanda = Comanda(idComanda, idCamarero, fechaHoraActual, 0.00, pagada)

        // Agrega la comanda a Firestore con el ID generado
        firestore.collection("comandas")
            .document(idComanda)
            .set(comanda)
            .addOnSuccessListener {
                println("Comanda creada correctamente en Firestore")
                onSuccess(idComanda) // Devuelve el ID de la comanda creada
            }
            .addOnFailureListener { e ->
                println("Error al crear la comanda: $e")
                onFailure(e)
            }
    }

    /**
     * Obtiene un camarero de Firestore por su ID.
     *
     * @param documentId ID del documento de camarero a obtener.
     * @param onSuccess Función de retorno que proporciona el objeto Camarero si se encuentra.
     * @param onFailure Función de retorno que se llama si la operación falla.
     */
    fun obtenerCamareroPorId(documentId: String, onSuccess: (Camarero?) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("camareros")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val camarero = document.toObject(Camarero::class.java)
                    onSuccess(camarero)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Obtiene un artículo de Firestore por su ID.
     *
     * @param documentId ID del documento de artículo a obtener.
     * @param onSuccess Función de retorno que proporciona el objeto Articulo si se encuentra.
     * @param onFailure Función de retorno que se llama si la operación falla.
     */
    fun obtenerArticuloPorId(documentId: String, onSuccess: (Articulo?) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("articulos")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val articulo = document.toObject(Articulo::class.java)
                    onSuccess(articulo)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Obtiene los artículos asociados a una comanda específica por su ID de comanda.
     *
     * @param idComanda ID de la comanda cuyos artículos se desean obtener.
     * @param callback Función de retorno que proporciona la lista de artículos asociados a la comanda.
     */
    fun getArticulosComandaByComanda(idComanda: String, callback: (List<ArticulosComanda>) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .get()
            .addOnSuccessListener { result ->
                val articulosComandaList = mutableListOf<ArticulosComanda>()
                for (document in result) {
                    val articuloComanda = document.toObject(ArticulosComanda::class.java)
                    articulosComandaList.add(articuloComanda)
                }
                callback(articulosComandaList)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
    }

    /**
     * Obtiene un artículo de Firestore por su ID.
     *
     * @param articuloId ID del artículo a obtener.
     * @param callback Función de retorno que proporciona el objeto Articulo si se encuentra.
     */
    fun getArticuloById(articuloId: String, callback: (Articulo?) -> Unit) {
        firestore.collection("articulos").document(articuloId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val articulo = document.toObject(Articulo::class.java)
                    callback(articulo)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

    /**
     * Obtiene un artículo de Firestore por su ID de comanda y su ID de artículo.
     *
     * @param idComanda ID de la comanda asociada al artículo.
     * @param idArticulo ID del artículo.
     * @param callback Función de retorno que proporciona el objeto ArticulosComanda si se encuentra.
     */
    fun getArticuloComandaByComandaAndArticulo(idComanda: String, idArticulo: String, callback: (ArticulosComanda?) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .whereEqualTo("idArticulo.articuloId", idArticulo)
            .get()
            .addOnSuccessListener { result ->
                val articuloComanda = result.documents.firstOrNull()?.toObject(ArticulosComanda::class.java)
                callback(articuloComanda)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

    /**
     * Actualiza la cantidad de un artículo en una comanda específica en Firestore.
     *
     * @param idComanda ID de la comanda asociada al artículo.
     * @param idArticulo ID del artículo.
     * @param nuevaCantidad Nueva cantidad del artículo en la comanda.
     * @param callback Función de retorno que indica si la operación fue exitosa.
     */
    fun updateArticuloComanda(idComanda: String, idArticulo: String, nuevaCantidad: Int, callback: (Boolean) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .whereEqualTo("idArticulo.articuloId", idArticulo)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                if (document != null) {
                    document.reference.update("cantidad", nuevaCantidad)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }

    /**
     * Elimina un artículo de una comanda específica en Firestore.
     *
     * @param idComanda ID de la comanda asociada al artículo.
     * @param idArticulo ID del artículo.
     * @param callback Función de retorno que indica si la operación fue exitosa.
     */
    fun deleteArticuloComanda(idComanda: String, idArticulo: String, callback: (Boolean) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .whereEqualTo("idArticulo.articuloId", idArticulo)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                if (document != null) {
                    document.reference.delete()
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }

    /**
     * Actualiza el estado de pago de una comanda en Firestore.
     *
     * @param idComanda ID de la comanda a actualizar.
     * @param pagado Nuevo estado de pago de la comanda.
     * @param callback Función de retorno que indica si la operación fue exitosa.
     */
    fun updateComandaPagado(idComanda: String, pagado: Boolean, totalComanda: Double, callback: (Boolean) -> Unit) {
        val comandaRef = firestore.collection("comandas").document(idComanda)
        comandaRef.update("pagada", pagado, "totalComanda", totalComanda)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false)
            }
    }

    /**
     * Busca usuarios por rol y nombre en Firestore.
     *
     * @param rol Rol del usuario a buscar.
     * @param nombre Nombre del usuario o parte del nombre.
     * @param callback Función de retorno que recibe la lista de usuarios encontrados.
     */
    fun buscarUsuariosPorRolYNombre(rol: String, nombre: String, callback: (List<Usuario>) -> Unit) {
        firestore.collection("usuarios")
            .whereEqualTo("rol", rol)
            .whereGreaterThanOrEqualTo("nombre", nombre)
            .whereLessThan("nombre", nombre + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val usuarios: MutableList<Usuario> = mutableListOf()
                for (document in result) {
                    val usuario = document.toObject(Usuario::class.java)
                    usuarios.add(usuario)
                }
                callback(usuarios)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al leer datos de Firestore: $exception")
                callback(emptyList())
            }
    }

    fun actualizarStockPorComanda(idComanda: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        getArticulosComandaByComanda(idComanda) { articulosComandaList ->
            if (articulosComandaList.isEmpty()) {
                onFailure(Exception("No se encontraron artículos para la comanda $idComanda"))
                return@getArticulosComandaByComanda
            }

            for (articuloComanda in articulosComandaList) {
                val articuloId = articuloComanda.idArticulo.articuloId
                obtenerArticuloPorId(articuloId, { articulo ->
                    if (articulo != null) {
                        val nuevoStock = articulo.stock - articuloComanda.cantidad
                        if (nuevoStock >= 0) {
                            articulo.stock = nuevoStock
                            actualizarArticulo(articuloId, articulo, {
                                Log.i("ACTUALIZARSTOCK", "Stock actualizado correctamente para artículo $articuloId")
                            }, {
                                Log.e("ACTUALIZARSTOCK", "Error actualizando stock para artículo $articuloId")
                                onFailure(Exception("Error actualizando stock para artículo $articuloId"))
                            })
                        } else {
                            onFailure(Exception("Stock insuficiente para el artículo: ${articulo.nombre}"))
                            return@obtenerArticuloPorId
                        }
                    } else {
                        onFailure(Exception("No se encontró el artículo con ID $articuloId"))
                        return@obtenerArticuloPorId
                    }
                }, { error ->
                    onFailure(error)
                    return@obtenerArticuloPorId
                })
            }

            // Todos los artículos procesados correctamente
            onSuccess()
        }
    }
}