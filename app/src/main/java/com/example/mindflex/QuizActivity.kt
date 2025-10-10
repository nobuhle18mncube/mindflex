package com.example.mindflex

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import com.example.mindflex.quiz.OpenTdbApi
//import com.example.mindflex.quiz.OpenTdbQuestion
//import com.example.mindflex.quiz.OpenTdbResponse
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class QuizActivity : AppCompatActivity() {
    private val TAG = "QuizActivity"

    private lateinit var tvQuestionIndex: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var rgAnswers: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var tvDifficulty: TextView
    private lateinit var tvResult: TextView

    private var questions: List<OpenTdbQuestion> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private val userAnswers = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quiz_scroll)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvQuestionIndex = findViewById(R.id.tvQuestionIndex)
        tvQuestion = findViewById(R.id.tvQuestion)
        rgAnswers = findViewById(R.id.rgAnswers)
        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvResult = findViewById(R.id.tvResult)

        btnPrev.isEnabled = false

        btnNext.setOnClickListener {
            saveSelectedAnswer()
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion(currentIndex)
            } else {
                // last question -> finish and show score
                calculateScoreAndShow()
            }
            btnPrev.isEnabled = currentIndex > 0
        }

        btnPrev.setOnClickListener {
            saveSelectedAnswer()
            if (currentIndex > 0) {
                currentIndex--
                showQuestion(currentIndex)
            }
            btnPrev.isEnabled = currentIndex > 0
        }

        fetchQuestions()
    }

    private fun fetchQuestions() {
        // OpenTDB base URL
        val baseUrl = "https://opentdb.com/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        val api = retrofit.create(OpenTdbApi::class.java)
        api.fetchQuestions(10).enqueue(object : Callback<OpenTdbResponse> {
            override fun onResponse(call: Call<OpenTdbResponse>, response: Response<OpenTdbResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.results.isNotEmpty()) {
                        questions = body.results
                        currentIndex = 0
                        showQuestion(0)
                    } else {
                        showError("No questions returned")
                    }
                } else {
                    showError("HTTP ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OpenTdbResponse>, t: Throwable) {
                Log.e(TAG, "fetchQuestions failure", t)
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun showError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        tvQuestion.text = "Failed to load questions."
        rgAnswers.removeAllViews()
    }

    private fun showQuestion(index: Int) {
        val q = questions[index]
        tvQuestionIndex.text = "Question ${index + 1} / ${questions.size}"
        // OpenTDB returns HTML escaped strings — decode
        tvQuestion.text = Html.fromHtml(q.question, Html.FROM_HTML_MODE_LEGACY)
        tvDifficulty.text = "Difficulty: ${q.difficulty.capitalize(Locale.ROOT)}"

        // Build options list (correct + incorrect), shuffle
        val options = mutableListOf<String>()
        options.add(q.correct_answer)
        options.addAll(q.incorrect_answers)
        options.shuffle()

        // populate radio group
        rgAnswers.removeAllViews()
        options.forEachIndexed { i, opt ->
            val rb = RadioButton(this)
            rb.id = View.generateViewId()
            rb.text = Html.fromHtml(opt, Html.FROM_HTML_MODE_LEGACY)
            rb.layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            rgAnswers.addView(rb)
        }

        // restore previously selected answer if exists
        val prev = userAnswers[index]
        if (prev != null) {
            // find radio with that text and check
            for (i in 0 until rgAnswers.childCount) {
                val r = rgAnswers.getChildAt(i) as RadioButton
                if (r.text.toString() == Html.fromHtml(prev, Html.FROM_HTML_MODE_LEGACY).toString() ||
                    Html.fromHtml(prev, Html.FROM_HTML_MODE_LEGACY).toString() == r.text.toString()
                ) {
                    r.isChecked = true
                    break
                }
            }
        } else {
            rgAnswers.clearCheck()
        }

        // If last question change Next text to "Finish"
        btnNext.text = if (index == questions.size - 1) "Finish" else "Next"
    }

    private fun saveSelectedAnswer() {
        val checkedId = rgAnswers.checkedRadioButtonId
        if (checkedId != -1) {
            val rb = findViewById<RadioButton>(checkedId)
            val chosen = rb.text.toString()
            // store raw value (HTML encoded original from API may differ; we compare later using decoded strings)
            userAnswers[currentIndex] = chosen
        } else {
            // user didn't select — store empty or leave absent
            userAnswers.remove(currentIndex)
        }
    }

    private fun calculateScoreAndShow() {
        var correct = 0
        for ((idx, q) in questions.withIndex()) {
            val userVal = userAnswers[idx]
            if (!userVal.isNullOrEmpty()) {
                // decode both sides to plain for safe comparison
                val userPlain = Html.fromHtml(userVal, Html.FROM_HTML_MODE_LEGACY).toString()
                val correctPlain = Html.fromHtml(q.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()
                if (userPlain.trim().equals(correctPlain.trim(), ignoreCase = true)) correct++
            }
        }
        score = correct
        tvResult.visibility = View.VISIBLE
        tvResult.text = "You scored $score out of ${questions.size}"
        // Optionally disable controls
        btnNext.isEnabled = false
        btnPrev.isEnabled = false
        rgAnswers.isEnabled = false
    }
}