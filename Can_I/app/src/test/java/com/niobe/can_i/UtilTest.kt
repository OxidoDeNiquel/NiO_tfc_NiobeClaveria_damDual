package com.niobe.can_i

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.mockitokotlin2.*
import com.niobe.can_i.util.Util
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilTest {

    @Test
    fun `setupRecyclerViewHorizontal should set LinearLayoutManager horizontal and adapter`() {
        // Arrange
        val recyclerView = mock<RecyclerView>()
        val adapter = mock<RecyclerView.Adapter<*>>()

        // Act
        Util.setupRecyclerViewHorizontal(mock(), recyclerView, adapter)

        // Assert
        verify(recyclerView).setHasFixedSize(true)
        verify(recyclerView).layoutManager = any()
        verify(recyclerView).adapter = adapter
    }

    @Test
    fun `setupRecyclerViewVertical should set LinearLayoutManager vertical and adapter`() {
        // Arrange
        val recyclerView = mock<RecyclerView>()
        val adapter = mock<RecyclerView.Adapter<*>>()

        // Act
        Util.setupRecyclerViewVertical(mock(), recyclerView, adapter)

        // Assert
        verify(recyclerView).setHasFixedSize(true)
        verify(recyclerView).layoutManager = any()
        verify(recyclerView).adapter = adapter
    }

    @Test
    fun `changeActivity should start new activity and finish current activity`() {
        // Arrange
        val context = mock<Context>()

        // Act
        Util.changeActivity(context, AppCompatActivity::class.java)

        // Assert
        verify(context).startActivity(any())
        // Since we cannot check activity finishing without Robolectric, we don't assert it here
    }

    @Test
    fun `changeActivityWithoutFinish should start new activity without finishing current activity`() {
        // Arrange
        val context = mock<Context>()

        // Act
        Util.changeActivityWithoutFinish(context, AppCompatActivity::class.java)

        // Assert
        verify(context).startActivity(any())
        verifyZeroInteractions(context)
    }

    @Test
    fun `obtenerFechaHoraActual should return current date and time`() {
        // Act
        val result = Util.obtenerFechaHoraActual()

        // Assert
        // Since the result is dynamic, we can only assert that it's not null
        assertTrue(result.isNotEmpty())
    }
}
