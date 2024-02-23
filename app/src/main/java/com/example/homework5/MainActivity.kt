package com.example.homework5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.homework5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var workings = "" // Kullanıcının girdiği matematiksel işlemin ifadesini tutar.
    private var isResultShown = false // Sonucun gösterilip gösterilmediğini takip eder.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Butonların ID'lerini bir listeye ekler.
        val buttonIds = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3, binding.button4,
            binding.button5, binding.button6, binding.button7, binding.button8, binding.button9,
            binding.buttonSum, binding.buttonMinus, binding.buttonTimes, binding.buttonDivide,
            binding.buttonDelete, binding.buttonClearAC, binding.buttonClearC, binding.buttonEqual,
            binding.buttonComma
        )
        // Butonlara tıklama dinleyicilerini ekler.
        for (buttonId in buttonIds) {
            buttonId.setOnClickListener {
                when (val buttonText = (it as Button).text.toString()) {
                    "=" -> evaluateExpression()
                    "C" -> clear()
                    "AC" -> clear()
                    "⌫" -> delete()
                    else -> appendText(buttonText)
                }
            }
        }
    }

    private fun delete(){
        // İşlem boş değilse, son karakteri siler.
        if (workings.isNotEmpty()) {
            workings = workings.dropLast(1)
            binding.textViewWorkings.text = workings
        }
    }
    private fun appendText(text: String) {
        // Sonuç gösteriliyorsa, yeni bir işlem başlatır.
        if (isResultShown) {
            workings = ""
            binding.textViewWorkings.text = ""
            isResultShown = false
        }
        // Virgül eklenmek isteniyorsa:
        if (text == ",") {
            // Eğer workings içinde virgül varsa, yeni bir virgül eklenmez.
            if (!workings.contains(",")) {
                // Son karakter bir operatör ise, virgül eklemeyiz.
                if (workings.isNotEmpty() && "+-×/".contains(workings.last())) {
                    workings += "0$text"
                } else {
                    workings += text
                }
            }
            // İşlem "0" ile başlıyorsa ve kullanıcı bir sayı ekliyorsa veya son karakter bir operatörse ve kullanıcı bir sayı ekliyorsa, sadece sayı eklenir.
        } else if (workings == "0" && (text.matches(Regex("[0-9]")) ||
                    text.matches(Regex("[-+×/]")))) {
            workings = text
        } else {
            // Son eklenen karakter bir operatörse ve workings'in son karakteri de bir operatörse, en son eklenen operatörü eklemeyiz.
            if (text.matches(Regex("[-+×/]")) && workings.isNotEmpty() &&
                workings.last().toString().matches(Regex("[-+×/]"))) {
                workings = workings.dropLast(1)
            }
            workings += text
        }
        binding.textViewWorkings.text = workings
    }


    private fun clear() {
        // İşlemi temizler, sonuçları varsayılan olarak ayarlar.
        workings = ""
        binding.textViewResult.text = "0"
        binding.textViewWorkings.text = "0"
    }

    private fun evaluateExpression() {
        try {
            // Eğer işlem boş değilse ve son karakter bir operatörse, son karakteri siler.
            if (workings.isNotEmpty() && "+-×/".contains(workings.last())) {
                workings = workings.dropLast(1)
                binding.textViewWorkings.text = workings
            }
            // İşlemi değerlendirir ve sonucu gösterir.
            val result = eval(workings)
            binding.textViewResult.text = result
            isResultShown = true
            workings = result

            // Eğer sonuç hata dönmüyorsa, sonucu işleme ekler.
            if (result != "Error") {
                workings = ""
                workings += result
                isResultShown = false
            }
        } catch (e: ArithmeticException) {
            // Hata oluşursa, hata mesajını gösterir.
            binding.textViewResult.text = "Error"
        }
    }

    private fun eval(expression: String): String {
        // İfadeyi temizler ve parçalarına ayırır.
        val cleanedExpression = expression.replace(',', '.')
        val parts = cleanedExpression.split(Regex("(?<=[-+×/])|(?=[-+×/])"))

        // İşlemleri gerçekleştirir ve sonucu döndürür.
        var result = parts[0].toDoubleOrNull()
        if (result != null) {
            for (i in 1 until parts.size step 2) {
                val operator = parts[i]
                val number = parts[i + 1].toDouble()

                if (result != null) {
                    result = when (operator) {
                        "+" -> result + number
                        "-" -> result - number
                        "×" -> result * number
                        "/" -> result / number
                        else -> throw IllegalArgumentException("Invalid operator")
                    }
                }
            }
            // Sonucu formatlayarak döndürür.
            return if (result == result?.toInt()?.toDouble()) {
                result?.toInt().toString()
            } else {
                String.format("%.2f", result)
            }
        } else {
            return "0"
        }
    }
}