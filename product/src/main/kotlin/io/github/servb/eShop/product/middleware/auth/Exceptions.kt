package io.github.servb.eShop.product.middleware.auth

class ProblemsWithConnectionToAuthServiceException(cause: Throwable) : Throwable("Can't validate request", cause)

object InvalidAuthTokenException : Throwable()
