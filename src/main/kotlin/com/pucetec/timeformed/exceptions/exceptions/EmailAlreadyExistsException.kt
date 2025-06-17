package com.pucetec.timeformed.exceptions.exceptions

class EmailAlreadyExistsException(email: String) : RuntimeException("El correo $email ya está registrado")
