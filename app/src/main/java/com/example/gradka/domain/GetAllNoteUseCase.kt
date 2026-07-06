package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use Case для получения реактивного потока со всеми заметками пользователя. */
class GetAllNoteUseCase @Inject constructor(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}
