package com.nickperov.stud.micro_notes_quarkus.service;

import com.nickperov.stud.micro_notes_quarkus.api.Note;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class NoteImpl implements Note {

  private final UUID id;
  private final String text;
  private final Date timestamp;

  private NoteImpl(final UUID id, final String text, final Date timestamp) {
    this.id = id;
    this.text = text;
    this.timestamp = timestamp;
  }

  NoteImpl(final String text) {
    this(UUID.randomUUID(), text, new Date());
  }

  NoteImpl(final Note note) {
    this(note.getId(), note.getText(), new Date());
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public String getText() {
    return text;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final NoteImpl note = (NoteImpl) o;
    return Objects.equals(id, note.id) && Objects.equals(text, note.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, text);
  }

  @Override
  public String toString() {
    return "NoteImpl{" +
        "id=" + id +
        ", text='" + text + '\'' +
        ", timestamp=" + timestamp +
        '}';
  }
}
