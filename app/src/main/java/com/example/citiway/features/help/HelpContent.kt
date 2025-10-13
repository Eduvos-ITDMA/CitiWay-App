package com.example.citiway.features.help

// HomeContent.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast



@Composable
fun HelpContent(
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FAQAndContactScreen()
    }
}
@Composable
fun FAQItem(
    question: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(horizontal=10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                color = Color(0xFF109FD6),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines=1,
                overflow=TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowRight,
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color(0xFF109FD6)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))


        Text(
            text = answer,
            fontSize = 15.sp,
            color = Color(0xFF757575),
            maxLines = if (expanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 0.dp, end = 0.dp),

            )
        Spacer(modifier = Modifier.height(15.dp))//spacer between questions
    }
}
@Composable
fun FAQScreen() {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
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
            question = "Does the app support offline use or map caching",
            answer = "Currently, internet connectivity is required to fetch up-to-date schedules and maps. Offline caching is planned for future releases."
        )
        FAQItem(
            question = "Is there a fare calculator or ticket purchase feature?",
            answer = "The app does not process ticket purchases but provides fare estimations for your jouney"
        )
        FAQItem(
            question = "Can I save my favourite routes for quick access?",
            answer = "You can save and favourite you most used routes by clicking on the heart next to the trip journey"
        )
        FAQItem(
            question = "Where can I provide feedback or report issues?",
            answer = "Use the contact form below or visit our website support page to send feedback or report errors."
        )

        // Add more FAQItem composables for other questions
    }
}

@Composable
fun ShadowedFAQ(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 380.dp)
            .drawBehind {
                val shadowHeight = 8.dp.toPx()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        startY = size.height - shadowHeight,
                        endY = size.height
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - shadowHeight),
                    size = androidx.compose.ui.geometry.Size(size.width, shadowHeight)
                )
            }

    )
    {
        FAQScreen()
    }
}
@Composable
fun ContactForm() {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    val isSubmitEnabled = email.text.isNotBlank() && message.text.isNotBlank()
    Column (modifier=Modifier.fillMaxWidth().padding(horizontal=40.dp)){
        Text(
            text = "Can’t see your Question?",
            color = Color(0xFF109FD6),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)

        )
        Text(
            text = "Submit your question below:",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            fontStyle = FontStyle.Italic
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Enter Email Address",fontStyle = FontStyle.Italic)},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF109FD6),
                unfocusedBorderColor = Color(0xFF109FD6),
                cursorColor = Color(0xFF109FD6)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type your message here...", fontStyle = FontStyle.Italic) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF109FD6),
                unfocusedBorderColor = Color(0xFF109FD6),
                cursorColor = Color(0xFF109FD6)
            ),
            keyboardOptions = KeyboardOptions.Default,
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")//
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("citiwayapp@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
                        putExtra(Intent.EXTRA_TEXT, "Issue: \n${message.text}")
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found on your device.", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = isSubmitEnabled,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF109FD6))
            ) {
                Text(
                    text = "Submit",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }


    }


}
@Composable
fun FAQAndContactScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        // Header section
        HeaderSection()
        VerticalSpace(20)
        Spacer(modifier = Modifier.height(0.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()

        ) {
            FAQScreen()
        }


        Spacer(modifier = Modifier.height(0.dp))
        VerticalSpace(30)
        ContactForm()
    }
}


@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth()

    ) {
        Title("FAQ's")
        VerticalSpace(4)

    }
}

