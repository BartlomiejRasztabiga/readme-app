package pl.infinitefuture.reading.books.persistence;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class BookWithSessions {

    @Embedded
    public Book book;

    @Relation(parentColumn = "book_id", entityColumn = "book_id")
    public List<ReadingSession> readingSessions;
}
