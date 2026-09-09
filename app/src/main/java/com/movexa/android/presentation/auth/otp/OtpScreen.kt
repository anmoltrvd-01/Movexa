package com.movexa.android.presentation.auth.otp

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PhonelinkLock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.ui.theme.MovexaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    email: String,
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isSent by viewModel.isSent.collectAsState()
    val error by viewModel.error.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var otp by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!isSent) viewModel.sendOtp(email)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background)
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = MovexaSpacing.lg)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(MovexaSpacing.xl))

            Icon(
                Icons.Rounded.PhonelinkLock,
                null,
                modifier = Modifier.size(80.dp),
                tint = colorScheme.primary
            )

            Spacer(Modifier.height(MovexaSpacing.lg))

            Text(
                "Verify your email",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Text(
                "We've sent a 6-digit code to\n$email",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = MovexaSpacing.md)
            )

            Spacer(Modifier.height(MovexaSpacing.xxl))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                placeholder = {
                    Text(
                        "000000",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            )

            error?.let {
                Text(
                    it,
                    color = colorScheme.error,
                    modifier = Modifier.padding(top = MovexaSpacing.md)
                )
            }

            Spacer(Modifier.height(MovexaSpacing.xxl))

            Button(
                onClick = { viewModel.verifyOtp(email, otp, onVerifySuccess) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = otp.length >= 4 && !isLoading,
                shape = MaterialTheme.shapes.large
            ) {
                if (isLoading) CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Verify", style = MaterialTheme.typography.titleMedium)
            }

            TextButton(
                onClick = { viewModel.sendOtp(email) },
                modifier = Modifier.padding(top = MovexaSpacing.md)
            ) {
                Text("Resend code", color = colorScheme.primary)
            }
        }
    }
}
