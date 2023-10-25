package com.nickperov.stud.micro_notes_quarkus.api;

import java.util.UUID;

public interface Note {

    UUID getId();

    String getText();
}
