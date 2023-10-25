package com.nickperov.stud.micro_notes_quarkus.api;

import java.util.UUID;

public class NoteDTO implements Note {

  // Constructor for JSON deserialization
  private NoteDTO(final UUID id, final String text) {
    this.id = id;
    this.text = text;
  }

  public NoteDTO(final Note note) {
    this.id = note.getId();
    this.text = note.getText();
  }

  private final UUID id;

  private final String text;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return "NoteDTO{" +
        "id=" + id +
        ", text='" + text + '\'' +
        '}';
  }
}
