package com.pucetec.timeformed.exceptions.exceptions

class TreatmentNotFoundException(id: Long) :
    RuntimeException("Tratamiento con ID $id no fue encontrado")
