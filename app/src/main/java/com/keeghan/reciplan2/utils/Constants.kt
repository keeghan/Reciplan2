package com.keeghan.reciplan2.utils


object Constants {
    const val SUNDAY_ID = 1
    const val MONDAY_ID = 2
    const val TUESDAY_ID = 3
    const val WEDNESDAY_ID = 4
    const val THURSDAY_ID = 5
    const val FRIDAY_ID = 6
    const val SATURDAY_ID = 7

    //Variable to check if application is run the first time
    const val IS_FIRST_RUN = "com.keeghan.reciplan2.firstrun"

    //  const val IS_FIRST_RUN_EXPLORE = "com.keeghan.reciplan2.firstrun_explore"
    const val IS_FIRST_RUN_PLAN = "com.keeghan.reciplan2.firstrun_plan"

    //potential variables for updating recipes in different versions
    //   const val IS_VERSION_TWO_UPDATE = "com.keeghan.reciplan2.version2update"
    //  const val IS_VERSION_THREE_UPDATE = "com.keeghan.reciplan2.version3update"

    const val BREAKFAST = "breakfast"
    const val LUNCH = "lunch"
    const val DINNER = "dinner"
    const val SNACK = "snack"
    const val MISSING_MEAL_PLAN = "Missing Meal Plan"

    const val NO_BREAKFAST = 0
    const val NO_LUNCH = 1
    const val NO_DINNER = 2

    const val SUCCESS = "success"

    const val EXPLORE = "Explore"
    const val COLLECTION = "Collection"
    const val FAVORTIE = "Favorite"

    fun generateUniqueId(): Int {
        return kotlin.math.abs(System.currentTimeMillis().toInt())
    }
}
