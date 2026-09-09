package com.movexa.android.presentation.auth.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.ui.theme.MovexaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = email.isNotBlank() && password.length >= 6 && isEmailValid

    Scaffold(containerColor = colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = MovexaSpacing.lg)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(MovexaSpacing.xxl))

            // Branding Section
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.FitnessCenter,
                    null,
                    modifier = Modifier.size(40.dp),
                    tint = colorScheme.primary
                )
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            Text(
                "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Text(
                "Sign in to continue your journey",
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(MovexaSpacing.xxl))

            // Form Section
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                icon = Icons.Rounded.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(MovexaSpacing.md))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Rounded.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = {
                    if (isFormValid) viewModel.login(email, password, onLoginSuccess)
                })
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { 
                    if (email.isNotBlank()) onNavigateToOtp(email) 
                    else viewModel.setError("Enter your email first")
                }) {
                    Text("Forgot Password?", color = colorScheme.primary)
                }
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            Button(
                onClick = { viewModel.login(email, password, onLoginSuccess) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isFormValid && !isLoading,
                shape = MaterialTheme.shapes.large
            ) {
                if (isLoading) CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Sign In", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.outlineVariant)
                Text(" OR ", modifier = Modifier.padding(horizontal = 16.dp), color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.outlineVariant)
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            // Google Sign In
            OutlinedButton(
                onClick = { /* Implement Google Sign In if needed */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large,
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(colorScheme.outlineVariant))
            ) {
                Icon(Icons.Rounded.AccountCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text("Continue with Google", color = colorScheme.onSurface)
            }

            error?.let {
                Text(
                    it,
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = MovexaSpacing.md)
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(vertical = MovexaSpacing.xl),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New to Movexa?", color = colorScheme.onSurfaceVariant)
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Create Account", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword!!) {
                    Icon(if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null)
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true
    )
}
