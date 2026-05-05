package com.paysms.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paysms.data.TransactionManager
import com.paysms.data.UserPreferences
import com.paysms.ui.account.AccountScreen
import com.paysms.ui.account.AccountViewModel
import com.paysms.ui.apps.AppsScreen
import com.paysms.ui.home.HomeScreen
import com.paysms.ui.payment.PaymentScreen
import com.paysms.ui.sendmoney.SendMoneyScreen
import com.paysms.ui.theme.TelebirrGreen
import com.paysms.ui.transaction.TransactionHistoryScreen
import kotlinx.coroutines.launch

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Payment : BottomNavItem("payment", "Payment", Icons.Filled.Payment, Icons.Outlined.Payment)
    data object Apps : BottomNavItem("apps", "Apps", Icons.Filled.Apps, Icons.Outlined.Apps)
    data object Account : BottomNavItem("account", "Account", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainNavigation(
    userPreferences: UserPreferences,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val transactionManager = TransactionManager(context)
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Payment,
        BottomNavItem.Apps,
        BottomNavItem.Account
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in navItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    val currentDestination = navBackStackEntry?.destination

                    navItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TelebirrGreen,
                                selectedTextColor = TelebirrGreen,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    userPreferences = userPreferences,
                    onSendMoney = { navController.navigate("send_money") },
                    onTransactionDetails = { navController.navigate("transaction_history") }
                )
            }
            composable(BottomNavItem.Payment.route) {
                PaymentScreen()
            }
            composable(BottomNavItem.Apps.route) {
                AppsScreen()
            }
            composable(BottomNavItem.Account.route) {
                val accountViewModel: AccountViewModel = viewModel()
                AccountScreen(
                    viewModel = accountViewModel,
                    onLogout = onLogout
                )
            }
            composable("send_money") {
                val balance by userPreferences.balance.collectAsState(initial = "0")
                val currentBalance = balance.toDoubleOrNull() ?: 0.0

                SendMoneyScreen(
                    currentBalance = currentBalance,
                    onSendMoney = { phone, name, amount, note ->
                        scope.launch {
                            val newBalance = currentBalance - amount - calculateFee(amount)
                            userPreferences.saveBalance("%.2f".format(newBalance))
                            val transaction = transactionManager.createSendMoneyTransaction(
                                amount = amount,
                                recipientName = name,
                                recipientPhone = phone,
                                note = note
                            )
                            transactionManager.addTransaction(transaction)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("transaction_history") {
                TransactionHistoryScreen(
                    transactionManager = transactionManager,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun calculateFee(amount: Double): Double {
    return when {
        amount <= 50 -> 1.0
        amount <= 100 -> 2.0
        amount <= 200 -> 4.0
        amount <= 500 -> 7.0
        amount <= 1000 -> 10.0
        amount <= 2000 -> 15.0
        amount <= 5000 -> 25.0
        amount <= 10000 -> 40.0
        else -> amount * 0.005
    }
}
