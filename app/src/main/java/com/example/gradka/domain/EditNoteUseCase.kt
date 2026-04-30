package com.example.gradka.domain

class EditNoteUseCase(
    private val repository: GradkaRepository
) {
    suspend operator fun invoke(note: Note){
        repository.editNote(note)
    }
}