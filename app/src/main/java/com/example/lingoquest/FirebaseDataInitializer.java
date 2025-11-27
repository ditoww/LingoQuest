package com.example.lingoquest;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDataInitializer {
    private static final String TAG = "FirebaseInit";
    private FirebaseFirestore db;

    public FirebaseDataInitializer() {
        db = FirebaseFirestore.getInstance();
    }

    public void initializeLanguages() {
        String[][] languages = {
                {"Bahasa Inggris", "@drawable/inggris"},
                {"Bahasa Jepang", "@drawable/jepang"},
                {"Bahasa Korea", "@drawable/korea"},
                {"Bahasa Mandarin", "@drawable/mandarin"}
        };

        for (String[] lang : languages) {
            Map<String, Object> language = new HashMap<>();
            language.put("language_name", lang[0]);
            language.put("icon_url", lang[1]);

            db.collection("languages")
                    .add(language)
                    .addOnSuccessListener(docRef ->
                            Log.d(TAG, "Language added: " + lang[0] + " with ID: " + docRef.getId()))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Error adding language: " + lang[0], e));
        }
    }

    public void initializeQuestions() {
        // Bahasa Inggris Questions
        addEnglishQuestions();

        // Bahasa Jepang Questions
        addJapaneseQuestions();

        // Bahasa Mandarin Questions
        addMandarinQuestions();

        // Bahasa Korea Questions
        addKoreanQuestions();
    }

    private void addEnglishQuestions() {
        Object[][] questions = {
                {1, "Apa arti kata \"pintu\"?", "Window", "Table", "Door", "Roof", "Door", 10, "@drawable/soal_pintu"},
                {2, "Apa arti kata \"anjing\"?", "Dog", "Cat", "Bird", "Fish", "Dog", 10, "@drawable/soal_anjing"},
                {3, "Apa arti kata \"kursi\"?", "Chair", "Bed", "Closet", "Desk", "Chair", 10, "@drawable/soal_kursi"},
                {4, "Apa arti kata \"buku\"?", "Pencil", "Book", "Bag", "Paper", "Book", 10, "@drawable/soal_buku"},
                {5, "Apa arti kata \"langit\"?", "Ground", "Sun", "Sky", "Moon", "Sky", 10, "@drawable/soal_langit"},
                {6, "Apa arti kata \"rumah\"?", "House", "Car", "Shop", "Fence", "House", 10, "@drawable/soal_rumah"},
                {7, "Apa arti kata \"apel\"?", "Orange", "Banana", "Apple", "Mango", "Apple", 10, "@drawable/soal_apple"},
                {8, "Apa arti kata \"sekolah\"?", "Class", "School", "Office", "Library", "School", 10, "@drawable/soal_sekolah"},
                {9, "Apa arti kata \"kucing\"?", "Fish", "Dog", "Cow", "Cat", "Cat", 10, "@drawable/soal_kucing"},
                {10, "Apa arti kata \"jalan\"?", "Road", "Wall", "Hill", "Tree", "Road", 10, "@drawable/soal_jalan"}
        };

        getLanguageIdAndAddQuestions("Bahasa Inggris", questions);
    }

    private void addJapaneseQuestions() {
        Object[][] questions = {
                {1, "Apa arti kata \"api\"?", "水 (Mizu)", "火 (Hi)", "木 (Ki)", "金 (Kin)", "火 (Hi)", 10, "@drawable/soal_api"},
                {1, "Apa arti kata \"gunung\"?", "山 (Yama)", "川 (Kawa)", "森 (Mori)", "空 (Sora)", "山 (Yama)", 10, "@drawable/soal_gunung"},
                {1, "Apa arti kata \"mobil\"?", "電車 (Densha)", "車 (Kuruma)", "自転車 (Jitensha)", "飛行機 (Hikouki)", "車 (Kuruma)", 10, "@drawable/soal_mobil"},
                {1, "Apa arti kata \"kucing\"?", "犬 (Inu)", "猫 (Neko)", "馬 (Uma)", "鳥 (Tori)", "猫 (Neko)", 10, "@drawable/soal_kucing"},
                {1, "Apa arti kata \"hujan\"?", "雪 (Yuki)", "風 (Kaze)", "雨 (Ame)", "空 (Sora)", "雨 (Ame)", 10, "@drawable/soal_hujan"},
                {1, "Apa arti kata \"matahari\"?", "月 (Tsuki)", "星 (Hoshi)", "太陽 (Taiyō)", "空 (Sora)", "太陽 (Taiyō)", 10, "@drawable/soal_matahari"},
                {1, "Apa arti kata \"bunga\"?", "草 (Kusa)", "木 (Ki)", "花 (Hana)", "葉 (Ha)", "花 (Hana)", 10, "@drawable/soal_bunga"},
                {1, "Apa arti kata \"air laut\"?", "湖 (Mizuumi)", "海 (Umi)", "川 (Kawa)", "雨 (Ame)", "海 (Umi)", 10, "@drawable/soal_air_laut"},
                {1, "Apa arti kata \"tangan\"?", "足 (Ashi)", "手 (Te)", "目 (Me)", "耳 (Mimi)", "手 (Te)", 10, "@drawable/soal_tangan"},
                {1, "Apa arti kata \"bulan (langit)\"?", "月 (Tsuki)", "太陽 (Taiyō)", "火 (Hi)", "花 (Hana)", "月 (Tsuki)", 10, "@drawable/soal_bulan"}
        };

        getLanguageIdAndAddQuestions("Bahasa Jepang", questions);
    }

    private void addMandarinQuestions() {
        Object[][] questions = {
                {1, "Apa arti kata \"matahari\"?", "月亮 (yuèliàng)", "星星 (xīngxing)", "太阳 (tàiyáng)", "火 (huǒ)", "太阳 (tàiyáng)", 10, "@drawable/soal_matahari"},
                {1, "Apa arti kata \"air\"?", "水 (shuǐ)", "火 (huǒ)", "风 (fēng)", "云 (yún)", "水 (shuǐ)", 10, "@drawable/soal_air"},
                {1, "Apa arti kata \"pohon\"?", "森林 (sēnlín)", "树 (shù)", "草 (cǎo)", "花 (huā)", "树 (shù)", 10, "@drawable/soal_pohon"},
                {1, "Apa arti kata \"burung\"?", "鸡 (jī)", "鸭 (yā)", "鸟 (niǎo)", "虎 (hǔ)", "鸟 (niǎo)", 10, "@drawable/soal_burung"},
                {1, "Apa arti kata \"kucing\"?", "狗 (gǒu)", "狼 (láng)", "猫 (māo)", "熊 (xióng)", "猫 (māo)", 10, "@drawable/soal_kucing"},
                {1, "Apa arti kata \"rumah\"?", "家 (jiā)", "学校 (xuéxiào)", "商店 (shāngdiàn)", "医院 (yīyuàn)", "家 (jiā)", 10, "@drawable/soal_rumah"},
                {1, "Apa arti kata \"bunga\"?", "草 (cǎo)", "树 (shù)", "花 (huā)", "木 (mù)", "花 (huā)", 10, "@drawable/soal_bunga"},
                {1, "Apa arti kata \"telinga\"?", "鼻子 (bízi)", "眼睛 (yǎnjing)", "耳朵 (ěrduo)", "嘴巴 (zuǐba)", "耳朵 (ěrduo)", 10, "@drawable/soal_telinga"},
                {1, "Apa arti kata \"laut\"?", "湖 (hú)", "河 (hé)", "海 (hǎi)", "雨 (yǔ)", "海 (hǎi)", 10, "@drawable/soal_laut"},
                {1, "Apa arti kata \"jalan\"?", "路 (lù)", "门 (mén)", "桌子 (zhuōzi)", "椅子 (yǐzi)", "路 (lù)", 10, "@drawable/soal_jalan"}
        };

        getLanguageIdAndAddQuestions("Bahasa Mandarin", questions);
    }

    private void addKoreanQuestions() {
        Object[][] questions = {
                {1, "Apa arti kata \"air\"?", "불 (bul)", "물 (mul)", "바람 (baram)", "땅 (ttang)", "물 (mul)", 10, "@drawable/soal_air"},
                {1, "Apa arti kata \"mata\"?", "손 (son)", "코 (ko)", "눈 (nun)", "귀 (gwi)", "눈 (nun)", 10, "@drawable/soal_mata"},
                {1, "Apa arti kata \"bunga\"?", "꽃 (kkot)", "잎 (ip)", "나무 (namu)", "풀 (pul)", "꽃 (kkot)", 10, "@drawable/soal_bunga"},
                {1, "Apa arti kata \"anjing\"?", "고양이 (goyang-i)", "강아지 (gangaji)", "돼지 (dwaeji)", "곰 (gom)", "강아지 (gangaji)", 10, "@drawable/soal_anjing"},
                {1, "Apa arti kata \"mobil\"?", "비행기 (bihaenggi)", "자전거 (jajeongeo)", "자동차 (jadongcha)", "배 (bae)", "자동차 (jadongcha)", 10, "@drawable/soal_mobil"},
                {1, "Apa arti kata \"rumah\"?", "집 (jip)", "학교 (hakgyo)", "문 (mun)", "방 (bang)", "집 (jip)", 10, "@drawable/soal_rumah"},
                {1, "Apa arti kata \"bulan\"?", "태양 (taeyang)", "별 (byeol)", "달 (dal)", "하늘 (haneul)", "달 (dal)", 10, "@drawable/soal_bulan"},
                {1, "Apa arti kata \"jalan\"?", "길 (gil)", "집 (jip)", "공원 (gongwon)", "나무 (namu)", "길 (gil)", 10, "@drawable/soal_jalan"},
                {1, "Apa arti kata \"matahari\"?", "별 (byeol)", "달 (dal)", "태양 (taeyang)", "구름 (gureum)", "태양 (taeyang)", 10, "@drawable/soal_matahari"},
                {1, "Apa arti kata \"kursi\"?", "탁자 (takja)", "의자 (uija)", "문 (mun)", "책 (chaek)", "의자 (uija)", 10, "@drawable/soal_kursi"}
        };

        getLanguageIdAndAddQuestions("Bahasa Korea", questions);
    }

    private void getLanguageIdAndAddQuestions(String languageName, Object[][] questions) {
        db.collection("languages")
                .whereEqualTo("language_name", languageName)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String languageId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        for (Object[] q : questions) {
                            Map<String, Object> question = new HashMap<>();
                            question.put("language_id", languageId);
                            question.put("question_level", q[0]);
                            question.put("question_text", q[1]);
                            question.put("option_1", q[2]);
                            question.put("option_2", q[3]);
                            question.put("option_3", q[4]);
                            question.put("option_4", q[5]);
                            question.put("correct_answer", q[6]);
                            question.put("xp_reward", q[7]);
                            question.put("image_url", q[8]);

                            db.collection("game_questions")
                                    .add(question)
                                    .addOnSuccessListener(docRef ->
                                            Log.d(TAG, "Question added for " + languageName))
                                    .addOnFailureListener(e ->
                                            Log.e(TAG, "Error adding question", e));
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error getting language: " + languageName, e));
    }

    // ==================== LISTENING QUESTIONS ====================

    public void initializeListeningQuestions() {
        addEnglishListeningQuestions();
        addJapaneseListeningQuestions();
        addKoreanListeningQuestions();
        addMandarinListeningQuestions();
    }

    private void addEnglishListeningQuestions() {
        Object[][] questions = {
                {1, "What did you hear?", "audio_url_here", "Hello", "Goodbye", "Thank you", "Sorry", "Hello", 10},
                {1, "What greeting did you hear?", "audio_url_here", "Good morning", "Good night", "Good afternoon", "Good evening", "Good morning", 10},
                {1, "What number did you hear?", "audio_url_here", "One", "Two", "Three", "Four", "Two", 10},
                {1, "What color was mentioned?", "audio_url_here", "Red", "Blue", "Green", "Yellow", "Blue", 10},
                {1, "What animal did you hear?", "audio_url_here", "Dog", "Cat", "Bird", "Fish", "Cat", 10}
        };

        getLanguageIdAndAddListeningQuestions("Bahasa Inggris", questions);
    }

    private void addJapaneseListeningQuestions() {
        Object[][] questions = {
                {1, "日本語で何と言いましたか？", "audio_url_here", "こんにちは (Konnichiwa)", "さようなら (Sayounara)", "ありがとう (Arigatou)", "すみません (Sumimasen)", "こんにちは (Konnichiwa)", 10},
                {1, "何の数字を聞きましたか？", "audio_url_here", "一 (Ichi)", "二 (Ni)", "三 (San)", "四 (Shi)", "二 (Ni)", 10},
                {1, "何色と言いましたか？", "audio_url_here", "赤 (Aka)", "青 (Ao)", "緑 (Midori)", "黄色 (Kiiro)", "青 (Ao)", 10}
        };

        getLanguageIdAndAddListeningQuestions("Bahasa Jepang", questions);
    }

    private void addKoreanListeningQuestions() {
        Object[][] questions = {
                {1, "무엇을 들었습니까?", "audio_url_here", "안녕하세요 (Annyeonghaseyo)", "안녕히 가세요 (Annyeonghi gaseyo)", "감사합니다 (Gamsahamnida)", "미안합니다 (Mianhamnida)", "안녕하세요 (Annyeonghaseyo)", 10},
                {1, "어떤 숫자를 들었습니까?", "audio_url_here", "하나 (Hana)", "둘 (Dul)", "셋 (Set)", "넷 (Net)", "둘 (Dul)", 10}
        };

        getLanguageIdAndAddListeningQuestions("Bahasa Korea", questions);
    }

    private void addMandarinListeningQuestions() {
        Object[][] questions = {
                {1, "你听到了什么？", "audio_url_here", "你好 (Nǐ hǎo)", "再见 (Zàijiàn)", "谢谢 (Xièxie)", "对不起 (Duìbùqǐ)", "你好 (Nǐ hǎo)", 10},
                {1, "你听到了什么数字？", "audio_url_here", "一 (Yī)", "二 (Èr)", "三 (Sān)", "四 (Sì)", "二 (Èr)", 10}
        };

        getLanguageIdAndAddListeningQuestions("Bahasa Mandarin", questions);
    }

    private void getLanguageIdAndAddListeningQuestions(String languageName, Object[][] questions) {
        db.collection("languages")
                .whereEqualTo("language_name", languageName)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String languageId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        for (Object[] q : questions) {
                            Map<String, Object> question = new HashMap<>();
                            question.put("language_id", languageId);
                            question.put("question_level", q[0]);
                            question.put("question_text", q[1]);
                            question.put("audio_url", q[2]);
                            question.put("option_1", q[3]);
                            question.put("option_2", q[4]);
                            question.put("option_3", q[5]);
                            question.put("option_4", q[6]);
                            question.put("correct_answer", q[7]);
                            question.put("xp_reward", q[8]);

                            db.collection("listening_questions")
                                    .add(question)
                                    .addOnSuccessListener(docRef ->
                                            Log.d(TAG, "Listening question added for " + languageName))
                                    .addOnFailureListener(e ->
                                            Log.e(TAG, "Error adding listening question", e));
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error getting language: " + languageName, e));
    }

    // ==================== READING QUESTIONS ====================

    public void initializeReadingQuestions() {
        addEnglishReadingQuestions();
        addJapaneseReadingQuestions();
        addKoreanReadingQuestions();
        addMandarinReadingQuestions();
    }

    private void addEnglishReadingQuestions() {
        Object[][] questions = {
                {1, "My name is John Smith. I am 25 years old and I work as a software engineer at a technology company in Silicon Valley. " +
                        "Every morning, I wake up at 6:30 AM and go for a run in the park near my house. After exercising, I take a shower and have breakfast. " +
                        "I usually eat cereal with milk and drink a cup of coffee. Then I drive to work, which takes about 30 minutes. " +
                        "I love my job because I get to solve interesting problems and work with talented people from around the world.",
                        "What is John's profession?",
                        "A teacher", "A software engineer", "A doctor", "An accountant", "A software engineer", 15},

                {1, "Last summer, my family and I went on vacation to Hawaii. It was an amazing experience! " +
                        "We stayed at a beautiful hotel right on the beach. Every day, we would wake up early and watch the sunrise over the ocean. " +
                        "The water was crystal clear and warm. We went snorkeling and saw many colorful fish and even sea turtles. " +
                        "My favorite activity was surfing. At first, I kept falling off the board, but by the end of the week, I could stand up and ride small waves. " +
                        "We also tried traditional Hawaiian food like poke and shaved ice. It was the best vacation ever!",
                        "What was the writer's favorite activity?",
                        "Snorkeling", "Swimming", "Surfing", "Eating", "Surfing", 15},

                {1, "Climate change is one of the biggest challenges facing our planet today. " +
                        "Scientists have observed that global temperatures have been rising steadily over the past century. " +
                        "This warming is primarily caused by greenhouse gases like carbon dioxide, which trap heat in the atmosphere. " +
                        "Human activities, such as burning fossil fuels and deforestation, have significantly increased these gases. " +
                        "The effects of climate change include melting ice caps, rising sea levels, and more frequent extreme weather events. " +
                        "To address this problem, countries around the world are working together to reduce emissions and develop renewable energy sources.",
                        "What is the main cause of rising global temperatures?",
                        "Natural disasters", "Greenhouse gases", "Ocean currents", "Solar radiation", "Greenhouse gases", 15},

                {1, "Emma has always loved books. When she was a child, her mother would read her bedtime stories every night. " +
                        "As she grew older, she started reading on her own and discovered a passion for mystery novels. " +
                        "Now, at age 30, Emma owns a small bookstore in downtown Boston. The store has tall wooden shelves filled with thousands of books. " +
                        "She especially loves recommending books to customers and helping them find their next favorite story. " +
                        "On weekends, Emma organizes book club meetings where people gather to discuss what they've been reading. " +
                        "She believes that books have the power to connect people and open minds to new ideas.",
                        "What does Emma do on weekends?",
                        "Reads alone", "Writes books", "Organizes book club meetings", "Goes shopping", "Organizes book club meetings", 15},

                {1, "The invention of the internet has revolutionized how we communicate, work, and access information. " +
                        "In the early 1990s, only a small number of people used the internet, primarily in universities and research institutions. " +
                        "Today, billions of people around the world are connected online. We use the internet for everything: sending emails, " +
                        "shopping, watching videos, learning new skills, and staying in touch with friends and family. " +
                        "Social media platforms have made it possible to share our thoughts and experiences instantly with people across the globe. " +
                        "However, the internet also presents challenges, such as privacy concerns and the spread of misinformation. " +
                        "Despite these issues, there's no doubt that the internet has fundamentally changed modern society.",
                        "When did only a small number of people use the internet?",
                        "In the 1980s", "In the early 1990s", "In the 2000s", "In the 1970s", "In the early 1990s", 15}
        };

        getLanguageIdAndAddReadingQuestions("Bahasa Inggris", questions);
    }

    private void addJapaneseReadingQuestions() {
        Object[][] questions = {
                {1, "私の名前は田中さくらです。東京の大学で英語を勉強している学生です。" +
                        "毎日朝7時に起きて、朝ごはんを食べます。朝ごはんはいつもご飯と味噌汁と焼き魚です。" +
                        "その後、電車で大学に行きます。電車の中では本を読んだり、音楽を聞いたりします。" +
                        "授業は9時から始まって、午後4時に終わります。授業が終わったら、図書館で勉強します。" +
                        "夜は友達とご飯を食べて、家に帰って寝ます。週末は買い物に行ったり、映画を見たりします。",
                        "田中さんは毎日何時に起きますか？",
                        "6時", "7時", "8時", "9時", "7時", 15},

                {1, "日本には四つの季節があります。春、夏、秋、冬です。" +
                        "春には桜の花が咲きます。日本人は桜の下でお花見をして楽しみます。天気が暖かくなって、とても気持ちがいいです。" +
                        "夏はとても暑くて湿度が高いです。海や山に行く人が多いです。夏祭りもたくさんあります。" +
                        "秋は涼しくて過ごしやすい季節です。紅葉がとてもきれいです。食べ物もおいしいです。" +
                        "冬は寒いですが、雪が降ってきれいです。スキーやスノーボードをする人が多いです。",
                        "日本人は春に何をしますか？",
                        "海に行く", "お花見をする", "スキーをする", "紅葉を見る", "お花見をする", 15},

                {1, "昨日、私は友達と京都に行きました。京都は日本の古い都市で、たくさんのお寺や神社があります。" +
                        "まず、金閣寺に行きました。金色に輝くお寺はとても美しかったです。" +
                        "次に、清水寺に行きました。高い場所にあるので、京都の街がよく見えました。" +
                        "お昼は伝統的な京料理を食べました。とてもおいしかったです。" +
                        "午後は着物を着て、街を歩きました。外国人の観光客もたくさんいました。" +
                        "夕方、嵐山に行って、竹林を見ました。とても静かで美しい場所でした。",
                        "最初にどこに行きましたか？",
                        "清水寺", "金閣寺", "嵐山", "竹林", "金閣寺", 15}
        };

        getLanguageIdAndAddReadingQuestions("Bahasa Jepang", questions);
    }

    private void addKoreanReadingQuestions() {
        Object[][] questions = {
                {1, "저는 김민수입니다. 서울에 사는 대학생입니다. " +
                        "매일 아침 7시에 일어나서 운동을 합니다. 운동 후에 샤워를 하고 아침을 먹습니다. " +
                        "아침은 주로 김치찌개와 밥을 먹습니다. 한국 음식은 정말 맛있습니다. " +
                        "그 다음에 지하철을 타고 학교에 갑니다. 지하철에서 책을 읽거나 음악을 듣습니다. " +
                        "수업은 9시에 시작해서 오후 5시에 끝납니다. 수업이 끝나면 친구들과 카페에 가서 커피를 마십니다. " +
                        "저녁에는 도서관에서 공부하고 밤 11시쯤 집에 갑니다.",
                        "김민수는 아침에 무엇을 먹습니까?",
                        "빵과 우유", "김치찌개와 밥", "라면", "과일", "김치찌개와 밥", 15},

                {1, "한국의 가을은 정말 아름답습니다. 9월부터 11월까지가 가을입니다. " +
                        "가을에는 날씨가 시원하고 하늘이 맑습니다. 산의 나무들이 빨강색, 노랑색, 주황색으로 변합니다. " +
                        "많은 사람들이 산에 등산을 갑니다. 등산을 하면서 아름다운 단풍을 볼 수 있습니다. " +
                        "가을에는 맛있는 음식도 많습니다. 밤, 대추, 감 등이 있습니다. " +
                        "또한 추석이라는 명절이 있습니다. 추석에는 가족들이 모여서 송편을 먹고 즐거운 시간을 보냅니다.",
                        "가을에 많은 사람들이 무엇을 합니까?",
                        "수영", "스키", "등산", "낚시", "등산", 15},

                {1, "지난 주말에 저는 부산에 갔습니다. 부산은 한국의 남쪽에 있는 큰 도시입니다. " +
                        "바다가 있어서 경치가 정말 아름답습니다. 저는 KTX 기차를 타고 갔는데, 2시간 30분 걸렸습니다. " +
                        "부산에 도착해서 먼저 해운대 해변에 갔습니다. 모래가 하얗고 물이 깨끗했습니다. " +
                        "점심으로 신선한 회를 먹었습니다. 정말 맛있었습니다. " +
                        "오후에는 감천문화마을에 갔습니다. 알록달록한 집들이 언덕에 있어서 사진을 많이 찍었습니다. " +
                        "저녁에는 자갈치 시장에서 해산물을 먹고, 밤에 광안대교의 야경을 봤습니다. 정말 멋있었습니다!",
                        "부산에 어떻게 갔습니까?",
                        "비행기", "버스", "KTX 기차", "자동차", "KTX 기차", 15}
        };

        getLanguageIdAndAddReadingQuestions("Bahasa Korea", questions);
    }

    private void addMandarinReadingQuestions() {
        Object[][] questions = {
                {1, "我叫李明，是一名大学生。我在北京学习中文。" +
                        "每天早上七点起床，然后去食堂吃早饭。我喜欢吃包子和喝豆浆。" +
                        "吃完早饭后，我去图书馆学习。我的中文课从上午九点开始，到下午四点结束。" +
                        "课后，我常常和朋友们一起打篮球或者去咖啡馆喝咖啡。" +
                        "晚上，我在宿舍做作业和复习功课。周末的时候，我喜欢去公园散步或者看电影。" +
                        "我很喜欢在北京的生活，这里有很多有趣的地方可以参观。",
                        "李明每天早上吃什么？",
                        "面条", "包子和豆浆", "米饭", "饺子", "包子和豆浆", 15},

                {1, "中国有很多传统节日，其中最重要的是春节。春节通常在一月或二月。" +
                        "春节之前，人们会打扫房子，买新衣服，准备很多好吃的食物。" +
                        "除夕那天，全家人会一起吃团圆饭。饭菜非常丰盛，有鱼、肉、蔬菜等等。" +
                        "吃完饭后，大家会一起看春节联欢晚会。到了午夜，人们会放烟花和鞭炮。" +
                        "春节期间，孩子们会收到红包，里面有钱。人们也会去拜访亲戚和朋友，互相说'新年快乐'。" +
                        "春节假期通常有七天，是中国最长的假期。",
                        "春节通常在什么时候？",
                        "三月或四月", "一月或二月", "五月或六月", "十月或十一月", "一月或二月", 15},

                {1, "上个月，我去了中国的长城。长城是世界上最长的墙，有6000多公里长。" +
                        "我去的是八达岭长城，这是最有名的一段。那天天气很好，阳光明媚。" +
                        "爬长城很累，但是风景非常美。从长城上可以看到连绵的山脉和茂密的树林。" +
                        "我拍了很多照片。导游告诉我们，长城有2000多年的历史了。" +
                        "古代的人们建造长城是为了防御敌人的入侵。真不敢相信他们怎么能建造这么伟大的工程！" +
                        "爬到长城顶端后，我感到非常自豪。这次旅行让我更了解中国的历史和文化。",
                        "长城有多长？",
                        "3000多公里", "4000多公里", "5000多公里", "6000多公里", "6000多公里", 15}
        };

        getLanguageIdAndAddReadingQuestions("Bahasa Mandarin", questions);
    }

    private void getLanguageIdAndAddReadingQuestions(String languageName, Object[][] questions) {
        db.collection("languages")
                .whereEqualTo("language_name", languageName)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String languageId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        for (Object[] q : questions) {
                            Map<String, Object> question = new HashMap<>();
                            question.put("language_id", languageId);
                            question.put("question_level", q[0]);
                            question.put("reading_text", q[1]);
                            question.put("question_text", q[2]);
                            question.put("option_1", q[3]);
                            question.put("option_2", q[4]);
                            question.put("option_3", q[5]);
                            question.put("option_4", q[6]);
                            question.put("correct_answer", q[7]);
                            question.put("xp_reward", q[8]);

                            db.collection("reading_questions")
                                    .add(question)
                                    .addOnSuccessListener(docRef ->
                                            Log.d(TAG, "Reading question added for " + languageName))
                                    .addOnFailureListener(e ->
                                            Log.e(TAG, "Error adding reading question", e));
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error getting language: " + languageName, e));
    }

    // ==================== ADMIN ACCOUNT ====================

    public void initializeAdminAccount() {
        // Check if admin exists first
        db.collection("users")
                .whereEqualTo("email", "admin@lingoquest.com")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Create admin via FirebaseHelper
                        FirebaseHelper helper = FirebaseHelper.getInstance();
                        helper.registerUser("admin@lingoquest.com", "admin123", "admin",
                                null, new FirebaseHelper.AuthCallback() {
                                    @Override
                                    public void onSuccess(String userId) {
                                        // Update to admin
                                        db.collection("users").document(userId)
                                                .update("is_admin", true)
                                                .addOnSuccessListener(aVoid ->
                                                        Log.d(TAG, "Admin account created"))
                                                .addOnFailureListener(e ->
                                                        Log.e(TAG, "Error setting admin flag", e));
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Log.e(TAG, "Error creating admin: " + error);
                                    }
                                });
                    } else {
                        Log.d(TAG, "Admin account already exists");
                    }
                });
    }

    // ==================== INITIALIZE ALL DATA ====================

    public void initializeAllData() {
        initializeLanguages();

        // Wait a bit for languages to be created, then add questions
        new android.os.Handler().postDelayed(() -> {
            initializeQuestions();
            initializeListeningQuestions();
            initializeReadingQuestions();
        }, 2000);

        initializeAdminAccount();
    }
}