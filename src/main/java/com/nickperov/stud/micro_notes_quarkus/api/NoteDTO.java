package com.nickperov.stud.micro_notes_quarkus.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class NoteDTO implements Note {

  @JsonCreator
  private NoteDTO(@JsonProperty("id") final UUID id, @JsonProperty("text") final String text) {
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
