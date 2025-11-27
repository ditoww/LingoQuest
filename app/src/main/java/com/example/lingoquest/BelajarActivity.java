package com.example.lingoquest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.bumptech.glide.Glide;
import androidx.core.view.WindowCompat;
import android.view.WindowManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class BelajarActivity extends AppCompatActivity {

    private static final String TAG = "BelajarActivity";

    private ImageView ivAvatar;
    private TextView tvTimer;
    private Button btnStartChallenge;
    private NestedScrollView nestedScrollView;
    private Timer timer;
    private Handler handler = new Handler(Looper.getMainLooper());
    private long challengeEndTime;
    private FirebaseHelper firebaseHelper;
    private LinearLayout llLanguageList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");

        firebaseHelper = FirebaseHelper.getInstance();

        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_belajar);

        userId = firebaseHelper.getCurrentUserId();

        initViews();
        setupClickListeners();
        loadUserData();
        setChallengeTimer();
        loadLearnedLanguages();
        setupBottomNavigation();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.avatar);
        tvTimer = findViewById(R.id.timer);
        btnStartChallenge = findViewById(R.id.btn_start_challenge);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        llLanguageList = findViewById(R.id.language_list);
    }

    private void setupClickListeners() {
        // Language cards click listeners
        setupLanguageClickListeners();

        // Continue Learning & New Practice
        findViewById(R.id.layout_continue_learning).setOnClickListener(v -> showContinueLearningDialog());
        findViewById(R.id.layout_new_practice).setOnClickListener(v -> showNewPracticeDialog());

        // LISTENING & READING - INI YANG PENTING
        findViewById(R.id.layout_listening_practice).setOnClickListener(v -> {
            Log.d(TAG, "Listening clicked!");
            showListeningDialog();
        });

        findViewById(R.id.layout_reading_practice).setOnClickListener(v -> {
            Log.d(TAG, "Reading clicked!");
            showReadingDialog();
        });

        findViewById(R.id.see_more_languages).setOnClickListener(v -> {
            nestedScrollView.smoothScrollTo(0, llLanguageList.getBottom());
        });

        btnStartChallenge.setOnClickListener(v -> startChallenge());
    }

    private void setupLanguageClickListeners() {
        String[] languages = {"Bahasa Inggris", "Bahasa Jepang", "Bahasa Korea", "Bahasa Mandarin"};
        int[] layoutIds = {R.id.layout_english, R.id.layout_japanese, R.id.layout_korean, R.id.layout_mandarin};

        for (int i = 0; i < languages.length; i++) {
            final String languageName = languages[i];
            findViewById(layoutIds[i]).setOnClickListener(v -> startGameActivity(languageName));
        }
    }

    // ==================== LISTENING DIALOG ====================
    private void showListeningDialog() {
        Log.d(TAG, "showListeningDialog called");

        String[] languages = {"Bahasa Inggris", "Bahasa Jepang", "Bahasa Korea", "Bahasa Mandarin"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎧 Pilih Bahasa - Listening");
        builder.setMessage("Pilih bahasa untuk latihan mendengar");
        builder.setItems(languages, (dialog, which) -> {
            String selectedLanguage = languages[which];
            Log.d(TAG, "Language selected: " + selectedLanguage);

            // Langsung start activity
            try {
                Intent intent = new Intent(BelajarActivity.this, ListeningLevelActivity.class);
                intent.putExtra("language", selectedLanguage);
                Log.d(TAG, "Starting ListeningLevelActivity...");
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting ListeningLevelActivity", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Batal", (dialog, which) -> {
            Log.d(TAG, "Dialog cancelled");
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "Dialog shown");
    }

    // ==================== READING DIALOG ====================
    private void showReadingDialog() {
        Log.d(TAG, "showReadingDialog called");

        String[] languages = {"Bahasa Inggris", "Bahasa Jepang", "Bahasa Korea", "Bahasa Mandarin"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📖 Pilih Bahasa - Reading");
        builder.setMessage("Pilih bahasa untuk latihan membaca");
        builder.setItems(languages, (dialog, which) -> {
            String selectedLanguage = languages[which];
            Log.d(TAG, "Language selected: " + selectedLanguage);

            // Langsung start activity
            try {
                Intent intent = new Intent(BelajarActivity.this, ReadingLevelActivity.class);
                intent.putExtra("language", selectedLanguage);
                Log.d(TAG, "Starting ReadingLevelActivity...");
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting ReadingLevelActivity", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Batal", (dialog, which) -> {
            Log.d(TAG, "Dialog cancelled");
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "Dialog shown");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        loadLearnedLanguages();
    }

    private void loadUserData() {
        if (userId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        firebaseHelper.getUserData(userId, new FirebaseHelper.UserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                String avatarUrl = (String) userData.get("avatar_url");
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(BelajarActivity.this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(ivAvatar);
                } else {
                    ivAvatar.setImageResource(R.drawable.default_avatar);
                }
            }

            @Override
            public void onFailure(String error) {
                ivAvatar.setImageResource(R.drawable.default_avatar);
            }
        });
    }

    private void setChallengeTimer() {
        if (userId != null) {
            tvTimer.setText("⏰ Tidak ada tantangan aktif");
        }
    }

    private void loadLearnedLanguages() {
        if (userId == null) return;

        String[] languages = {"Bahasa Inggris", "Bahasa Jepang", "Bahasa Korea", "Bahasa Mandarin"};

        for (String languageName : languages) {
            firebaseHelper.getLanguageId(languageName, languageId -> {
                if (languageId != null) {
                    firebaseHelper.getUserGameProgress(userId, languageId, new FirebaseHelper.UserDataCallback() {
                        @Override
                        public void onSuccess(Map<String, Object> userData) {
                            Long levelLong = (Long) userData.get("current_level");
                            Long totalXpLong = (Long) userData.get("total_xp");

                            int level = levelLong != null ? levelLong.intValue() : 0;
                            int totalXp = totalXpLong != null ? totalXpLong.intValue() : 0;

                            int maxLevel = 10;
                            int progress = (level * 100) / maxLevel;

                            runOnUiThread(() -> updateLanguageUI(languageName, level, progress, totalXp));
                        }

                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> updateLanguageUI(languageName, 0, 0, 0));
                        }
                    });
                }
            });
        }
    }

    private void updateLanguageUI(String languageName, int level, int progress, int totalXp) {
        int levelTextId, progressBarId, progressTextId;

        switch (languageName) {
            case "Bahasa Inggris":
                levelTextId = R.id.level_english;
                progressBarId = R.id.progress_english;
                progressTextId = R.id.progress_text_english;
                break;
            case "Bahasa Jepang":
                levelTextId = R.id.level_japanese;
                progressBarId = R.id.progress_japanese;
                progressTextId = R.id.progress_text_japanese;
                break;
            case "Bahasa Korea":
                levelTextId = R.id.level_korean;
                progressBarId = R.id.progress_korean;
                progressTextId = R.id.progress_text_korean;
                break;
            case "Bahasa Mandarin":
                levelTextId = R.id.level_mandarin;
                progressBarId = R.id.progress_mandarin;
                progressTextId = R.id.progress_text_mandarin;
                break;
            default:
                return;
        }

        TextView tvLevel = findViewById(levelTextId);
        ProgressBar progressBar = findViewById(progressBarId);
        TextView tvProgress = findViewById(progressTextId);

        if (level == 0) {
            tvLevel.setText("Belum Mulai");
            progressBar.setProgress(0);
            tvProgress.setText("0 XP");
        } else {
            tvLevel.setText("Level " + level);
            progressBar.setProgress(progress);
            tvProgress.setText(totalXp + " XP");
        }
    }

    private void showContinueLearningDialog() {
        getLearnedLanguages(learnedLanguages -> {
            if (learnedLanguages.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Lanjutkan Belajar")
                        .setMessage("Anda belum mempelajari bahasa apa pun. Mulai latihan baru terlebih dahulu!")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            String[] languagesArray = learnedLanguages.toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setTitle("Lanjutkan Belajar")
                    .setItems(languagesArray, (dialog, which) -> {
                        String selectedLanguage = languagesArray[which];
                        startGameActivity(selectedLanguage);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void showNewPracticeDialog() {
        getUnlearnedLanguages(unlearnedLanguages -> {
            if (unlearnedLanguages.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Latihan Baru")
                        .setMessage("Anda sudah mempelajari semua bahasa yang tersedia!")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            String[] languagesArray = unlearnedLanguages.toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setTitle("Latihan Baru")
                    .setItems(languagesArray, (dialog, which) -> {
                        String selectedLanguage = languagesArray[which];
                        addLanguageToUser(selectedLanguage);
                        startGameActivity(selectedLanguage);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void getLearnedLanguages(LanguageListCallback callback) {
        List<String> learnedLanguages = new ArrayList<>();
        if (userId == null) {
            callback.onResult(learnedLanguages);
            return;
        }

        String[] allLanguages = {"Bahasa Inggris", "Bahasa Jepang", "Bahasa Korea", "Bahasa Mandarin"};
        final int[] checkCount = {0};

        for (String languageName : allLanguages) {
            firebaseHelper.getLanguageId(languageName, languageId -> {
                if (languageId != null) {
                    firebaseHelper.getUserGameProgress(userId, languageId, new FirebaseHelper.UserDataCallback() {
                        @Override
                        public void onSuccess(Map<String, Object> userData) {
                            learnedLanguages.add(languageName);
                            checkCount[0]++;
                            if (checkCount[0] == allLanguages.length) {
                                callback.onResult(learnedLanguages);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            checkCount[0]++;
                            if (checkCount[0] == allLanguages.length) {
                                callback.onResult(learnedLanguages);
                            }
                        }
                    });
                } else {
                    checkCount[0]++;
                    if (checkCount[0] == allLanguages.length) {
                        callback.onResult(learnedLanguages);
                    }
                }
            });
        }
    }

    private void getUnlearnedLanguages(LanguageListCallback callback) {
        getLearnedLanguages(learnedLanguages -> {
            List<String> allLanguages = List.of("Bahasa Inggris", "Bahasa Jepang", "Bahasa Korea", "Bahasa Mandarin");
            List<String> unlearnedLanguages = new ArrayList<>();

            for (String language : allLanguages) {
                if (!learnedLanguages.contains(language)) {
                    unlearnedLanguages.add(language);
                }
            }
            callback.onResult(unlearnedLanguages);
        });
    }

    interface LanguageListCallback {
        void onResult(List<String> languages);
    }

    private void addLanguageToUser(String languageName) {
        if (userId == null) return;

        firebaseHelper.getLanguageId(languageName, languageId -> {
            if (languageId != null) {
                firebaseHelper.updateUserGameProgress(userId, languageId, 1, 0, new FirebaseHelper.XpCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> loadLearnedLanguages());
                    }

                    @Override
                    public void onFailure(String error) {
                        // Handle error
                    }
                });
            }
        });
    }

    private void startGameActivity(String language) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("language", language);
        startActivity(intent);
    }

    private void startChallenge() {
        Intent intent = new Intent(this, TantanganActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_belajar);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            NavigationItem navItem = NavigationItem.fromItemId(item.getItemId());
            if (navItem == null) return false;
            switch (navItem) {
                case NAV_HOME:
                    startActivity(new Intent(BelajarActivity.this, MainActivity.class));
                    return true;
                case NAV_BELAJAR:
                    return true;
                case NAV_TANTANGAN:
                    startActivity(new Intent(BelajarActivity.this, TantanganActivity.class));
                    return true;
                case NAV_PERINGKAT:
                    startActivity(new Intent(BelajarActivity.this, PeringkatActivity.class));
                    return true;
                case NAV_PROFIL:
                    startActivity(new Intent(BelajarActivity.this, ProfilActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }

    private boolean isUserLoggedIn() {
        return firebaseHelper.isUserLoggedIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}