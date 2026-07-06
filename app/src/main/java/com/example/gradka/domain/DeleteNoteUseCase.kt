package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для удаления заметки пользователя по идентификатору. */
class DeleteNoteUseCase @Inject constructor(
    private val repository: GradkaRepository
){
    suspend operator fun invoke(noteId: Int){
        repository.deleteNote(noteId)
    }
}
