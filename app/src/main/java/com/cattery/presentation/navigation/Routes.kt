package com.cattery.presentation.navigation

object Routes {
    const val NO_ID = -1L

    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val RESERVATIONS = "reservations"
    const val CAT_FEMALES = "cat_females"
    const val CAT_FEMALE_DETAIL = "cat_females/{id}"
    const val CAT_MALES = "cat_males"
    const val CAT_MALE_DETAIL = "cat_males/{id}"
    const val LITTERS = "litters"
    const val LITTER_DETAIL = "litters/{id}"
    const val LITTER_KITTENS = "litters/{litterId}/kittens"
    const val KITTEN_DETAIL = "kittens/{id}"
    const val FORM = "form/{entityType}/{entityId}/{litterId}"

    fun catFemaleDetail(id: Long) = "cat_females/$id"
    fun catMaleDetail(id: Long) = "cat_males/$id"
    fun litterDetail(id: Long) = "litters/$id"
    fun litterKittens(litterId: Long) = "litters/$litterId/kittens"
    fun kittenDetail(id: Long) = "kittens/$id"
    fun form(entityType: String, entityId: Long = NO_ID, litterId: Long = NO_ID) =
        "form/$entityType/$entityId/$litterId"
}
