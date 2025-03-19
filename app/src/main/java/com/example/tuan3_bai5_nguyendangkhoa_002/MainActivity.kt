package com.example.tuan3_bai5_nguyendangkhoa_002

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import java.text.NumberFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val laiSuatMap = mapOf(
        "1 tháng" to 2.7,
        "3 tháng" to 2.8,
        "6 tháng" to 3.0,
        "12 tháng" to 3.5
    )

    private val kyHanList = listOf("1 tháng", "3 tháng", "6 tháng", "12 tháng")
    private val taiKhoanThuHuongList = listOf("19008010", "1234567890", "0987654321")
    private val taiTucList = listOf("Tái tục vốn lãi", "Tái tục vốn gốc", "Không tái tục")
    private val hinhThucList = listOf("Cuối kỳ", "Định kỳ hàng tháng")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taiKhoanNguonDisplay = findViewById<TextView>(R.id.tai_khoan_nguon_display)
        val soTienGui = findViewById<EditText>(R.id.so_tien_gui)
        val hinhThucNhanLai = findViewById<Spinner>(R.id.hinh_thuc_nhan_lai)
        val kyHan = findViewById<Spinner>(R.id.ky_han)
        val laiSuat = findViewById<Spinner>(R.id.lai_suat)
        val ngayMo = findViewById<CalendarView>(R.id.ngay_mo)
        val soDuTamTinh = findViewById<TextView>(R.id.so_du_tam_tinh_2)
        val hinhThucTaiTuc = findViewById<Spinner>(R.id.hinh_thuc_tai_tuc)
        val taiKhoanThuHuong = findViewById<Spinner>(R.id.tai_khoan_thu_huong)
        val moNhieuSo = findViewById<Switch>(R.id.mo_nhieu_so)
        val dongYDieuKhoan = findViewById<CheckBox>(R.id.dong_y_dieu_khoan)
        val btnTiepTuc = findViewById<Button>(R.id.btn_tiep_tuc)

        setSpinnerAdapter(hinhThucNhanLai, hinhThucList)
        setSpinnerAdapter(kyHan, kyHanList)
        setSpinnerAdapter(taiKhoanThuHuong, taiKhoanThuHuongList)
        setSpinnerAdapter(hinhThucTaiTuc, taiTucList)

        btnTiepTuc.isEnabled = false
        btnTiepTuc.alpha = 0.2f

        kyHan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedKyHan = kyHan.selectedItem.toString()
                val laiSuatForKyHan = laiSuatMap[selectedKyHan] ?: 0.0
                laiSuat.setSelection(0)
                updateLaiSuat(laiSuat, laiSuatForKyHan)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Lắng nghe sự thay đổi của checkbox "Đồng ý điều khoản"
        dongYDieuKhoan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Bật nút "Tiếp tục" khi checkbox được chọn
                btnTiepTuc.isEnabled = true
                btnTiepTuc.alpha = 1.0f
            } else {
                // Vô hiệu hóa nút "Tiếp tục" khi checkbox không được chọn
                btnTiepTuc.isEnabled = false
                btnTiepTuc.alpha = 0.2f
            }
        }

        btnTiepTuc.setOnClickListener {
            val soTien = soTienGui.text.toString().toDoubleOrNull()

            if (soTien == null || soTien <= 0) {
                Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kyHanSelected = kyHan.selectedItem.toString()
            val laiSuat = laiSuatMap[kyHanSelected] ?: 0.0
            val kyHanThang = getKyHanThang(kyHanSelected)

            val tongTien = tinhTongTien(soTien, laiSuat, kyHanThang)

            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            val formattedTongTien = currencyFormat.format(tongTien)

            soDuTamTinh.text = "$formattedTongTien"

            val taiKhoanThuHuongSelected = taiKhoanThuHuong.selectedItem.toString()

            Toast.makeText(this, "Sổ tiết kiệm của khách hàng $taiKhoanThuHuongSelected đã tạo thành công", Toast.LENGTH_LONG).show()
        }
    }

    private fun setSpinnerAdapter(spinner: Spinner, data: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, data)
        spinner.adapter = adapter
    }

    private fun updateLaiSuat(laiSuatSpinner: Spinner, laiSuatValue: Double) {
        val laiSuatList = listOf("$laiSuatValue %")
        val laiSuatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, laiSuatList)
        laiSuatSpinner.adapter = laiSuatAdapter
    }

    private fun getKyHanThang(kyHan: String): Int {
        return when (kyHan) {
            "1 tháng" -> 1
            "3 tháng" -> 3
            "6 tháng" -> 6
            "12 tháng" -> 12
            else -> 0
        }
    }

    private fun tinhTongTien(soTien: Double, laiSuat: Double, kyHanThang: Int): Double {
        val n = 12
        val t = kyHanThang / 12.0
        val laiSuatHangNam = laiSuat / 100
        val A = soTien * Math.pow(1 + laiSuatHangNam / n, n * t)
        return A
    }
}
