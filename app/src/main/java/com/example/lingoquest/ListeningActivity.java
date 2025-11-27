package com.example.lingoquest;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListeningActivity extends AppCompatActivity {

    private TextView tvQuestionNumber;
    private TextView tvQuestionText;
    private RadioGroup radioGroupOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnPlayAudio, btnSubmit, btnNext;
    private ProgressBar progressBar;
    private ImageView btnBack;

    private TextToSpeech textToSpeech;
    private FirebaseHelper firebaseHelper;
    private String userId, languageId, languageName;
    private int currentLevel = 1;
    private int selectedLevel = 1; // Level yang dipilih user
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalXp = 0;
    private int earnedXp = 0; // XP yang didapat di sesi ini

    private List<Map<String, Object>> questions;
    private boolean isTtsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        firebaseHelper = FirebaseHelper.getInstance();
        userId = firebaseHelper.getCurrentUserId();
        languageName = getIntent().getStringExtra("language");
        selectedLevel = getIntent().getIntExtra("level", 1); // Ambil level dari intent

        initViews();
        setupListeners();
        initializeTTS();
        loadLanguageAndQuestions();
    }

    private void initViews() {
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvQuestionText = findViewById(R.id.tv_question_text);
        radioGroupOptions = findViewById(R.id.radio_group_options);
        rbOption1 = findViewById(R.id.rb_option1);
        rbOption2 = findViewById(R.id.rb_option2);
        rbOption3 = findViewById(R.id.rb_option3);
        rbOption4 = findViewById(R.id.rb_option4);
        btnPlayAudio = findViewById(R.id.btn_play_audio);
        btnSubmit = findViewById(R.id.btn_submit);
        btnNext = findViewById(R.id.btn_next);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initializeTTS() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = setTTSLanguage();
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Bahasa TTS tidak didukung", Toast.LENGTH_SHORT).show();
                    isTtsReady = false;
                } else {
                    isTtsReady = true;
                    btnPlayAudio.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "Inisialisasi TTS gagal", Toast.LENGTH_SHORT).show();
                isTtsReady = false;
            }
        });
    }

    private int setTTSLanguage() {
        Locale locale;
        switch (languageName) {
            case "Bahasa Inggris":
                locale = Locale.US;
                break;
            case "Bahasa Jepang":
                locale = Locale.JAPANESE;
                break;
            case "Bahasa Korea":
                locale = Locale.KOREAN;
                break;
            case "Bahasa Mandarin":
                locale = Locale.CHINESE;
                break;
            default:
                locale = Locale.US;
        }
        return textToSpeech.setLanguage(locale);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlayAudio.setOnClickListener(v -> playAudio());

        btnSubmit.setOnClickListener(v -> submitAnswer());

        btnNext.setOnClickListener(v -> loadNextQuestion());
    }

    private void loadLanguageAndQuestions() {
        firebaseHelper.getLanguageId(languageName, langId -> {
            if (langId != null) {
                languageId = langId;
                loadUserProgress();
            } else {
                Toast.makeText(this, "Bahasa tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadUserProgress() {
        firebaseHelper.getUserListeningProgress(userId, languageId, new FirebaseHelper.UserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                Long level = (Long) userData.get("current_level");
                Long xp = (Long) userData.get("total_xp");

                currentLevel = level != null ? level.intValue() : 1;
                totalXp = xp != null ? xp.intValue() : 0;

                loadQuestions();
            }

            @Override
            public void onFailure(String error) {
                currentLevel = 1;
                totalXp = 0;
                loadQuestions();
            }
        });
    }

    private void loadQuestions() {
        // Load questions based on selected level
        firebaseHelper.getListeningQuestions(languageId, selectedLevel, new FirebaseHelper.QuestionsCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> questionsList) {
                if (questionsList.isEmpty()) {
                    Toast.makeText(ListeningActivity.this,
                            "Belum ada soal untuk level ini", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                questions = questionsList;
                Collections.shuffle(questions);
                currentQuestionIndex = 0;
                correctAnswers = 0;
                earnedXp = 0; // Reset earned XP

                displayQuestion();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ListeningActivity.this,
                        "Gagal memuat soal: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showResultDialog();
            return;
        }

        Map<String, Object> question = questions.get(currentQuestionIndex);

        tvQuestionNumber.setText("Soal " + (currentQuestionIndex + 1) + "/" + questions.size());
        tvQuestionText.setText((String) question.get("question_text"));

        rbOption1.setText((String) question.get("option_1"));
        rbOption2.setText((String) question.get("option_2"));
        rbOption3.setText((String) question.get("option_3"));
        rbOption4.setText((String) question.get("option_4"));

        radioGroupOptions.clearCheck();
        btnSubmit.setEnabled(true);
        btnNext.setVisibility(View.GONE);

        progressBar.setMax(questions.size());
        progressBar.setProgress(currentQuestionIndex);

        btnPlayAudio.setEnabled(isTtsReady);
    }

    private void playAudio() {
        if (!isTtsReady) {
            Toast.makeText(this, "TTS belum siap", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> question = questions.get(currentQuestionIndex);
        String correctAnswer = (String) question.get("correct_answer");

        // Extract text from correct answer (remove text in parentheses if exists)
        String textToSpeak = extractTextForSpeech(correctAnswer);

        btnPlayAudio.setEnabled(false);
        Toast.makeText(this, "🔊 Memutar audio...", Toast.LENGTH_SHORT).show();

        // Set speech rate and pitch
        textToSpeech.setSpeechRate(0.8f); // Slower for learning
        textToSpeech.setPitch(1.0f);

        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);

        // Re-enable button after 3 seconds
        new Handler().postDelayed(() -> {
            btnPlayAudio.setEnabled(true);
        }, 3000);
    }

    private String extractTextForSpeech(String text) {
        // Remove text in parentheses for cleaner speech
        // Example: "こんにちは (Konnichiwa)" -> "こんにちは"
        if (text.contains("(")) {
            return text.substring(0, text.indexOf("(")).trim();
        }
        return text;
    }

    private void submitAnswer() {
        int selectedId = radioGroupOptions.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Pilih jawaban terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> question = questions.get(currentQuestionIndex);
        String correctAnswer = (String) question.get("correct_answer");

        RadioButton selectedRadio = findViewById(selectedId);
        String selectedAnswer = selectedRadio.getText().toString();

        if (selectedAnswer.equals(correctAnswer)) {
            correctAnswers++;
            Long xpReward = (Long) question.get("xp_reward");
            int xp = xpReward != null ? xpReward.intValue() : 10;
            earnedXp += xp; // Track earned XP in this session

            selectedRadio.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(this, "✓ Benar! +" + xp + " XP", Toast.LENGTH_SHORT).show();
        } else {
            selectedRadio.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            // Show correct answer
            if (rbOption1.getText().toString().equals(correctAnswer)) {
                rbOption1.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (rbOption2.getText().toString().equals(correctAnswer)) {
                rbOption2.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (rbOption3.getText().toString().equals(correctAnswer)) {
                rbOption3.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (rbOption4.getText().toString().equals(correctAnswer)) {
                rbOption4.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            Toast.makeText(this, "✗ Salah! Jawaban: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        btnSubmit.setEnabled(false);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void loadNextQuestion() {
        // Reset colors
        rbOption1.setTextColor(getResources().getColor(android.R.color.white));
        rbOption2.setTextColor(getResources().getColor(android.R.color.white));
        rbOption3.setTextColor(getResources().getColor(android.R.color.white));
        rbOption4.setTextColor(getResources().getColor(android.R.color.white));

        currentQuestionIndex++;

        if (currentQuestionIndex >= questions.size()) {
            showResultDialog();
        } else {
            displayQuestion();
        }
    }

    private void showResultDialog() {
        // Calculate if user passed (70% or more correct)
        double percentage = (double) correctAnswers / questions.size();
        boolean passed = percentage >= 0.7;

        // Update progress only if passed and it's the current level
        int newLevel = currentLevel;
        if (passed && selectedLevel == currentLevel) {
            newLevel = currentLevel + 1;
        }

        // Update total XP
        int newTotalXp = totalXp + earnedXp;

        firebaseHelper.updateUserListeningProgress(userId, languageId, newLevel, newTotalXp,
                new FirebaseHelper.XpCallback() {
                    @Override
                    public void onSuccess() {
                        // Record XP gain to user stats
                        firebaseHelper.recordXpGain(userId, earnedXp, null);
                    }

                    @Override
                    public void onFailure(String error) {
                        // Handle error
                    }
                });

        String message = "Skor: " + correctAnswers + "/" + questions.size() +
                " (" + String.format("%.0f", percentage * 100) + "%)\n" +
                "XP Didapat: +" + earnedXp + "\n";

        if (passed && selectedLevel == currentLevel) {
            message += "\n🎉 Selamat! Level naik ke " + newLevel + "!";
        } else if (!passed) {
            message += "\n⚠️ Anda perlu 70% untuk naik level. Coba lagi!";
        } else {
            message += "\n✓ Latihan selesai!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Hasil Listening")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Selamat!")
                .setMessage("Anda telah menyelesaikan semua level listening untuk " + languageName)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}