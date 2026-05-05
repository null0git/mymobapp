package com.paysms.ui.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysms.ui.theme.TelebirrGreen
import com.paysms.ui.theme.TextHint
import com.paysms.ui.theme.TextSecondary

@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onLogout: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val accountNumber by viewModel.accountNumber.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val endekiseBalance by viewModel.endekiseBalance.collectAsState()
    val rewardBalance by viewModel.rewardBalance.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val region by viewModel.region.collectAsState()
    val city by viewModel.city.collectAsState()
    val hiddenSettingsEnabled by viewModel.hiddenSettingsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Green header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TelebirrGreen)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Account",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TelebirrGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName.ifEmpty { "User" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "+251 $phoneNumber",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = TelebirrGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Menu items
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    AccountMenuItem(Icons.Default.AccountCircle, "My Profile", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.CreditCard, "My Cards", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.History, "Transaction History", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Fingerprint, "Biometric Settings", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Lock, "Change PIN", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Notifications, "Notifications", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Language, "Language", TelebirrGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // More options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    AccountMenuItem(Icons.Default.Share, "Invite Friends", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Star, "Rate Us", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Help, "Help & Support", TelebirrGreen)
                    AccountMenuDivider()
                    AccountMenuItem(Icons.Default.Info, "About", TelebirrGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logout button
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version text (tap 7 times to enable hidden settings)
            Text(
                text = "Version 1.0.0",
                color = TextHint,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onVersionTap() }
                    .padding(vertical = 8.dp),
                fontWeight = FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Hidden settings section
            AnimatedVisibility(visible = hiddenSettingsEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hidden Settings",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFFE65100)
                            )
                        }

                        HiddenSettingField("Display Name", userName) { viewModel.updateUserName(it) }
                        HiddenSettingField("Full Name", fullName) { viewModel.updateFullName(it) }
                        HiddenSettingField("Phone Number", phoneNumber) { viewModel.updatePhoneNumber(it) }
                        HiddenSettingField("Email", email) { viewModel.updateEmail(it) }
                        HiddenSettingField("Account Number", accountNumber) { viewModel.updateAccountNumber(it) }
                        HiddenSettingField("Balance (ETB)", balance) { viewModel.updateBalance(it) }
                        HiddenSettingField("Endekise Balance", endekiseBalance) { viewModel.updateEndekiseBalance(it) }
                        HiddenSettingField("Reward Balance", rewardBalance) { viewModel.updateRewardBalance(it) }
                        HiddenSettingField("Gender", gender) { viewModel.updateGender(it) }
                        HiddenSettingField("Date of Birth", dateOfBirth) { viewModel.updateDateOfBirth(it) }
                        HiddenSettingField("Region", region) { viewModel.updateRegion(it) }
                        HiddenSettingField("City", city) { viewModel.updateCity(it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AccountMenuItem(icon: ImageVector, title: String, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AccountMenuDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 56.dp),
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Composable
private fun HiddenSettingField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE65100),
            unfocusedBorderColor = Color(0xFFBCAAA4),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
