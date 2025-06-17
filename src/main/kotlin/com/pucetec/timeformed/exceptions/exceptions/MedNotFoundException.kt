package com.pucetec.timeformed.exceptions.exceptions

class MedNotFoundException(id: Long) :
    RuntimeException("No se encontró la medicina con ID $id")
