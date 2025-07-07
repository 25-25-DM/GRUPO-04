package com.ec.edu.uce.opendocument

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PdfViewerViewModel(application: Application) : AndroidViewModel(application) {

    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var currentPage: PdfRenderer.Page? = null

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex

    private val _pageCount = MutableStateFlow(0)
    val pageCount: StateFlow<Int> = _pageCount

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadPDF(uri: Uri) {
        viewModelScope.launch {
            try {
                currentPage?.close()
                pdfRenderer?.close()
                fileDescriptor?.close()

                val context = getApplication<Application>().applicationContext
                fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                fileDescriptor?.let {
                    pdfRenderer = PdfRenderer(it)
                    _pageCount.value = pdfRenderer!!.pageCount
                    showPage(0)
                }

            } catch (e: IOException) {
                e.printStackTrace()
                _errorMessage.value = "Error al cargar el PDF"
            }
        }
    }

    @SuppressLint("UseKtx")
    fun showPage(index: Int) {
        val renderer = pdfRenderer ?: return
        if (index < 0 || index >= renderer.pageCount) return

        currentPage?.close()
        currentPage = renderer.openPage(index)
        val page = currentPage ?: return

        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        _bitmap.value = bitmap
        _currentPageIndex.value = index
    }

    fun nextPage() = showPage(_currentPageIndex.value + 1)
    fun prevPage() = showPage(_currentPageIndex.value - 1)

    override fun onCleared() {
        super.onCleared()
        currentPage?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
    }
}