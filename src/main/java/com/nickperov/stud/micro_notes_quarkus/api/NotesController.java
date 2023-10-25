package com.nickperov.stud.micro_notes_quarkus.api;


import com.nickperov.stud.micro_notes_quarkus.service.NotesService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.resteasy.reactive.RestResponse;


@Path("/api/notes")
public class NotesController {

  private final NotesService notesService;

  NotesController(final NotesService notesService) {
    this.notesService = notesService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<NoteDTO> listNotes() {
    return notesService.getAllNotes().stream().map(NoteDTO::new).collect(Collectors.toList());
  }

  @GET
  @Path("/{note_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public NoteDTO getNote(@PathParam("note_id") final UUID noteId) {
    final var note = notesService.getNote(noteId);
    if (note != null) {
      return new NoteDTO(note);
    } else {
      throw new NotFoundException();
    }
  }

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public NoteDTO createNote(final String noteText) {
    return new NoteDTO(notesService.createNote(noteText));
  }

  @PUT
  @Path("/{note_id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<?> updateNote(@Context final UriInfo uriInfo, @PathParam("note_id") final UUID noteId, final NoteDTO note) {
    if (note.getId() == null || !note.getId().equals(noteId) || note.getText() == null) {
      throw new BadRequestException();
    }
    final boolean isUpdated = notesService.updateNote(note);
    if (isUpdated) {
      return RestResponse.ok();
    } else {
      return RestResponse.created(uriInfo.getRequestUri());
    }
  }
  
  @DELETE
  @Path("/{note_id}")
  @Produces
  public RestResponse<?> deleteNote(@PathParam("note_id") final UUID noteId) {
    final var result = notesService.deleteNote(noteId);
    return result ? RestResponse.noContent() : RestResponse.notFound();
  }
}
