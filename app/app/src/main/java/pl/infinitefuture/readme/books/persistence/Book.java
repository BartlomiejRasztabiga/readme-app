package pl.infinitefuture.readme.books.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.Date;
import java.util.Random;

import pl.infinitefuture.readme.util.MaterialColors;

@Entity(tableName = "books")
public final class Book {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "book_id")
    private Long id;

    @Nullable
    @ColumnInfo(name = "title")
    private String title;

    @Nullable
    @ColumnInfo(name = "firstPage")
    private Long firstPage;

    @Nullable
    @ColumnInfo(name = "lastPage")
    private Long lastPage;

    @Nullable
    @ColumnInfo(name = "readPages")
    private Long readPages;

    @Nullable
    @ColumnInfo(name = "startDate")
    private Date startDate;

    @Nullable
    @ColumnInfo(name = "endDate")
    private Date deadlineDate;

    @ColumnInfo(name = "iconColor")
    @NonNull
    private Integer iconColor;

    @ColumnInfo(name = "completed")
    private boolean completed;

    @Ignore
    public Book() {
        title = "";
        firstPage = 0L;
        lastPage = 0L;
        readPages = 0L;
        iconColor = getMaterialColor();
    }

    @Ignore
    public Book(@Nullable String title, @Nullable Long lastPage) {
        this.title = title;
        this.lastPage = lastPage;
        this.iconColor = getMaterialColor();
    }

    @Ignore
    public Book(@NonNull Long id, @Nullable String title, @Nullable Long lastPage) {
        this.id = id;
        this.title = title;
        this.lastPage = lastPage;
        completed = false;
        this.iconColor = getMaterialColor();
    }

    @Ignore
    public Book(@NonNull Long id, @Nullable String title, @Nullable Long lastPage,
                boolean completed) {
        this.id = id;
        this.title = title;
        this.lastPage = lastPage;
        this.completed = completed;
        this.iconColor = getMaterialColor();
    }

    @Ignore
    public Book(@Nullable String title, @Nullable Long firstPage, @Nullable Long lastPage,
                @Nullable Date startDate, @Nullable Date deadlineDate) {
        this.title = title;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.iconColor = getMaterialColor();
    }

    @Ignore
    public Book(Long id, @Nullable String title, @Nullable Long lastPage,
                @Nullable Date startDate, @Nullable Date deadlineDate, boolean completed,
                @NonNull Integer iconColor, @Nullable Long readPages) {
        this.id = id;
        this.title = title;
        this.lastPage = lastPage;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.completed = completed;
        this.iconColor = iconColor;
        this.readPages = readPages;
    }

    @Ignore
    public Book(Long id, @Nullable String title, @Nullable Long lastPage,
                @Nullable Long readPages, boolean completed) {
        this.id = id;
        this.title = title;
        this.lastPage = lastPage;
        this.readPages = readPages;
        this.completed = completed;
        this.iconColor = getMaterialColor();
    }

    public Book(Long id, @Nullable String title, @Nullable Long firstPage, @Nullable Long lastPage,
                @Nullable Long readPages, @Nullable Date startDate,
                @Nullable Date deadlineDate, boolean completed, Integer iconColor) {
        this.id = id;
        this.title = title;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.readPages = readPages;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.completed = completed;
        this.iconColor = iconColor;
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
    public Long getFirstPage() {
        return firstPage;
    }

    @Nullable
    public Long getLastPage() {
        return lastPage;
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

    @NonNull
    public Integer getIconColor() {
        return iconColor;
    }

    public String getFirstTitleLetter() {
        if (this.title == null) return "";
        return this.title.substring(0, 1);
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

    public void setIconColor(@NonNull Integer iconColor) {
        this.iconColor = iconColor;
    }

    public void addReadPages(Long pages) {
        if (this.readPages == null) this.readPages = 0L;
        this.readPages += pages;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(title) ||
                (lastPage == null || lastPage == 0L) ||
                (startDate == null) || (deadlineDate == null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equal(id, book.id) &&
                Objects.equal(title, book.title) &&
                Objects.equal(lastPage, book.lastPage) &&
                Objects.equal(readPages, book.readPages) &&
                Objects.equal(startDate, book.startDate) &&
                Objects.equal(deadlineDate, book.deadlineDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, lastPage);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", lastPage=" + lastPage +
                ", readPages=" + readPages +
                ", startDate=" + startDate +
                ", deadlineDate=" + deadlineDate +
                ", iconColor=" + iconColor +
                ", completed=" + completed +
                '}';
    }

    private int getMaterialColor() {
        String[] materialColors = MaterialColors.getMaterialColors();

        return Color.parseColor(materialColors[new Random().nextInt(materialColors.length)]);
    }
}
