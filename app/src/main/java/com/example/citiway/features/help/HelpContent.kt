package com.example.citiway.features.help

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.draw.shadow

/**
 * Main content container for the Help screen.
 *
 * @param paddingValues Padding from the scaffold to respect system bars
 * @param userEmail Optional pre-filled email from the logged-in user
 * @param userName Optional user name for personalizing support requests
 */
@Composable
fun HelpContent(
    paddingValues: PaddingValues,
    userEmail: String? = null,
    userName: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FAQAndContactScreen(userEmail = userEmail, userName = userName)
    }
}

// ========================================
// FAQ Components
// ========================================

/**
 * Expandable FAQ item card with question and answer.
 *
 * @param question The FAQ question text
 * @param answer The FAQ answer text, shown when expanded
 * @param modifier Optional modifier for the card
 */
@Composable
fun FAQItem(
    question: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Question header with expand/collapse indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = question,
                    color = Color(0xFF109FD6),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF109FD6)
                )
            }

            // Show answer when expanded
            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = answer,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Scrollable list of frequently asked questions with scroll indicator.
 */
@Composable
fun FAQScreen() {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            FAQItem(
                question = "Which transport services are included in this app?",
                answer = "This app includes MyCiTi buses and PRASA trains, integrated with Google Maps for real-time routing and schedules."
            )
            FAQItem(
                question = "Can I save my favourite routes for quick access?",
                answer = "You can save and favourite you most used routes by clicking on the heart next to the trip journey"
            )
            FAQItem(
                question = "Can I plan multi-modal trips using this app?",
                answer = "Yes, you can combine buses, trains, and walking routes in a single trip plan seamlessly."
            )
            FAQItem(
                question = "Does the app support offline use or map caching?",
                answer = "Currently, internet connectivity is required to fetch up-to-date schedules and maps. Offline caching is planned for future releases."
            )
            FAQItem(
                question = "Is there a fare calculator or ticket purchase feature?",
                answer = "The app does not process ticket purchases but provides fare estimations for your journey"
            )
            FAQItem(
                question = "Where can I provide feedback or report issues?",
                answer = "Use the contact form below or visit our website support page to send feedback or report errors."
            )

            // Extra space at bottom for scroll indicator
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Gradient scroll indicator at bottom when more content is available
        if (!scrollState.canScrollForward && scrollState.maxValue > 0) {
            // At bottom - no indicator needed
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
        }
    }
}

// ========================================
// Contact Form Components
// ========================================

/**
 * Contact form for submitting support questions via email.
 *
 * Opens the device's email client with pre-filled information when submitted.
 *
 * @param userEmail Optional pre-filled email address from user profile
 * @param userName Optional user name for personalizing the email subject
 */
@Composable
fun ContactForm(
    userEmail: String? = null,
    userName: String? = null
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue(userEmail ?: "")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }

    // Pre-fill email field when user data becomes available
    LaunchedEffect(userEmail) {
        if (userEmail != null && email.text.isEmpty()) {
            email = TextFieldValue(userEmail)
        }
    }

    // Enable submit button only when both fields have content
    val isSubmitEnabled = email.text.isNotBlank() && message.text.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Form header
            Text(
                text = "Can't see your question?",
                color = Color(0xFF109FD6),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Submit your question below:",
                color = Color(0xFF616161),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp),
                fontStyle = FontStyle.Italic
            )

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Your Email Address", fontSize = 13.sp, fontStyle = FontStyle.Italic) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF109FD6),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    cursorColor = Color(0xFF109FD6),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Message input field
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Type your message here...", fontSize = 13.sp, fontStyle = FontStyle.Italic) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF109FD6),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    cursorColor = Color(0xFF109FD6),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions.Default,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Submit button - opens email client with pre-filled data
            Button(
                onClick = {
                    // Use username if available, otherwise extract from email
                    val displayName = userName ?: email.text.substringBefore("@")

                    // Create email intent with pre-filled subject and body
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("citiwayapp@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "App Feedback from $displayName")
                        putExtra(Intent.EXTRA_TEXT,
                            "Message:\n${message.text}"
                        )
                    }

                    // Attempt to open email client with error handling
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    } catch (e: Exception) {
                        // Show user-friendly error if no email app is installed
                        Toast.makeText(
                            context,
                            "No email app found on your device.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                enabled = isSubmitEnabled,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(120.dp)
                    .height(42.dp),
                shape = RoundedCornerShape(21.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF109FD6),
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            ) {
                Text(
                    text = "Submit",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ========================================
// Screen Composition
// ========================================

/**
 * Combined FAQ and contact form screen.
 *
 * @param userEmail Optional pre-filled email address
 * @param userName Optional user name for personalization
 */
@Composable
fun FAQAndContactScreen(
    userEmail: String? = null,
    userName: String? = null
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HeaderSection()
        VerticalSpace(16)

        // Scrollable FAQ section
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            FAQScreen()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact form at bottom
        ContactForm(userEmail = userEmail, userName = userName)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Header section displaying the FAQ title.
 */
@Composable
private fun HeaderSection() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Title("FAQ's")
        VerticalSpace(4)
    }
}