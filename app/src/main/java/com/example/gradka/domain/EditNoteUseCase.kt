package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для редактирования существующей заметки пользователя. */
class EditNoteUseCase @Inject constructor(
    private val repository: GradkaRepository
) {
    suspend operator fun invoke(note: Note){
        repository.editNote(note)
    }
}
