package com.nickperov.stud;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nickperov.stud.micro_notes_quarkus.api.Note;
import com.nickperov.stud.micro_notes_quarkus.api.NoteDTO;
import com.nickperov.stud.micro_notes_quarkus.service.NotesServiceImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class NotesApplicationTest {

  private static final String BASE_URL = "/api/notes";

  @Inject
  NotesServiceImpl notesService;

  @AfterEach
  public void cleanup() {
    notesService.cleanUp();
  }

  @Test
  public void testCreateNote() {
    final var noteText = "Test note one";
    final var note = createNoteSuccess(noteText);
    assertEquals(noteText, note.getText());
    assertNotNull(note.getId());

    final var createdNote = getNoteSuccess(note.getId());
    assertEquals(noteText, createdNote.getText());
  }

  @Test
  public void testUpdateNote() {
    final var originalText = "Test note one";
    final var note = createNoteSuccess(originalText);
    assertEquals(originalText, note.getText());
    assertNotNull(note.getId());
    final var updatedText = "Test note one modified";
    final var updNote = constructNote(note.getId(), updatedText);
    updateNoteSuccess(updNote);
    final var updatedNote = getNoteSuccess(note.getId());
    assertEquals(note.getId(), updatedNote.getId());
    assertEquals(updatedText, updatedNote.getText());
  }

  @Test
  public void testUpdateNoteBadRequest() {
    final var originalText = "Test note 123456789";
    createNoteSuccess(originalText);

    final var updNote = constructNote(UUID.randomUUID(), null);
    updateNoteBadRequest(updNote);
  }

  @Test
  public void testAddNoteBadRequest() {
    final var note = constructNote(UUID.randomUUID(), "Some text 1234567");
    updateNoteBadUrl(note);
  }

  @Test
  public void testAddNote() {
    final var text = "Test note add 123";
    final var id = UUID.randomUUID();
    final var note = constructNote(id, text);
    final var noteLocation = addNoteSuccess(note);
    final var newNote = getNoteSuccess(URI.create(noteLocation));
    assertEquals(note.getId(), newNote.getId());
    assertEquals(text, newNote.getText());
  }

  @Test
  public void testCreateMultipleNotes() throws Exception {
    final var noteOneText = "Test note one";
    final var noteTwoText = "Test note two";
    final var noteThreeText = "Test note three";
    final var noteFourText = "Test note four";
    // Test sort
    createNoteSuccess(noteOneText);
    Thread.sleep(10);
    createNoteSuccess(noteTwoText);
    Thread.sleep(10);
    createNoteSuccess(noteThreeText);
    Thread.sleep(10);
    createNoteSuccess(noteFourText);

    final var allNotes = getNotesSuccess();
    assertEquals(4, allNotes.size());
    assertEquals(noteFourText, allNotes.get(0).getText());
    assertEquals(noteThreeText, allNotes.get(1).getText());
    assertEquals(noteTwoText, allNotes.get(2).getText());
    assertEquals(noteOneText, allNotes.get(3).getText());
  }

  @Test
  public void testUpdateMultipleNotes() throws Exception {

    final var noteOneText = "Test note one";
    final var noteTwoText = "Test note two";
    final var noteThreeText = "Test note three";
    final var noteFourText = "Test note four";
    // Test sort
    createNoteSuccess(noteOneText);
    Thread.sleep(10);
    final var noteTwo = createNoteSuccess(noteTwoText);
    Thread.sleep(10);
    final var noteThree = createNoteSuccess(noteThreeText);
    Thread.sleep(10);
    createNoteSuccess(noteFourText);

    final var noteTwoUpdText = "Test note two update 222";
    final var noteThreeUpdText = "Test note three update 333";

    Thread.sleep(10);
    updateNoteSuccess(constructNote(noteTwo.getId(), noteTwoUpdText));
    Thread.sleep(10);
    updateNoteSuccess(constructNote(noteThree.getId(), noteThreeUpdText));

    final var allNotes = getNotesSuccess();
    assertEquals(4, allNotes.size());
    assertEquals(noteThreeUpdText, allNotes.get(0).getText());
    assertEquals(noteTwoUpdText, allNotes.get(1).getText());
    assertEquals(noteFourText, allNotes.get(2).getText());
    assertEquals(noteOneText, allNotes.get(3).getText());
  }

  @Test
  public void testDeleteNote() {
    final var noteOne = "Note text 123456789";

    final var note = createNoteSuccess(noteOne);
    final var noteId = note.getId();
    final var createdNote = getNoteSuccess(noteId);
    assertNotNull(createdNote);
    deleteNoteSuccess(noteId);
    getNoteNotFound(noteId);
  }

  @Test
  public void testDeleteMultipleNotes() {
    final var noteOne = "Note text 111";
    final var noteTwo = "Note text 222";
    final var noteThree = "Note text 333";
    final var noteFour = "Note text 444";
    final var noteFive = "Note text 555";

    final var allNotes = new String[]{noteOne, noteTwo, noteThree, noteFour, noteFive};
    final var allNoteIds = Arrays.stream(allNotes).map(this::createNoteSuccessNoException).map(Note::getId).toList();

    final var notesListInit = getNotesSuccess();
    assertNotNull(notesListInit);
    assertEquals(5, notesListInit.size());
    assertTrue(notesListInit.stream().map(NoteDTO::getText).allMatch(noteText -> new HashSet<>(List.of(allNotes)).contains(noteText)));

    deleteNoteSuccess(allNoteIds.get(3));
    deleteNoteSuccess(allNoteIds.get(4));

    final var notesListThreeElements = getNotesSuccess();
    assertNotNull(notesListThreeElements);
    assertEquals(3, notesListThreeElements.size());
    assertTrue(notesListThreeElements.stream().map(NoteDTO::getText).anyMatch(text -> text.equals(noteOne)));
    assertTrue(notesListThreeElements.stream().map(NoteDTO::getText).anyMatch(text -> text.equals(noteTwo)));
    assertTrue(notesListThreeElements.stream().map(NoteDTO::getText).anyMatch(text -> text.equals(noteThree)));

    deleteNoteSuccess(allNoteIds.get(0));
    deleteNoteSuccess(allNoteIds.get(1));
    deleteNoteSuccess(allNoteIds.get(2));

    final var notesListEmpty = getNotesSuccess();
    assertNotNull(notesListEmpty);
    assertTrue(notesListEmpty.isEmpty());
  }

  @Test
  public void testDeleteNoteNotFound() {
    deleteNoteNotFound(UUID.randomUUID());
  }

  @Test
  public void testModelMapping() {
    final var id = UUID.randomUUID();
    final var text = "Test text";
    final var noteDTO = constructNote(id, text);
    assertEquals(id, noteDTO.getId());
    assertEquals(text, noteDTO.getText());
    assertNotNull(noteDTO.toString());
    assertTrue(noteDTO.toString().startsWith("NoteDTO"));
  }

  @Test
  public void testServiceModel() {
    final var text = "Test text";
    final var noteOne = notesService.createNote(text);
    assertNotNull(noteOne);
    assertEquals(text, noteOne.getText());
    assertNotNull(noteOne.toString());
    assertTrue(noteOne.toString().startsWith("NoteImpl"));

    final var noteTwo = notesService.createNote(text);
    assertNotEquals(noteOne, noteTwo);
    assertNotEquals(noteOne, null);

    final Set<Note> notesSet = new HashSet<>();
    notesSet.add(noteOne);
    notesSet.add(noteTwo);
    assertEquals(2, notesSet.size());
  }

  private NoteDTO constructNote(final UUID id, final String text) {
    return new NoteDTO(new Note() {
      @Override
      public UUID getId() {
        return id;
      }

      @Override
      public String getText() {
        return text;
      }
    });
  }

  private NoteDTO getNoteSuccess(final UUID id) {
    return getNoteSuccess(URI.create(BASE_URL + '/' + id.toString()));
  }

  private NoteDTO getNoteSuccess(final URI uri) {
    return given().when().contentType(ContentType.JSON).get(uri)
        .then()
        .statusCode(200)
        .extract().body().as(NoteDTO.class);
  }

  private void getNoteNotFound(final UUID id) {
    performGetNote(id).then().statusCode(404);
  }

  private List<NoteDTO> getNotesSuccess() {
    return given().contentType(ContentType.JSON).get(BASE_URL)
        .then().statusCode(200)
        .extract().body().as(new TypeRef<>() {
        });
  }

  private Note createNoteSuccess(final String noteText) {
    return given()
        .when().body(noteText).post(BASE_URL)
        .then()
        .statusCode(200)
        .extract().body().as(NoteDTO.class);
  }

  private Note createNoteSuccessNoException(final String noteText) {
    try {
      return createNoteSuccess(noteText);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void updateNoteSuccess(final NoteDTO note) {
    performUpdateNote(note).then().statusCode(200);
  }

  private void updateNoteBadRequest(final NoteDTO note) {
    performUpdateNote(note).then().statusCode(400);
  }

  private void updateNoteBadUrl(final NoteDTO note) {
    performUpdateNote(note, BASE_URL + '/' + UUID.randomUUID()).then().statusCode(400);
  }

  private String addNoteSuccess(final NoteDTO note) {
    return given().contentType(ContentType.JSON).body(note).put(BASE_URL + '/' + note.getId())
        .then().statusCode(201)
        .extract().header("Location");
  }

  private void deleteNoteSuccess(final UUID id) {
    performNoteDelete(id).then().statusCode(204);
  }

  private void deleteNoteNotFound(final UUID id) {
    performNoteDelete(id).then().statusCode(404);
  }

  private Response performGetNote(final UUID id) {
    return given().when().contentType(ContentType.JSON).get(BASE_URL + '/' + id.toString());
  }

  private Response performUpdateNote(final NoteDTO note) {
    return performUpdateNote(note, BASE_URL + '/' + note.getId());
  }

  private Response performUpdateNote(final NoteDTO note, final String url) {
    return given().when().contentType(ContentType.JSON).body(note).put(url);
  }

  private Response performNoteDelete(final UUID id) {
    return given().delete(BASE_URL + '/' + id.toString());
  }
}
