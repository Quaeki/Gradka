package com.example.gradka.domain

class AddNoteUseCase(
    private val repository: GradkaRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String
    ){
        repository.addNote(title, content)
    }
}