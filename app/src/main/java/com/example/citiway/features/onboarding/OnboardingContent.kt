package com.example.citiway.features.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.features.shared.OnboardingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingContent(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val languages = listOf("English", "Afrikaans", "isiXhosa")

    // Brand colors
    val orangeColor = Color(0xFFFCA311)
    val darkCharcoal = Color(0xFF0B3B4E)
    val celestialBlue = Color(0xFF109FD6)
    val antiFlashWhite = Color(0xFFF1F1F1)

    // Animations - More pronounced movement
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val circleOffset1 by infiniteTransition.animateFloat(
        initialValue = -120f,
        targetValue = -80f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val circleOffset2 by infiniteTransition.animateFloat(
        initialValue = 60f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    val scale by animateFloatAsState(
        targetValue = if (name.isNotBlank() && email.isNotBlank()) 1f else 0.95f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "button_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        antiFlashWhite,
                        Color(0xFFE8F4F8)
                    )
                )
            )
    ) {
        // Decorative circles with more obvious movement
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = circleOffset1.dp, y = circleOffset1.dp)
                .alpha(animatedAlpha * 0.5f)
                .clip(CircleShape)
                .background(celestialBlue)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = circleOffset2.dp, y = circleOffset2.dp)
                .alpha(animatedAlpha * 0.4f)
                .clip(CircleShape)
                .background(orangeColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo with elevation shadow effect
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.citiway_logo),
                    contentDescription = "CitiWay Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to CitiWay",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your multimodal travel companion",
                fontSize = 15.sp,
                color = darkCharcoal.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Error message
            if (errorMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFC62828),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Input Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = celestialBlue,
                            focusedLabelColor = celestialBlue,
                            cursorColor = celestialBlue,
                            unfocusedBorderColor = darkCharcoal.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = celestialBlue,
                            focusedLabelColor = celestialBlue,
                            cursorColor = celestialBlue,
                            unfocusedBorderColor = darkCharcoal.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Language Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedLanguage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Language") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = celestialBlue,
                                focusedLabelColor = celestialBlue,
                                unfocusedBorderColor = darkCharcoal.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            languages.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(language) },
                                    onClick = {
                                        selectedLanguage = language
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button with gradient and animation
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        isLoading = true
                        errorMessage = null

                        viewModel.saveUser(
                            name = name.trim(),
                            email = email.trim(),
                            preferredLanguage = selectedLanguage,
                            onSuccess = {
                                isLoading = false
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            },
                            onError = { error ->
                                isLoading = false
                                errorMessage = error
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .scale(scale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = celestialBlue,
                    contentColor = Color.White,
                    disabledContainerColor = celestialBlue.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                ),
                enabled = name.isNotBlank() && email.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Powered by section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.google_maps),
                    contentDescription = "Google Maps",
                    modifier = Modifier.height(65.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}