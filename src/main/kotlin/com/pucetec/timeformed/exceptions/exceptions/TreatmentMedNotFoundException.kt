package com.pucetec.timeformed.exceptions.exceptions

class TreatmentMedNotFoundException(id: Long) :
    RuntimeException("No se encontró la relación tratamiento-medicamento con ID $id")
