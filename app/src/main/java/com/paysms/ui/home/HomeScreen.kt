package com.paysms.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysms.data.UserPreferences
import com.paysms.ui.theme.TelebirrBlue
import com.paysms.ui.theme.TelebirrGradientEnd
import com.paysms.ui.theme.TelebirrGradientStart
import com.paysms.ui.theme.TelebirrGreen
import com.paysms.ui.theme.TelebirrLightGreen
import com.paysms.ui.theme.TelebirrOrange
import com.paysms.ui.theme.TextSecondary

data class ServiceItem(
    val name: String,
    val icon: ImageVector,
    val badgeText: String? = null,
    val iconColor: Color = TelebirrGreen
)

data class QuickAction(
    val name: String,
    val icon: ImageVector,
    val iconColor: Color = TelebirrGreen
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userPreferences: UserPreferences,
    onSendMoney: () -> Unit = {},
    onTransactionDetails: () -> Unit = {}
) {
    val userName by userPreferences.userName.collectAsState(initial = "User")
    val balance by userPreferences.balance.collectAsState(initial = "******")
    val endekiseBalance by userPreferences.endekiseBalance.collectAsState(initial = "******")
    val rewardBalance by userPreferences.rewardBalance.collectAsState(initial = "******")
    var showBalance by remember { mutableStateOf(false) }

    val services = listOf(
        ServiceItem("Send Money", Icons.Default.Send),
        ServiceItem("Buy Airtime", Icons.Default.PhoneAndroid, badgeText = "10%+"),
        ServiceItem("Buy Package", Icons.Default.ShoppingCart, badgeText = "10%+"),
        ServiceItem("Cash In/Out", Icons.Default.SwapHoriz),
        ServiceItem("Financial\nService", Icons.Default.AccountBalanceWallet, iconColor = TelebirrBlue),
        ServiceItem("Pay for\nMerchant", Icons.Default.Store),
        ServiceItem("Payment", Icons.Default.Payment),
        ServiceItem("Apps", Icons.Default.Apps)
    )

    val quickActions = listOf(
        QuickAction("Pay Ethio\ntelecom Bill", Icons.Default.Receipt, TelebirrGreen),
        QuickAction("Schedule\npayment", Icons.Default.Schedule, TelebirrGreen),
        QuickAction("Transfer to\nBank", Icons.Default.CreditCard, TelebirrGreen),
        QuickAction("Transfer to\nWallet", Icons.Default.AccountBalanceWallet, TelebirrGreen)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F9F5))
    ) {
        // Green header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(TelebirrGradientStart, TelebirrGradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Top bar with logo and icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = " ethio telecom",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        BadgedBox(
                            badge = {
                                Badge(containerColor = TelebirrOrange) {
                                    Text("99+", fontSize = 8.sp)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("English", color = Color.White, fontSize = 12.sp)
                    }
                }

                // User greeting
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selam, $userName",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                // Balance section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Balance (ETB)",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = { showBalance = !showBalance },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle balance",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = if (showBalance) balance else "******",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                // endekise and Reward
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("endekise (ETB)", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        Icon(Icons.Default.VisibilityOff, null, tint = Color.White, modifier = Modifier.size(14.dp).padding(start = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reward (ETB)", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        Icon(Icons.Default.VisibilityOff, null, tint = Color.White, modifier = Modifier.size(14.dp).padding(start = 4.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (showBalance) endekiseBalance else "******",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        if (showBalance) rewardBalance else "******",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // "ONE APP FOR ALL YOUR NEEDS" banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(TelebirrGreen, TelebirrLightGreen)
                    )
                )
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ONE APP FOR ALL YOUR NEEDS!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Services grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                for (rowIndex in 0 until 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (colIndex in 0 until 4) {
                            val index = rowIndex * 4 + colIndex
                            if (index < services.size) {
                                ServiceItemCard(
                                    service = services[index],
                                    onClick = {
                                        if (services[index].name == "Send Money") onSendMoney()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Promotional banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TelebirrGreen)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "tele gebeya",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Online Shopping",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Dots indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (index == 0) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) TelebirrGreen else Color.LightGray)
                    )
                }
            }

            // Transaction Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTransactionDetails() }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction Details",
                    color = TelebirrBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "  >",
                    color = TelebirrBlue,
                    fontSize = 14.sp
                )
            }

            // Quick actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                quickActions.forEach { action ->
                    QuickActionItem(action)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scan QR button
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreen)
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan QR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ServiceItemCard(service: ServiceItem, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.name,
                    tint = service.iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            if (service.badgeText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(TelebirrOrange, RoundedCornerShape(4.dp))
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = service.badgeText,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = service.name,
            color = TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun QuickActionItem(action: QuickAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(action.iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.name,
                tint = action.iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = action.name,
            color = TextSecondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 13.sp
        )
    }
}
