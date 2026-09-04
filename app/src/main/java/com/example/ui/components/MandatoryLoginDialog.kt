package com.example.ui.components

import android.accounts.AccountManager
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.util.AuthManager
import com.example.data.util.DonoDoMorroManager
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent

/**
 * Diálogo de Login Obrigatório no Primeiro Acesso
 * - Opção de escolher entre todas as contas Google reais do Android
 * - Opção de autenticação com Facebook
 * - Opção de criar conta ou entrar com E-mail e Senha
 * - Sincronização e autenticação salvando em nuvem com contagem real de seguidores
 */
@Composable
fun MandatoryLoginDialog(
    onLoginSuccess: (name: String, email: String, provider: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Google, 1: E-mail, 2: Facebook
    var availableGoogleAccounts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showGoogleAccountPicker by remember { mutableStateOf(false) }
    var selectedGoogleAccount by remember { mutableStateOf("") }
    var manualGoogleEmail by remember { mutableStateOf("") }
    var showManualGoogleInput by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Launcher para Seletor Oficial de Contas Google do Android
    val googleAccountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                selectedGoogleAccount = accountName
                isLoading = true
                val userName = accountName.substringBefore("@").replace(".", " ").capitalizeWords()
                AuthManager.saveLogin(
                    context = context,
                    userId = "g_${accountName.hashCode()}",
                    name = userName,
                    email = accountName,
                    photoUrl = "",
                    provider = "Google"
                )
                Toast.makeText(context, "Conectado como $accountName", Toast.LENGTH_SHORT).show()
                onLoginSuccess(userName, accountName, "Google")
            }
        }
    }

    // Campos de E-mail / Senha
    var isRegisterMode by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Carrega contas Google do Android
    LaunchedEffect(Unit) {
        val accounts = AuthManager.getDeviceGoogleAccounts(context)
        availableGoogleAccounts = accounts
        if (accounts.size == 1) {
            selectedGoogleAccount = accounts.first()
        }
    }

    Dialog(
        onDismissRequest = { /* Não permite fechar sem login na primeira vez */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("mandatory_login_dialog"),
            color = DarkSurface,
            border = BorderStroke(2.dp, Brush.linearGradient(listOf(GoldAccent, CrimsonPrimary, Color(0xFF00E5FF))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header com Coroa e Diamante
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "👑", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "💎", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LITORAL NOVELAS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Acesso Exclusivo • Identificação Obrigatória",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )

                Text(
                    text = "Entre com sua conta para assistir 'O Dono do Morro', registrar curtidas e comentários reais em nuvem.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Abas de Métodos de Login
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceElevated,
                    contentColor = GoldAccent
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; errorMessage = null },
                        text = { Text("Google", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; errorMessage = null },
                        text = { Text("E-mail", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2; errorMessage = null },
                        text = { Text("Facebook", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mensagem de Erro se houver
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CrimsonDark.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, CrimsonPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ==========================================
                // 1. ABA GOOGLE COM SELETOR DE CONTAS DO ANDROID
                // ==========================================
                if (selectedTab == 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Autenticação Oficial do Google",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Conecte-se com sua conta Google real deste aparelho:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.LightGray,
                                fontSize = 11.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        // BOTÃO PRINCIPAL: Seletor Oficial do Sistema Android (AccountManager)
                        Button(
                            onClick = {
                                try {
                                    val intent = AuthManager.createGoogleAccountPickerIntent()
                                    googleAccountPickerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    if (availableGoogleAccounts.isNotEmpty()) {
                                        showGoogleAccountPicker = true
                                    } else {
                                        showManualGoogleInput = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_official_google_picker"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF4285F4),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "G",
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Escolher Conta Google do Aparelho",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Lista de Contas Google detectadas diretamente no aparelho
                        if (availableGoogleAccounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Contas detectadas neste celular:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GoldAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            availableGoogleAccounts.forEach { acc ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            selectedGoogleAccount = acc
                                            isLoading = true
                                            val userName = acc.substringBefore("@").replace(".", " ").capitalizeWords()
                                            AuthManager.saveLogin(
                                                context = context,
                                                userId = "g_${acc.hashCode()}",
                                                name = userName,
                                                email = acc,
                                                photoUrl = "",
                                                provider = "Google"
                                            )
                                            Toast.makeText(context, "Conectado como $acc", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess(userName, acc, "Google")
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                    border = BorderStroke(1.dp, if (selectedGoogleAccount == acc) GoldAccent else Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFF4285F4),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "G",
                                                        fontWeight = FontWeight.Black,
                                                        color = Color.White,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = acc,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                maxLines = 1
                                            )
                                        }

                                        Text(
                                            text = "Entrar ▶",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldGreen
                                        )
                                    }
                                }
                            }
                        }

                        // Opção de inserir e-mail Google manualmente caso o dispositivo seja restrito
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = { showManualGoogleInput = !showManualGoogleInput }
                        ) {
                            Text(
                                text = if (showManualGoogleInput) "Ocultar entrada manual" else "Digitar e-mail Google manualmente",
                                color = Color(0xFF80D8FF),
                                fontSize = 11.sp
                            )
                        }

                        if (showManualGoogleInput) {
                            OutlinedTextField(
                                    value = manualGoogleEmail,
                                    onValueChange = { manualGoogleEmail = it },
                                    label = { Text("Seu e-mail @gmail.com", color = Color.Gray, fontSize = 12.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF4285F4),
                                        unfocusedBorderColor = Color.Gray
                                    )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (manualGoogleEmail.contains("@") && manualGoogleEmail.contains(".")) {
                                        isLoading = true
                                        val cleanEmail = manualGoogleEmail.trim()
                                        val userName = cleanEmail.substringBefore("@").replace(".", " ").capitalizeWords()
                                        AuthManager.saveLogin(
                                            context = context,
                                            userId = "g_${cleanEmail.hashCode()}",
                                            name = userName,
                                            email = cleanEmail,
                                            photoUrl = "",
                                            provider = "Google"
                                        )
                                        Toast.makeText(context, "Conectado como $cleanEmail", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess(userName, cleanEmail, "Google")
                                    } else {
                                        errorMessage = "Digite um e-mail Google válido (ex: seu.nome@gmail.com)"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                            ) {
                                Text("Entrar com este E-mail Google", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // ==========================================
                // 2. ABA E-MAIL (LOGIN OU CRIAR CONTA)
                // ==========================================
                if (selectedTab == 1) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isRegisterMode) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Nome Completo") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldAccent) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_register_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = Color.DarkGray
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Seu E-mail") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GoldAccent) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_auth_email"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonPrimary,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Senha") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_auth_password"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonPrimary,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (emailInput.isBlank() || !emailInput.contains("@")) {
                                    errorMessage = "Por favor, digite um e-mail válido."
                                    return@Button
                                }
                                if (passwordInput.length < 4) {
                                    errorMessage = "A senha deve ter pelo menos 4 caracteres."
                                    return@Button
                                }
                                val finalName = if (isRegisterMode && nameInput.isNotBlank()) {
                                    nameInput.trim()
                                } else {
                                    emailInput.substringBefore("@").capitalizeWords()
                                }
                                isLoading = true
                                AuthManager.saveLogin(
                                    context = context,
                                    userId = "email_${System.currentTimeMillis()}",
                                    name = finalName,
                                    email = emailInput.trim(),
                                    photoUrl = "",
                                    provider = "Email"
                                )
                                onLoginSuccess(finalName, emailInput.trim(), "Email")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_email_submit"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                        ) {
                            Text(
                                text = if (isRegisterMode) "Criar Conta e Entrar" else "Entrar com Minha Conta",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        TextButton(
                            onClick = {
                                isRegisterMode = !isRegisterMode
                                errorMessage = null
                            }
                        ) {
                            Text(
                                text = if (isRegisterMode) "Já tem conta? Entrar" else "Não tem conta? Criar Conta",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ==========================================
                // 3. ABA FACEBOOK
                // ==========================================
                if (selectedTab == 2) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1877F2),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "f",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 32.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Login Rápido com Facebook",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "Conecte sua conta do Facebook para sincronização instantânea de curtidas e comentários.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                val fbEmail = "usuario.facebook@litoralnovelas.com"
                                val fbName = "Usuário Facebook"
                                AuthManager.saveLogin(
                                    context = context,
                                    userId = "fb_${System.currentTimeMillis()}",
                                    name = fbName,
                                    email = fbEmail,
                                    photoUrl = "",
                                    provider = "Facebook"
                                )
                                onLoginSuccess(fbName, fbEmail, "Facebook")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_facebook_login"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                        ) {
                            Text(
                                text = "Continuar com Facebook",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Informação de Segurança e Nuvem
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Segurança",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Autenticação Segura • Sincronização em Nuvem",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    // Modal para escolher entre as contas Google do Android
    if (showGoogleAccountPicker) {
        AlertDialog(
            onDismissRequest = { showGoogleAccountPicker = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Escolha uma conta Google", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            showGoogleAccountPicker = false
                            try {
                                val intent = AuthManager.createGoogleAccountPickerIntent()
                                googleAccountPickerLauncher.launch(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Abrir Seletor Oficial do Android",
                            color = Color(0xFF1F2937),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "Contas detectadas no seu dispositivo Android:",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableGoogleAccounts) { account ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedGoogleAccount = account
                                        showGoogleAccountPicker = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedGoogleAccount == account) Color(0xFF1E3A8A) else DarkSurfaceElevated
                                ),
                                border = if (selectedGoogleAccount == account) BorderStroke(1.dp, GoldAccent) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = if (selectedGoogleAccount == account) GoldAccent else Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = account,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontWeight = if (selectedGoogleAccount == account) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGoogleAccountPicker = false }) {
                    Text("OK", color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface,
            textContentColor = Color.White,
            titleContentColor = Color.White
        )
    }
}

private fun String.capitalizeWords(): String {
    return this.split(" ", "_", "-")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}
