package com.movexa.android.presentation.auth.signup

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.ui.theme.AchievementGold
import com.movexa.android.ui.theme.MovexaSpacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var step by remember { mutableIntStateOf(1) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    val goals = listOf("Lose Weight", "Gain Muscle", "Improve Stamina", "Stay Active")
    var selectedGoalIndex by remember { mutableIntStateOf(0) }
    var isMetric by remember { mutableStateOf(true) }

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isStepOneValid = name.isNotBlank() && isEmailValid && password.length >= 6

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { StepIndicator(step, 2) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else onNavigateToLogin()
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = MovexaSpacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(MovexaSpacing.md))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "step_transition"
            ) { currentStep ->
                if (currentStep == 1) {
                    StepOne(
                        name = name, onNameChange = { name = it },
                        email = email, onEmailChange = { email = it },
                        password = password, onPasswordChange = { password = it },
                        passwordVisible = passwordVisible, onTogglePassword = { passwordVisible = !passwordVisible },
                        isValid = isStepOneValid,
                        onNext = { step = 2 }
                    )
                } else {
                    StepTwo(
                        age = age, onAgeChange = { age = it },
                        weight = weight, onWeightChange = { weight = it },
                        height = height, onHeightChange = { height = it },
                        goals = goals, selectedGoalIndex = selectedGoalIndex, onGoalSelect = { selectedGoalIndex = it },
                        isMetric = isMetric, onToggleUnit = { isMetric = it },
                        isLoading = isLoading,
                        isValid = age.isNotBlank() && weight.isNotBlank() && height.isNotBlank(),
                        onSignUp = {
                            viewModel.signUp(
                                name, email, password, age, weight, height, goals[selectedGoalIndex], onSignUpSuccess
                            )
                        }
                    )
                }
            }

            error?.let {
                Text(
                    it,
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = MovexaSpacing.md)
                )
            }

            Spacer(Modifier.height(MovexaSpacing.xl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?", color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Sign In", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(current: Int, total: Int) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..total) {
            Box(
                modifier = Modifier
                    .size(width = if (i == current) 24.dp else 8.dp, height = 8.dp)
                    .clip(CircleShape)
                    .background(if (i <= current) colorScheme.primary else colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
private fun StepOne(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean, onTogglePassword: () -> Unit,
    isValid: Boolean,
    onNext: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text("Create account", color = colorScheme.onSurface, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Let's get you started on your journey", color = colorScheme.onSurfaceVariant, fontSize = 16.sp)

        Spacer(Modifier.height(MovexaSpacing.xl))

        SignUpField(name, onNameChange, "Full Name", Icons.Rounded.Person)
        Spacer(Modifier.height(MovexaSpacing.md))
        SignUpField(email, onEmailChange, "Email Address", Icons.Rounded.Email, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(MovexaSpacing.md))
        SignUpField(
            password, onPasswordChange, "Password", Icons.Rounded.Lock,
            keyboardType = KeyboardType.Password, isPassword = true, passwordVisible = passwordVisible,
            onTogglePassword = onTogglePassword, imeAction = ImeAction.Done
        )

        Spacer(Modifier.height(MovexaSpacing.xxl))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isValid,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text("Continue", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepTwo(
    age: String, onAgeChange: (String) -> Unit,
    weight: String, onWeightChange: (String) -> Unit,
    height: String, onHeightChange: (String) -> Unit,
    goals: List<String>, selectedGoalIndex: Int, onGoalSelect: (Int) -> Unit,
    isMetric: Boolean, onToggleUnit: (Boolean) -> Unit,
    isLoading: Boolean,
    isValid: Boolean,
    onSignUp: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text("About You", color = colorScheme.onSurface, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Help us personalize your experience", color = colorScheme.onSurfaceVariant, fontSize = 16.sp)

        Spacer(Modifier.height(MovexaSpacing.xl))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md)) {
            SignUpField(age, onAgeChange, "Age", Icons.Rounded.Cake, modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
            UnitToggle(isMetric, onToggleUnit, Modifier.weight(1f))
        }
        Spacer(Modifier.height(MovexaSpacing.md))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md)) {
            SignUpField(weight, onWeightChange, if (isMetric) "Weight (kg)" else "Weight (lb)", Icons.Rounded.MonitorWeight, Modifier.weight(1f), keyboardType = KeyboardType.Number)
            SignUpField(height, onHeightChange, if (isMetric) "Height (cm)" else "Height (ft/in)", Icons.Rounded.Height, Modifier.weight(1f), keyboardType = KeyboardType.Number)
        }

        Spacer(Modifier.height(MovexaSpacing.xl))
        Text("Your Goal", style = MaterialTheme.typography.titleMedium, color = colorScheme.onSurface)
        Spacer(Modifier.height(MovexaSpacing.sm))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            goals.forEachIndexed { index, goal ->
                val selected = index == selectedGoalIndex
                FilterChip(
                    selected = selected,
                    onClick = { onGoalSelect(index) },
                    label = { Text(goal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AchievementGold.copy(alpha = 0.2f),
                        selectedLabelColor = AchievementGold
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            }
        }

        Spacer(Modifier.height(MovexaSpacing.xxl))

        Button(
            onClick = onSignUp,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isValid && !isLoading,
            shape = MaterialTheme.shapes.large
        ) {
            if (isLoading) CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            else Text("Complete Setup", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun UnitToggle(isMetric: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(Modifier.fillMaxSize().padding(4.dp)) {
            Box(
                Modifier.weight(1f).fillMaxHeight()
                    .clip(MaterialTheme.shapes.small)
                    .background(if (isMetric) colorScheme.primary else Color.Transparent)
                    .clickable { onToggle(true) },
                contentAlignment = Alignment.Center
            ) {
                Text("Metric", color = if (isMetric) colorScheme.onPrimary else colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
            Box(
                Modifier.weight(1f).fillMaxHeight()
                    .clip(MaterialTheme.shapes.small)
                    .background(if (!isMetric) colorScheme.primary else Color.Transparent)
                    .clickable { onToggle(false) },
                contentAlignment = Alignment.Center
            ) {
                Text("Imperial", color = if (!isMetric) colorScheme.onPrimary else colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SignUpField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next
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
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true
    )
}
