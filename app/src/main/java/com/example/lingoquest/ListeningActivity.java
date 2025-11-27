package com.example.lingoquest;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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
import java.util.Set;

public class ListeningActivity extends AppCompatActivity {

    private static final String TAG = "ListeningActivity";

    private TextView tvQuestionNumber;
    private TextView tvQuestionText;
    private RadioGroup radioGroupOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnPlayAudio, btnSubmit, btnNext;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private TextView tvTtsWarning;

    private TextToSpeech textToSpeech;
    private FirebaseHelper firebaseHelper;
    private String userId, languageId, languageName;
    private int currentLevel = 1;
    private int selectedLevel = 1;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalXp = 0;
    private int earnedXp = 0;

    private List<Map<String, Object>> questions;
    private boolean isTtsReady = false;
    private boolean isTtsSupported = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        firebaseHelper = FirebaseHelper.getInstance();
        userId = firebaseHelper.getCurrentUserId();
        languageName = getIntent().getStringExtra("language");
        selectedLevel = getIntent().getIntExtra("level", 1);

        Log.d(TAG, "Language: " + languageName + ", Level: " + selectedLevel);

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

                if (result == TextToSpeech.LANG_MISSING_DATA) {
                    Log.w(TAG, "TTS Language data missing for " + languageName);
                    isTtsReady = false;
                    isTtsSupported = false;
                    showTTSInstallDialog();
                } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "TTS Language not supported for " + languageName);
                    isTtsReady = false;
                    isTtsSupported = false;
                    showLanguageNotSupportedDialog();
                } else if (result == TextToSpeech.LANG_AVAILABLE ||
                        result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                        result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
                    Log.d(TAG, "TTS Ready for " + languageName);
                    isTtsReady = true;
                    isTtsSupported = true;
                    btnPlayAudio.setEnabled(true);

                    // Check available engines
                    checkAvailableVoices();
                }
            } else {
                Log.e(TAG, "TTS initialization failed");
                Toast.makeText(this, "Inisialisasi TTS gagal", Toast.LENGTH_SHORT).show();
                isTtsReady = false;
                isTtsSupported = false;
            }
        });
    }

    private void checkAvailableVoices() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Set<Locale> locales = textToSpeech.getAvailableLanguages();
            if (locales != null) {
                Log.d(TAG, "Available TTS languages: " + locales.size());
                for (Locale locale : locales) {
                    Log.d(TAG, "Available: " + locale.toString());
                }
            }
        }
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
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            default:
                locale = Locale.US;
        }

        Log.d(TAG, "Setting TTS locale to: " + locale.toString());
        int result = textToSpeech.setLanguage(locale);
        Log.d(TAG, "TTS setLanguage result: " + result);

        return result;
    }

    private void showTTSInstallDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Data TTS Tidak Tersedia")
                .setMessage("Data bahasa " + languageName + " untuk Text-to-Speech tidak tersedia. " +
                        "Anda perlu mengunduh data bahasa dari pengaturan TTS.\n\n" +
                        "Pergi ke: Pengaturan → Bahasa & Input → Text-to-Speech → " +
                        "Download bahasa yang diperlukan\n\n" +
                        "Anda masih dapat melanjutkan latihan tanpa audio.")
                .setPositiveButton("Lanjut Tanpa Audio", (dialog, which) -> {
                    btnPlayAudio.setEnabled(false);
                    btnPlayAudio.setText("Audio Tidak Tersedia");
                })
                .setNegativeButton("Kembali", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showLanguageNotSupportedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Bahasa Tidak Didukung")
                .setMessage("Perangkat Anda tidak mendukung Text-to-Speech untuk " + languageName + ".\n\n" +
                        "Untuk menggunakan fitur audio, Anda mungkin perlu:\n" +
                        "1. Menginstal aplikasi TTS tambahan dari Play Store (contoh: Google Text-to-Speech)\n" +
                        "2. Mengunduh data bahasa di pengaturan TTS\n\n" +
                        "Anda masih dapat melanjutkan latihan tanpa audio.")
                .setPositiveButton("Lanjut Tanpa Audio", (dialog, which) -> {
                    btnPlayAudio.setEnabled(false);
                    btnPlayAudio.setText("Audio Tidak Tersedia");
                })
                .setNegativeButton("Kembali", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
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
                earnedXp = 0;

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

        btnPlayAudio.setEnabled(isTtsReady && isTtsSupported);

        if (!isTtsSupported) {
            btnPlayAudio.setText("Audio Tidak Tersedia");
        } else {
            btnPlayAudio.setText("🔊 Putar Audio");
        }
    }

    private void playAudio() {
        if (!isTtsReady || !isTtsSupported) {
            Toast.makeText(this, "TTS tidak tersedia untuk bahasa ini", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> question = questions.get(currentQuestionIndex);
        String correctAnswer = (String) question.get("correct_answer");

        String textToSpeak = extractTextForSpeech(correctAnswer);

        btnPlayAudio.setEnabled(false);
        Toast.makeText(this, "🔊 Memutar audio...", Toast.LENGTH_SHORT).show();

        // Set speech rate and pitch based on language
        float speechRate = 0.7f; // Slower for Asian languages
        if (languageName.equals("Bahasa Inggris")) {
            speechRate = 0.8f;
        }

        textToSpeech.setSpeechRate(speechRate);
        textToSpeech.setPitch(1.0f);

        Log.d(TAG, "Speaking: " + textToSpeak);

        int speakResult = textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");

        if (speakResult == TextToSpeech.ERROR) {
            Log.e(TAG, "TTS speak error");
            Toast.makeText(this, "Error memutar audio", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(() -> {
            btnPlayAudio.setEnabled(true);
        }, 3000);
    }

    private String extractTextForSpeech(String text) {
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
            earnedXp += xp;

            selectedRadio.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(this, "✓ Benar! +" + xp + " XP", Toast.LENGTH_SHORT).show();
        } else {
            selectedRadio.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

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
        double percentage = (double) correctAnswers / questions.size();
        boolean passed = percentage >= 0.7;

        int newLevel = currentLevel;
        if (passed && selectedLevel == currentLevel) {
            newLevel = currentLevel + 1;
        }

        int newTotalXp = totalXp + earnedXp;

        firebaseHelper.updateUserListeningProgress(userId, languageId, newLevel, newTotalXp,
                new FirebaseHelper.XpCallback() {
                    @Override
                    public void onSuccess() {
                        firebaseHelper.recordXpGain(userId, earnedXp, null);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Error updating progress: " + error);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}