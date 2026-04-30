package com.example.gradka.domain

class DeleteNoteUseCase(
    private val repository: GradkaRepository
){
    suspend operator fun invoke(noteId: Int){
        repository.deleteNote(noteId)
    }
}