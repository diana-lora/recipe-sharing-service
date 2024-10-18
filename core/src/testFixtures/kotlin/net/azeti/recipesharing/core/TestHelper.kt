package net.azeti.recipesharing.core

import net.azeti.recipesharing.core.model.CustomUserDetails

fun defaultUserDetails(
    id: Long = 1,
    email: String = "email@example.com",
    username: String = "username",
    password: String = "StrongPass123!@",
) = CustomUserDetails(id = id, username = username, email = email, password = password)
