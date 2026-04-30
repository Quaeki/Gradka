package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow

class GetAllNoteUseCase(private val repository: GradkaRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}