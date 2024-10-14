package net.azeti.challenge.recipe

import net.azeti.challenge.recipe.security.auth.JwtAuthorizationHeader.TOKEN_HEADER
import net.azeti.challenge.recipe.security.auth.JwtAuthorizationHeader.TOKEN_PREFIX
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

fun MockMvc.getRequest(
    uri: String,
    params: List<Any> = emptyList(),
    jwt: String? = null,
): ResultActions {
    val request = get(uri, *params.toTypedArray())
    jwt?.let { request.header(TOKEN_HEADER, "$TOKEN_PREFIX $jwt") }
    return perform(request).andDo(MockMvcResultHandlers.print())
}

fun MockMvc.postRequest(
    uri: String,
    params: List<Any> = emptyList(),
    body: String,
    jwt: String? = null,
): ResultActions {
    val request =
        post(uri, *params.toTypedArray())
            .content(body).contentType(MediaType.APPLICATION_JSON)
    jwt?.let { request.header(TOKEN_HEADER, "$TOKEN_PREFIX $jwt") }
    return perform(request).andDo(MockMvcResultHandlers.print())
}

fun MockMvc.putRequest(
    uri: String,
    params: List<Any> = emptyList(),
    body: String,
    jwt: String? = null,
): ResultActions {
    val request =
        put(uri, *params.toTypedArray())
            .content(body).contentType(MediaType.APPLICATION_JSON)
    jwt?.let { request.header(TOKEN_HEADER, "$TOKEN_PREFIX $jwt") }
    return perform(request).andDo(MockMvcResultHandlers.print())
}

fun MockMvc.deleteRequest(
    uri: String,
    params: List<Any> = emptyList(),
    jwt: String,
): ResultActions {
    val request = delete(uri, *params.toTypedArray())
    request.header(TOKEN_HEADER, "$TOKEN_PREFIX $jwt")
    return perform(request).andDo(MockMvcResultHandlers.print())
}
