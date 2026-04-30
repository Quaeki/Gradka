package com.example.gradka

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gradka.ui.components.BottomNavBar
import com.example.gradka.ui.screens.*
import com.example.gradka.ui.theme.LocalAppColors
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp

val mainTabs = setOf("home", "catalog", "fav", "cart", "profile")

@Composable
fun AppNavigation(vm: AppViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current.applicationContext as Application))) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "onboarding"
    val colors = LocalAppColors.current

    val showBottomBar = currentRoute in mainTabs

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = "auth",
                enterTransition = { sharedScreenEnterTransition() },
                exitTransition = { sharedScreenExitTransition() },
                popEnterTransition = { sharedScreenPopEnterTransition() },
                popExitTransition = { sharedScreenPopExitTransition() },
            ) {
                composable("auth") {
                    AuthScreen(onAuthDone = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    })
                }

                composable("onboarding") {
                    OnboardingScreen(onFinish = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    })
                }

                composable("home") {
                    HomeScreen(
                        vm = vm,
                        onOpenProduct = { id -> navController.navigate("product/$id") },
                        onNavigate = { navController.navigateTab(it) },
                    )
                }

                composable("catalog") {
                    CatalogScreen(
                        vm = vm,
                        onOpenProduct = { id -> navController.navigate("product/$id") },
                    )
                }

                composable("search") {
                    SearchScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onOpenProduct = { id -> navController.navigate("product/$id") },
                    )
                }

                composable("product/{productId}") { back ->
                    val productId = back.arguments?.getString("productId") ?: return@composable
                    ProductScreen(
                        productId = productId,
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onGoCart = { navController.navigateTab("cart") },
                    )
                }

                composable("fav") {
                    FavScreen(
                        vm = vm,
                        onOpenProduct = { id -> navController.navigate("product/$id") },
                        onAddList = { navController.navigate("add_list") },
                        onOpenList = { id -> navController.navigate("list/$id") },
                    )
                }

                composable("add_list") {
                    AddListScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                    )
                }

                composable("list/{noteId}") { back ->
                    val noteId = back.arguments?.getString("noteId")?.toIntOrNull()
                        ?: return@composable
                    ListDetailScreen(
                        noteId = noteId,
                        vm = vm,
                        onBack = { navController.popBackStack() },
                    )
                }

                composable("cart") {
                    CartScreen(
                        vm = vm,
                        onCheckout = { navController.navigate("checkout") },
                        onOpenProduct = { id -> navController.navigate("product/$id") },
                        onCatalog = { navController.navigateTab("catalog") },
                    )
                }

                composable("checkout") {
                    CheckoutScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onPay = {
                            vm.clearCart()
                            navController.navigate("success") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        onAddress = { navController.navigate("address") },
                    )
                }

                composable("tracking") {
                    TrackingScreen(onBack = { navController.popBackStack() })
                }

                composable("address") {
                    AddressScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onSave = { navController.popBackStack() },
                        onAddManual = { navController.navigate("manual_address") },
                    )
                }

                composable("manual_address") {
                    ManualAddressScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        onNavigate = { route ->
                            when (route) {
                                "orders" -> navController.navigate("orders")
                                "address" -> navController.navigate("address")
                                "support" -> navController.navigate("support")
                                else -> {}
                            }
                        },
                        onLogout = {
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    )
                }

                composable("support") {
                    SupportChatScreen(onBack = { navController.popBackStack() })
                }

                composable("orders") {
                    OrdersScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onTracking = { navController.navigate("tracking") },
                    )
                }

                composable("recipes") {
                    RecipesScreen(
                        vm = vm,
                        onCartNavigate = { navController.navigateTab("cart") },
                    )
                }

                composable("success") {
                    SuccessScreen(
                        onTracking = {
                            navController.navigate("tracking") {
                                popUpTo("success") { inclusive = true }
                            }
                        },
                        onHome = {
                            navController.navigate("home") {
                                popUpTo("success") { inclusive = true }
                            }
                        },
                    )
                }
            }
        }

        if (showBottomBar) {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { navController.navigateTab(it) },
                cartCount = vm.cartCount,
                colors = colors,
            )
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private const val SCREEN_TRANSITION_DURATION_MS = 260

private fun sharedScreenEnterTransition(): EnterTransition =
    slideInHorizontally(animationSpec = tween(SCREEN_TRANSITION_DURATION_MS)) { fullWidth ->
        fullWidth
    }

private fun sharedScreenExitTransition(): ExitTransition =
    slideOutHorizontally(animationSpec = tween(SCREEN_TRANSITION_DURATION_MS)) { fullWidth ->
        -fullWidth
    }

private fun sharedScreenPopEnterTransition(): EnterTransition =
    slideInHorizontally(animationSpec = tween(SCREEN_TRANSITION_DURATION_MS)) { fullWidth ->
        -fullWidth
    }

private fun sharedScreenPopExitTransition(): ExitTransition =
    slideOutHorizontally(animationSpec = tween(SCREEN_TRANSITION_DURATION_MS)) { fullWidth ->
        fullWidth
    }
