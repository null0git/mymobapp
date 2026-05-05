package com.paysms.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysms.ui.theme.TelebirrBlue
import com.paysms.ui.theme.TelebirrGreen
import com.paysms.ui.theme.TelebirrOrange
import com.paysms.ui.theme.TextSecondary

data class AppItem(
    val name: String,
    val icon: ImageVector,
    val iconColor: Color = TelebirrGreen
)

@Composable
fun AppsScreen() {
    val topApps = listOf(
        AppItem("My Ethiotel", Icons.Default.PhoneAndroid, TelebirrGreen),
        AppItem("Telegebeya", Icons.Default.ShoppingCart, TelebirrGreen),
        AppItem("Ethiopian Airlines", Icons.Default.AirplanemodeActive, Color(0xFF2E7D32)),
        AppItem("DSTV", Icons.Default.Tv, Color(0xFF1565C0)),
        AppItem("Public Transport", Icons.Default.DirectionsBus, Color(0xFF546E7A)),
        AppItem("WebSprix", Icons.Default.Web, Color(0xFF7B1FA2)),
        AppItem("Zmall", Icons.Default.ShoppingBag, Color(0xFFE53935)),
        AppItem("Ahun", Icons.Default.Star, TelebirrBlue),
        AppItem("Digital Equb", Icons.Default.Group, TelebirrGreen),
        AppItem("Tikus Delivery", Icons.Default.LocalShipping, TelebirrOrange),
        AppItem("Hulu beje", Icons.Default.Home, Color(0xFF1565C0)),
        AppItem("Ashewa", Icons.Default.Favorite, Color(0xFFE53935))
    )

    val bottomApps = listOf(
        AppItem("Guzo Go", Icons.Default.Map, TelebirrOrange),
        AppItem("ACT American", Icons.Default.Star, TelebirrBlue),
        AppItem("AfroRead", Icons.Default.Book, Color(0xFF7B1FA2)),
        AppItem("Awra Store", Icons.Default.Store, Color(0xFFE53935)),
        AppItem("MoveEt", Icons.Default.DirectionsBus, TelebirrBlue),
        AppItem("Safe", Icons.Default.Security, Color(0xFFE53935))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Green header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TelebirrGreen)
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Apps",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        }

        // Apps grid
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Top apps grid (4 rows x 3 columns)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    for (rowIndex in 0 until 4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (colIndex in 0 until 3) {
                                val index = rowIndex * 3 + colIndex
                                if (index < topApps.size) {
                                    AppGridItem(topApps[index])
                                } else {
                                    Spacer(modifier = Modifier.width(80.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Promotional banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TelebirrGreen)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Buy Airtime via telebirr",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "10% OFF",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
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

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom apps grid
            for (rowIndex in 0 until 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (colIndex in 0 until 3) {
                        val index = rowIndex * 3 + colIndex
                        if (index < bottomApps.size) {
                            AppGridItem(bottomApps[index])
                        } else {
                            Spacer(modifier = Modifier.width(80.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AppGridItem(app: AppItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
            .clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = app.icon,
                contentDescription = app.name,
                tint = app.iconColor,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.name,
            color = TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}
