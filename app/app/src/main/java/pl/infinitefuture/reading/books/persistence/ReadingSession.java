package pl.infinitefuture.reading.books.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.common.base.Objects;

import java.util.Date;

@Entity(tableName = "readingSessions")
public class ReadingSession {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    private Long id;

    @ColumnInfo(name = "book_id")
    private Long bookId;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "pages")
    private Long readPages;

    public ReadingSession(Long id, Long bookId, Date date, Long readPages) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
        this.readPages = readPages;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public Date getDate() {
        return date;
    }

    public Long getReadPages() {
        return readPages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setReadPages(Long readPages) {
        this.readPages = readPages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadingSession that = (ReadingSession) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (bookId != null ? !bookId.equals(that.bookId) : that.bookId != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return readPages != null ? readPages.equals(that.readPages) : that.readPages == null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, bookId, date, readPages);
    }

    @Override
    public String toString() {
        return "ReadingSession{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", date=" + date +
                ", readPages=" + readPages +
                '}';
    }
}
