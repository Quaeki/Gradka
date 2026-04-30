package com.example.gradka.domain

import javax.inject.Inject

class EditNoteUseCase @Inject constructor(
    private val repository: GradkaRepository
) {
    suspend operator fun invoke(note: Note){
        repository.editNote(note)
    }
}
