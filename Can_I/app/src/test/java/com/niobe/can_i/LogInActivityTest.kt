package com.niobe.can_i

import android.content.Intent
import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.usecases.login.LogInActivity
import com.niobe.can_i.util.Constants
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class LogInActivityTest {

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Mock
    private lateinit var mockDocument: DocumentSnapshot

    @Mock
    private lateinit var mockDocumentReference: DocumentReference

    private lateinit var logInActivity: LogInActivity

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        logInActivity = LogInActivity()
        logInActivity.auth = mockAuth
        logInActivity.db = mockDb
    }

    @Test
    fun iniciarSesion_conCredencialesValidas_iniciaSesionExitosamente() {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val mockTask = mock(Task::class.java) as Task<AuthResult>

        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)

        // Act
        logInActivity.iniciarSesion(email, password)

        // Assert
        verify(mockAuth).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun obtenerRolUsuario_conUidValido_usuarioAdmin() {
        // Arrange
        val uid = "testUid"
        val mockTask = Tasks.forResult(mockDocument)

        `when`(mockDb.collection("usuarios").document(uid)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.get()).thenReturn(mockTask)
        `when`(mockDocument.exists()).thenReturn(true)
        `when`(mockDocument.getString("rol")).thenReturn(Constants.TIPO_USUARIO_ADMINISTRADOR)

        // Act
        logInActivity.obtenerRolUsuario(uid)

        // Assert
        verify(mockDocumentReference).get()
        // Verifica la intención aquí si es necesario
    }

    @Test
    fun obtenerRolUsuario_conUidValido_usuarioCamarero() {
        // Arrange
        val uid = "testUid"
        val mockTask = Tasks.forResult(mockDocument)

        `when`(mockDb.collection("usuarios").document(uid)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.get()).thenReturn(mockTask)
        `when`(mockDocument.exists()).thenReturn(true)
        `when`(mockDocument.getString("rol")).thenReturn(Constants.TIPO_USUARIO_CAMARERO)

        // Act
        logInActivity.obtenerRolUsuario(uid)

        // Assert
        verify(mockDocumentReference).get()
        // Verifica la intención aquí si es necesario
    }

    @Test
    fun obtenerRolUsuario_conUidInvalido_muestraError() {
        // Arrange
        val uid = "testUid"
        val mockTask = Tasks.forResult(mockDocument)

        `when`(mockDb.collection("usuarios").document(uid)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.get()).thenReturn(mockTask)
        `when`(mockDocument.exists()).thenReturn(false)

        // Act
        logInActivity.obtenerRolUsuario(uid)

        // Assert
        verify(mockDocumentReference).get()
        // Verifica el mensaje Toast aquí si es necesario
    }
}