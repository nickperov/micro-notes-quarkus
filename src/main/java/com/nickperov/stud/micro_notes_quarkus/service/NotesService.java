package com.nickperov.stud.micro_notes_quarkus.service;

import com.nickperov.stud.micro_notes_quarkus.api.Note;
import java.util.List;
import java.util.UUID;

public interface NotesService {

  Note getNote(UUID id);

  List<? extends Note> getAllNotes();

  Note createNote(String text);

  boolean updateNote(Note note);

  boolean deleteNote(UUID id);
}
