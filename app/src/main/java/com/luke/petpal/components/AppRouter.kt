package com.luke.petpal.components

class AppRouter {
    private object Route {
        const val LOGIN = "login"
        const val HOME = "home"
    }

    sealed class Screen(val route: String) {
        object Login: Screen(Route.LOGIN)
        object Home: Screen(Route.HOME)
    }
}