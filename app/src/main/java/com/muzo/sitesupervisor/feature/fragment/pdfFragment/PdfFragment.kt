package com.muzo.sitesupervisor.feature.fragment.pdfFragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.databinding.FragmentPdfBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfVersion
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.muzo.sitesupervisor.core.common.toastMessage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class PdfFragment : Fragment() {
    private lateinit var binding: FragmentPdfBinding
    private val viewModel: PdfFragmentViewModel by viewModels()
    private lateinit var list: List<DataModel>
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPdfBinding.inflate(layoutInflater, container, false)
        initViews()

        return binding.root
    }

    private fun initViews() {
        getSiteInfo()
        viewModel.getAllData(siteSupervisor, constructionArea)
        observeData()
        binding.btn.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }


    private fun observeData() {

        lifecycleScope.launch {
            viewModel.dataState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.resultList != null -> {
                        binding.progressBar.hide()
                        list = uiState.resultList
                    }
                }
            }
        }
    }

    private fun getSiteInfo() {
        lifecycleScope.launch {
            viewModel.readDataStore("construction_key").collect { area ->
                constructionArea = area!!
                viewModel.readDataStore("user_key").collect { supervisor ->
                    siteSupervisor = supervisor!!
                }
            }
        }
    }

    private fun generatePdfFromList(dataList: List<DataModel>) {
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val uniqueFileName = "pdf_${currentDate}_${System.currentTimeMillis()}.pdf"

        val pdfPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            uniqueFileName
        ).toString()

        // PDF dosyasını oluştur
        val writerProperties = WriterProperties().setPdfVersion(PdfVersion.PDF_1_7).setFullCompressionMode(true)
        val writer = PdfWriter(pdfPath, writerProperties)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // Font tanımla (örneğin Arial)
        val font = try {
            val fontPath = "res/font/arial.ttf" // Font dosyasının yolu
            PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        // DataModel listesinde dön ve PDF'e ekle
        for (dataModel in dataList) {
            val titleParagraph = Paragraph()
                .setFont(font)
                .add(Text("Başlık: ${dataModel.title}").setBold())

            val messageParagraph = Paragraph()
                .setFont(font)
                .add(Text("Açıklama: ${dataModel.message}"))

            val dayTimeParagraph = Paragraph()
                .setFont(font)
                .add(Text("Gün: ${dataModel.day}"))
                .add("\n")
                .add(Text("Saat: ${dataModel.time}"))
                .add("\n\n")

            document.add(titleParagraph)
            document.add(messageParagraph)
            document.add(dayTimeParagraph)
        }

        // PDF dosyasını kapat
        document.close()

        // Toast mesajını göster
        toastMessage("PDF indirildi: $uniqueFileName", requireContext())
    }


    private fun toastMessage(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Şantiye Çıktılarını Al")
        builder.setMessage("Şantiye çıktıları pdf olarak Dosyalarım/İndirilenler klasörüne dosya adı bugünün tarihi olucak şekilde eklenicektir.Onaylıyor musunuz?")
        builder.setPositiveButton("Evet") { dialog, _ ->
            generatePdfFromList(list)
            dialog.dismiss()
        }
        builder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}







