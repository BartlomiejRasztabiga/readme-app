package pl.infinitefuture.reading.books.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.Date;

@Entity(tableName = "books")
public final class Book {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "book_id")
    private Long id;

    @Nullable
    @ColumnInfo(name = "title")
    private String title;

    @Nullable
    @ColumnInfo(name = "totalPages")
    private Long totalPages;

    @Nullable
    @ColumnInfo(name = "readPages")
    private Long readPages;

    @Nullable
    @ColumnInfo(name = "startDate")
    private Date startDate;

    @Nullable
    @ColumnInfo(name = "endDate")
    private Date deadlineDate;

    @ColumnInfo(name = "completed")
    private boolean completed;

    @Ignore
    public Book() {
        title = "";
        totalPages = 0L;
        readPages = 0L;
    }

    @Ignore
    public Book(@Nullable String title, @Nullable Long totalPages) {
        this.title = title;
        this.totalPages = totalPages;
    }

    @Ignore
    public Book(@NonNull Long id, @Nullable String title, @Nullable Long totalPages) {
        this.id = id;
        this.title = title;
        this.totalPages = totalPages;
        completed = false;
    }

    @Ignore
    public Book(@NonNull Long id, @Nullable String title, @Nullable Long totalPages,
                boolean completed) {
        this.id = id;
        this.title = title;
        this.totalPages = totalPages;
        this.completed = completed;
    }

    public Book(Long id, @Nullable String title, @Nullable Long totalPages,
                @Nullable Long readPages, boolean completed) {
        this.id = id;
        this.title = title;
        this.totalPages = totalPages;
        this.readPages = readPages;
        this.completed = completed;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public Long getTotalPages() {
        return totalPages;
    }

    @Nullable
    public Long getReadPages() {
        return readPages;
    }

    @Nullable
    public Date getStartDate() {
        return startDate;
    }

    @Nullable
    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public boolean isActive() { return !isCompleted();}

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setStartDate(@Nullable Date startDate) {
        this.startDate = startDate;
    }

    public void setDeadlineDate(@Nullable Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(title) &&
                (totalPages == null || totalPages == 0L);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equal(id, book.id) &&
                Objects.equal(title, book.title) &&
                Objects.equal(totalPages, book.totalPages) &&
                Objects.equal(readPages, book.readPages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, totalPages);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", totalPages=" + totalPages +
                ", readPages=" + readPages +
                ", completed=" + completed +
                '}';
    }
}
