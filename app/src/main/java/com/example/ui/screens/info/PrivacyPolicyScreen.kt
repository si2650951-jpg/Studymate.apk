package com.example.ui.screens.info

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudyMateBlue
import com.example.ui.theme.StudyMateGradient

data class PolicySection(
    val id: String,
    val icon: ImageVector,
    val titleEn: String,
    val titleHi: String,
    val summaryEn: String,
    val summaryHi: String,
    val contentEn: List<String>,
    val contentHi: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: (() -> Unit)? = null,
    onNavigateToSupport: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isHindi by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedAll by remember { mutableStateOf(false) }

    val policySections = remember {
        listOf(
            PolicySection(
                id = "overview",
                icon = Icons.Default.Shield,
                titleEn = "1. Introduction & App Scope",
                titleHi = "1. परिचय एवं ऐप का दायरा",
                summaryEn = "StudyMate is committed to protecting student privacy and educational records.",
                summaryHi = "StudyMate छात्रों की गोपनीयता और शैक्षणिक रिकॉर्ड की सुरक्षा के लिए पूरी तरह प्रतिबद्ध है।",
                contentEn = listOf(
                    "StudyMate is an educational productivity and study management application designed for students and educators to organize timetables, track homework, schedule exams, take notes, calculate attendance, and track study milestones.",
                    "We strongly respect your privacy. This Privacy Policy outlines what information we collect, how it is used, how it is secured, and your explicit rights regarding your personal academic data.",
                    "By using StudyMate, you acknowledge the terms described in this policy. If you do not agree with this policy, please do not use the application."
                ),
                contentHi = listOf(
                    "StudyMate एक शैक्षणिक प्रबंधन और उत्पादकता ऐप है जो छात्रों को समय सारिणी (Timetable), होमवर्क, परीक्षा, नोट्स और उपस्थिति प्रबंधित करने में मदद करता है।",
                    "हम आपकी निजता का पूरा सम्मान करते हैं। यह गोपनीयता नीति बताती है कि हम कौन सी जानकारी एकत्र करते हैं, उसका उपयोग कैसे करते हैं और आपका डेटा कैसे सुरक्षित रहता है।",
                    "StudyMate का उपयोग करके, आप इस नीति में वर्णित शर्तों को स्वीकार करते हैं।"
                )
            ),
            PolicySection(
                id = "data_collected",
                icon = Icons.Default.AccountBox,
                titleEn = "2. Information We Collect",
                titleHi = "2. हम कौन सी जानकारी एकत्र करते हैं",
                summaryEn = "Only essential profile details and user-entered academic records.",
                summaryHi = "केवल आवश्यक प्रोफ़ाइल विवरण और छात्र द्वारा दर्ज की गई पढ़ाई संबंधी सामग्री।",
                contentEn = listOf(
                    "Student Profile Data: When creating an account, we collect your Name, Email address, Phone number (optional), Institution / College name, and Class / Grade.",
                    "Academic Content: All study materials entered by you, including Timetable classes, Homework assignments, Exam dates & syllabus, Digital notes & tags, Attendance records, and Pomodoro study timer logs.",
                    "Device & Technical Data: Android OS version, app version, and anonymous crash logs strictly used to troubleshoot bugs and improve app stability.",
                    "No Invasive Tracking: We do NOT collect GPS location, contacts, SMS, call logs, browsing history, or biometric data."
                ),
                contentHi = listOf(
                    "छात्र प्रोफ़ाइल डेटा: खाता बनाते समय हम आपका नाम, ईमेल पता, मोबाइल नंबर (वैकल्पिक), स्कूल/कॉलेज का नाम और कक्षा एकत्र करते हैं।",
                    "शैक्षणिक सामग्री: आपके द्वारा ऐप में जोड़ी गई समय सारिणी, होमवर्क, परीक्षा विवरण, नोट्स और उपस्थिति का डेटा।",
                    "डिवाइस और तकनीकी डेटा: एंड्रॉइड ओएस संस्करण और ऐप क्रैश लॉग, जो केवल तकनीकी सुधार के लिए उपयोग किए जाते हैं।",
                    "कोई अनावश्यक ट्रैकिंग नहीं: हम आपका जीपीएस लोकेशन, संपर्क (Contacts), एसएमएस या ब्राउज़िंग इतिहास कभी एकत्र नहीं करते।"
                )
            ),
            PolicySection(
                id = "data_usage",
                icon = Icons.Default.Tune,
                titleEn = "3. How We Use Your Information",
                titleHi = "3. हम आपकी जानकारी का उपयोग कैसे करते हैं",
                summaryEn = "Used exclusively to deliver educational features and personalized study alerts.",
                summaryHi = "केवल पढ़ाई की सुविधाएं प्रदान करने और समय पर सूचनाएं देने के लिए उपयोग किया जाता है।",
                contentEn = listOf(
                    "Personalized Academic Schedule: To display your daily class schedule, upcoming assignment deadlines, and exam countdowns.",
                    "Attendance & Performance Metrics: To calculate your attendance percentages and provide alerts if your attendance falls below the target threshold.",
                    "Study Streaks & Milestones: To monitor your Pomodoro study sessions and celebrate your completed academic goals.",
                    "Local Push Reminders: To alert you before scheduled classes or upcoming homework deadlines.",
                    "Zero Advertising: We NEVER sell, rent, or trade student data with third-party advertisers or data brokers."
                ),
                contentHi = listOf(
                    "शैक्षणिक शेड्यूल: आपकी दैनिक कक्षाएं, होमवर्क की समय सीमा और परीक्षा की उल्टी गिनती दिखाना।",
                    "उपस्थिति प्रतिशत: आपकी उपस्थिति की गणना करना और लक्ष्य से कम होने पर चेतावनी देना।",
                    "पढ़ाई का समय: पोमोडोरो टाइमर सत्रों को रिकॉर्ड करना और पढ़ाई के लक्ष्यों की प्रगति दिखाना।",
                    "रिमाइंडर अलर्ट: कक्षाओं और असाइनमेंट के लिए समय पर सूचना भेजना।",
                    "शून्य विज्ञापन: हम छात्र डेटा को कभी भी किसी विज्ञापनदाता को नहीं बेचते।"
                )
            ),
            PolicySection(
                id = "permissions",
                icon = Icons.Default.LockClock,
                titleEn = "4. Device Permissions & Purpose",
                titleHi = "4. डिवाइस अनुमतियां और उनका उद्देश्य",
                summaryEn = "Minimal permissions requested strictly for notifications and background timers.",
                summaryHi = "केवल जरूरी अनुमतियां जैसे नोटिफिकेशन और टाइमर कंपन के लिए।",
                contentEn = listOf(
                    "POST_NOTIFICATIONS (Android 13+): Required only to deliver class reminders, homework alerts, and exam notifications. You can toggle individual notification categories in Settings.",
                    "INTERNET & ACCESS_NETWORK_STATE: Required to securely synchronize study notes and timetable items with Google Firebase cloud services.",
                    "VIBRATE: Used for tactile feedback when a Pomodoro focus or break timer finishes.",
                    "Photo / File Picker: Note image attachments use Android's zero-permission system Photo Picker. We do NOT request broad READ_EXTERNAL_STORAGE permissions."
                ),
                contentHi = listOf(
                    "नोटिफिकेशन अनुमति (POST_NOTIFICATIONS): कक्षा, होमवर्क और परीक्षा के समय पर रिमाइंडर भेजने के लिए। आप सेटिंग्स में इसे कभी भी बंद कर सकते हैं।",
                    "इंटरनेट (INTERNET): आपके नोट्स और टाइमटेबल को सुरक्षित रूप से क्लाउड पर सुरक्षित रखने के लिए।",
                    "कंपन (VIBRATE): पोमोडोरो स्टडी टाइमर पूरा होने पर अलर्ट देने के लिए।",
                    "फ़ोटो चयन: नोट्स में फ़ोटो जोड़ने के लिए सुरक्षित सिस्टम फोटो पिकर का उपयोग किया जाता है।"
                )
            ),
            PolicySection(
                id = "security",
                icon = Icons.Default.Security,
                titleEn = "5. Data Storage, Security & Cloud Sync",
                titleHi = "5. डेटा सुरक्षा और क्लाउड स्टोरेज",
                summaryEn = "Industry standard TLS 1.3 encryption and per-user Firebase security isolation.",
                summaryHi = "TLS एन्क्रिप्शन और उपयोगकर्ता-वार सुरक्षित क्लाउड स्टोरेज।",
                contentEn = listOf(
                    "Offline-First Storage: All study data is cached locally on your device in secure app-private storage (Room Database), allowing complete offline access.",
                    "Transport Encryption: All network communications with cloud servers are encrypted using TLS 1.3 / SSL protocols.",
                    "User-Level Security Isolation: Firebase Security Rules ensure that only your authenticated student account can access, edit, or delete your personal study data.",
                    "Role Isolation: Standard administrators and other students cannot read or inspect your private personal notes or homework submissions."
                ),
                contentHi = listOf(
                    "ऑफ़लाइन स्टोरेज: सभी नोट्स और टाइमटेबल आपके फ़ोन में सुरक्षित रहते हैं ताकि बिना इंटरनेट भी ऐप काम करे।",
                    "एन्क्रिप्शन: क्लाउड के साथ डेटा ट्रांसफर पूरी तरह एन्क्रिप्टेड (TLS 1.3) होता है।",
                    "डेटा अलगाव: आपके व्यक्तिगत नोट्स और होमवर्क केवल आपके खाते द्वारा ही देखे और बदले जा सकते हैं।"
                )
            ),
            PolicySection(
                id = "children",
                icon = Icons.Default.ChildCare,
                titleEn = "6. Children's & Student Privacy (COPPA / FERPA)",
                titleHi = "6. छात्र एवं बाल गोपनीयता सुरक्षा",
                summaryEn = "Designed with strict protections for minors and educational integrity.",
                summaryHi = "छात्रों और नाबालिगों के लिए सुरक्षित वातावरण, बिना किसी व्यावसायिक विज्ञापन के।",
                contentEn = listOf(
                    "StudyMate is designed as a safe academic utility for students in secondary school, high school, and university.",
                    "We do not knowingly collect personal information from children under 13 without appropriate parental or institutional guidance.",
                    "We do not display behavioral advertising, pop-ups, or age-inappropriate content.",
                    "If a parent or guardian discovers that their child has provided personal details without consent, they may contact us for immediate account and data deletion."
                ),
                contentHi = listOf(
                    "StudyMate स्कूल और कॉलेज के छात्रों के लिए एक सुरक्षित शैक्षणिक उपकरण है।",
                    "हम किसी भी प्रकार के व्यावसायिक विज्ञापन या अनुचित सामग्री नहीं दिखाते।",
                    "माता-पिता किसी भी समय अपने बच्चे के खाते और डेटा को हटाने का अनुरोध कर सकते हैं।"
                )
            ),
            PolicySection(
                id = "third_party",
                icon = Icons.Default.CloudSync,
                titleEn = "7. Third-Party Services",
                titleHi = "7. तृतीय पक्ष सेवाएं",
                summaryEn = "Powered by Google Firebase Authentication, Firestore, and Cloud Storage.",
                summaryHi = "गूगल फायरबेस (Google Firebase) की सुरक्षित सेवाओं द्वारा संचालित।",
                contentEn = listOf(
                    "We utilize Google Firebase services (Firebase Authentication, Cloud Firestore, Cloud Storage, Firebase Analytics/Crashlytics) for infrastructure and data sync.",
                    "Google processes data according to Google's Privacy Policy and high security standards: https://policies.google.com/privacy",
                    "We do not integrate any third-party ad networks, social media trackers, or analytics brokers."
                ),
                contentHi = listOf(
                    "हम डेटा प्रमाणीकरण और सुरक्षित क्लाउड बैकअप के लिए Google Firebase सेवाओं का उपयोग करते हैं।",
                    "गूगल अपने उच्च सुरक्षा मानकों के तहत डेटा प्रोसेस करता है: https://policies.google.com/privacy",
                    "हम किसी भी विज्ञापन नेटवर्क या सोशल मीडिया ट्रैकर का उपयोग नहीं करते।"
                )
            ),
            PolicySection(
                id = "rights_deletion",
                icon = Icons.Default.DeleteForever,
                titleEn = "8. Your Rights & Account Deletion",
                titleHi = "8. आपके अधिकार और डेटा हटाना",
                summaryEn = "Full right to export, modify, or permanently wipe your account and study data.",
                summaryHi = "अपने डेटा को देखने, बदलने या स्थायी रूप से हटाने का पूर्ण अधिकार।",
                contentEn = listOf(
                    "Right to Access & Rectify: You can review and update your name, email, institution, and study records directly from the Profile and Settings screens.",
                    "Right to Erasure (Delete Account): You can permanently delete your student account and all related timetable items, notes, homework, and attendance records directly within the app or by submitting a support request.",
                    "Data Retention: Once deleted, your account data is permanently expunged from active cloud databases and local storage immediately."
                ),
                contentHi = listOf(
                    "डेटा देखने और सुधारने का अधिकार: आप प्रोफ़ाइल स्क्रीन से कभी भी अपनी जानकारी अपडेट कर सकते हैं।",
                    "अकाउंट हटाने का अधिकार: आप किसी भी समय अपना खाता और सभी नोट्स, टाइमटेबल और उपस्थिति स्थायी रूप से हटा सकते हैं।",
                    "डेटा निष्कासन: खाता हटाने पर आपका सारा डेटा क्लाउड और फ़ोन दोनों से तुरंत मिटा दिया जाता है।"
                )
            ),
            PolicySection(
                id = "updates",
                icon = Icons.Default.Update,
                titleEn = "9. Policy Updates & Modifications",
                titleHi = "9. गोपनीयता नीति में संशोधन",
                summaryEn = "Periodic revisions to reflect app improvements and compliance updates.",
                summaryHi = "ऐप में सुधार या नए नियमों के अनुसार समय-समय पर अपडेट।",
                contentEn = listOf(
                    "We may update our Privacy Policy periodically to reflect new features or legal requirements.",
                    "Any material changes will be announced within the StudyMate app notifications or updated directly on this page with a revised 'Last Updated' date.",
                    "Continued use of StudyMate after modifications constitutes acceptance of the updated policy."
                ),
                contentHi = listOf(
                    "हम नए फीचर्स या कानूनी आवश्यकताओं के आधार पर इस नीति को समय-समय पर अपडेट कर सकते हैं।",
                    "किसी भी बड़े बदलाव की सूचना ऐप के माध्यम से दी जाएगी।"
                )
            ),
            PolicySection(
                id = "contact",
                icon = Icons.Default.ContactSupport,
                titleEn = "10. Contact Us & Grievance Officer",
                titleHi = "10. संपर्क एवं शिकायत अधिकारी",
                summaryEn = "Direct support for any questions regarding your privacy or data.",
                summaryHi = "डेटा और गोपनीयता संबंधी किसी भी प्रश्न के लिए सीधा संपर्क।",
                contentEn = listOf(
                    "If you have any questions, concerns, feedback, or data deletion requests regarding this Privacy Policy, please reach out to us:",
                    "• Support Email: saidulali66990@gmail.com",
                    "• In-App Support: Navigate to 'Help & Support' in the main menu to submit a ticket",
                    "• Response Time: We respond to all privacy and account inquiries within 24 to 48 hours."
                ),
                contentHi = listOf(
                    "यदि आपके पास इस गोपनीयता नीति या डेटा संबंधी कोई प्रश्न है, तो कृपया संपर्क करें:",
                    "• ईमेल सहायता: saidulali66990@gmail.com",
                    "• ऐप में सहायता: मेनू में 'Help & Support' पर जाकर टिकट दर्ज करें।",
                    "• प्रतिक्रिया समय: 24 से 48 घंटे के भीतर उत्तर दिया जाता है।"
                )
            )
        )
    }

    val filteredSections = remember(searchQuery, isHindi) {
        if (searchQuery.isBlank()) {
            policySections
        } else {
            policySections.filter { sec ->
                sec.titleEn.contains(searchQuery, ignoreCase = true) ||
                        sec.titleHi.contains(searchQuery, ignoreCase = true) ||
                        sec.summaryEn.contains(searchQuery, ignoreCase = true) ||
                        sec.summaryHi.contains(searchQuery, ignoreCase = true) ||
                        sec.contentEn.any { it.contains(searchQuery, ignoreCase = true) } ||
                        sec.contentHi.any { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Hero Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(StudyMateGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PrivacyTip,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "गोपनीयता नीति" else "Privacy Policy",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isHindi) "StudyMate शैक्षणिक सुरक्षा" else "StudyMate Academic Shield",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Language Switcher
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable { isHindi = !isHindi }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "हिन्दी" else "English",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi)
                            "अंतिम अपडेट: सितंबर 2026 • आपकी व्यक्तिगत पढ़ाई का डेटा सुरक्षित और निजी है।"
                        else
                            "Last updated: September 2026 • Your academic and personal study data is fully protected and private.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Key Highlights Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PolicyBadge(
                            icon = Icons.Default.Lock,
                            label = if (isHindi) "एन्क्रिप्टेड" else "Encrypted",
                            color = Color(0xFF059669),
                            bgColor = Color(0xFFD1FAE5),
                            modifier = Modifier.weight(1f)
                        )
                        PolicyBadge(
                            icon = Icons.Default.Block,
                            label = if (isHindi) "विज्ञापन मुक्त" else "Zero Ads",
                            color = Color(0xFF2563EB),
                            bgColor = Color(0xFFDBEAFE),
                            modifier = Modifier.weight(1f)
                        )
                        PolicyBadge(
                            icon = Icons.Default.CloudDone,
                            label = if (isHindi) "ऑफ़लाइन मोड" else "Offline First",
                            color = Color(0xFF7C3AED),
                            bgColor = Color(0xFFEDE9FE),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Search Bar & Expand All Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isHindi) "नीति में खोजें (जैसे: डेटा, टाइमर)..." else "Search policy (e.g., delete, sync)...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("policy_search_input")
                )

                FilledTonalButton(
                    onClick = { expandedAll = !expandedAll },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (expandedAll) {
                            if (isHindi) "समेटें" else "Collapse"
                        } else {
                            if (isHindi) "विस्तार" else "Expand"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Search result indicator
            if (searchQuery.isNotBlank()) {
                Text(
                    text = if (isHindi) "${filteredSections.size} परिणाम मिले" else "Found ${filteredSections.size} matching sections",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Accordion Sections
            filteredSections.forEach { section ->
                PolicySectionCard(
                    section = section,
                    isHindi = isHindi,
                    forceExpanded = expandedAll
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Card (Copy summary or Contact Support)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHindi) "प्रश्न या सहायता चाहिए?" else "Questions or Data Inquiries?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isHindi)
                            "आप सीधे स्टडीमेट सहायता टीम से संपर्क कर सकते हैं या अपनी प्रतिलिपि सहेज सकते हैं।"
                        else
                            "Contact our support team or copy the policy summary for your records.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clip = ClipData.newPlainText(
                                    "StudyMate Privacy Policy",
                                    "StudyMate Privacy Policy - Contact: saidulali66990@gmail.com\nStrict student data protection, offline-first notes, and zero advertising."
                                )
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    if (isHindi) "गोपनीयता नीति लिंक कॉपी हो गया" else "Policy summary copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("policy_copy_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHindi) "कॉपी करें" else "Copy", fontSize = 12.sp)
                        }

                        if (onNavigateToSupport != null) {
                            Button(
                                onClick = onNavigateToSupport,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("policy_support_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHindi) "सहायता लें" else "Support", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PolicyBadge(
    icon: ImageVector,
    label: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun PolicySectionCard(
    section: PolicySection,
    isHindi: Boolean,
    forceExpanded: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val isCurrentlyExpanded = forceExpanded || expanded

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded }
            .testTag("policy_section_${section.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) section.titleHi else section.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) section.summaryHi else section.summaryEn,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isCurrentlyExpanded) Int.MAX_VALUE else 1
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isCurrentlyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isCurrentlyExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    val contents = if (isHindi) section.contentHi else section.contentEn
                    contents.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
