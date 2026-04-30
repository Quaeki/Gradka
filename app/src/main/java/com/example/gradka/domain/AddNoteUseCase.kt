package com.example.gradka.domain

import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: GradkaRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String
    ){
        repository.addNote(title, content)
    }
}
