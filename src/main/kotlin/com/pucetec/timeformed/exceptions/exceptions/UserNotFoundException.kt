package com.pucetec.timeformed.exceptions.exceptions

class UserNotFoundException(id: Long) : RuntimeException("Usuario con ID $id no fue encontrado")
