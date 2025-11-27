package com.example.lingoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Map;

public class ListeningLevelActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView btnBack;
    private LinearLayout llLevelContainer;

    private FirebaseHelper firebaseHelper;
    private String userId, languageId, languageName;
    private int currentLevel = 1;
    private int totalXp = 0;

    private static final int MAX_LEVEL = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_level);

        firebaseHelper = FirebaseHelper.getInstance();
        userId = firebaseHelper.getCurrentUserId();
        languageName = getIntent().getStringExtra("language");

        initViews();
        setupListeners();
        loadLanguageAndProgress();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        llLevelContainer = findViewById(R.id.ll_level_container);

        tvTitle.setText("🎧 Listening - " + languageName);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadLanguageAndProgress() {
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

                createLevelButtons();
            }

            @Override
            public void onFailure(String error) {
                currentLevel = 1;
                totalXp = 0;
                createLevelButtons();
            }
        });
    }

    private void createLevelButtons() {
        llLevelContainer.removeAllViews();

        for (int level = 1; level <= MAX_LEVEL; level++) {
            CardView levelCard = createLevelCard(level);
            llLevelContainer.addView(levelCard);
        }
    }

    private CardView createLevelCard(int level) {
        // Create CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 24);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(8);
        cardView.setRadius(16);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.card_background));
        cardView.setContentPadding(24, 24, 24, 24);

        // Create inner LinearLayout
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Left side - Level info
        LinearLayout leftLayout = new LinearLayout(this);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        leftLayout.setLayoutParams(leftParams);
        leftLayout.setOrientation(LinearLayout.VERTICAL);

        // Level title
        TextView tvLevelTitle = new TextView(this);
        tvLevelTitle.setText("Level " + level);
        tvLevelTitle.setTextSize(18);
        tvLevelTitle.setTextColor(getResources().getColor(android.R.color.white));
        tvLevelTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        leftLayout.addView(tvLevelTitle);

        // Level status
        TextView tvLevelStatus = new TextView(this);
        String status;
        int statusColor;

        if (level < currentLevel) {
            status = "✓ Selesai";
            statusColor = getResources().getColor(android.R.color.holo_green_light);
        } else if (level == currentLevel) {
            status = "▶ Sedang Belajar";
            statusColor = getResources().getColor(android.R.color.holo_blue_light);
        } else {
            status = "🔒 Terkunci";
            statusColor = getResources().getColor(android.R.color.darker_gray);
        }

        tvLevelStatus.setText(status);
        tvLevelStatus.setTextSize(14);
        tvLevelStatus.setTextColor(statusColor);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(0, 8, 0, 0);
        tvLevelStatus.setLayoutParams(statusParams);
        leftLayout.addView(tvLevelStatus);

        // Progress bar for current level
        if (level == currentLevel) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    16
            );
            progressParams.setMargins(0, 12, 0, 0);
            progressBar.setLayoutParams(progressParams);
            progressBar.setMax(100);
            progressBar.setProgress(50); // You can calculate actual progress
            progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(android.R.color.holo_blue_light)));
            leftLayout.addView(progressBar);
        }

        innerLayout.addView(leftLayout);

        // Right side - Start button or lock icon
        if (level <= currentLevel) {
            // Create start button
            android.widget.Button btnStart = new android.widget.Button(this);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnStart.setLayoutParams(btnParams);
            btnStart.setText(level < currentLevel ? "Ulangi" : "Mulai");
            btnStart.setTextColor(getResources().getColor(android.R.color.white));
            btnStart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(android.R.color.holo_blue_dark)));
            btnStart.setOnClickListener(v -> startListeningGame(level));
            innerLayout.addView(btnStart);
        } else {
            // Show lock icon
            ImageView lockIcon = new ImageView(this);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
            lockIcon.setLayoutParams(iconParams);
            lockIcon.setImageResource(android.R.drawable.ic_lock_lock);
            lockIcon.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            innerLayout.addView(lockIcon);
        }

        cardView.addView(innerLayout);
        return cardView;
    }

    private void startListeningGame(int level) {
        Intent intent = new Intent(this, ListeningActivity.class);
        intent.putExtra("language", languageName);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload progress when returning from game
        if (languageId != null) {
            loadUserProgress();
        }
    }
}