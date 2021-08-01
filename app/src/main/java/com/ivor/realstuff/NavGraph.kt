package com.ivor.realstuff

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivor.realstuff.ui.home.HomeScreen

object Destination {
    const val ROUTE_HOME = "home"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.ROUTE_HOME,
) {
    val actions = remember(navController) {
        RouteActions(navController)
    }

    NavHost(navController, startDestination) {
        composable(Destination.ROUTE_HOME) {
            HomeScreen(selectArticle = { url ->
                actions.openArticle(url)
            })
        }
    }
}

class RouteActions(navController: NavHostController) {
    val openArticle = { url: String ->
        navController.context.startActivity(Intent().apply {
            action = ACTION_VIEW
            data = Uri.parse(url)
        })
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED