package com.example.gradka.domain

import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: GradkaRepository
){
    suspend operator fun invoke(noteId: Int){
        repository.deleteNote(noteId)
    }
}
