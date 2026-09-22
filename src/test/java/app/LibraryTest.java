package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryTest {

    private Library library;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        library = new Library();
        book1 = new Book("Clean Code", "Robert Martin");
        book2 = new Book("Effective Java", "Joshua Bloch");
    }

    @Test
    void addBook_shouldIncreaseCount() {
        library.addBook(book1);

        assertEquals(1, library.getBookCount());
        assertTrue(library.getBooks().contains(book1));
    }

    @Test
    void addBook_shouldAddMultipleBooks() {
        library.addBook(book1);
        library.addBook(book2);

        assertEquals(2, library.getBookCount());
        assertEquals(List.of(book1, book2), library.getBooks());
    }

    @Test
    void addBook_shouldThrowExceptionWhenBookIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> library.addBook(null)
        );

        assertEquals("Book must not be null", exception.getMessage());
        assertEquals(0, library.getBookCount());
    }

    @Test
    void removeBook_shouldRemoveExistingBook() {
        library.addBook(book1);
        library.addBook(book2);

        boolean removed = library.removeBook(book1);

        assertTrue(removed);
        assertEquals(1, library.getBookCount());
        assertFalse(library.getBooks().contains(book1));
        assertTrue(library.getBooks().contains(book2));
    }

    @Test
    void removeBook_shouldReturnFalseWhenBookNotFound() {
        library.addBook(book1);

        boolean removed = library.removeBook(book2);

        assertFalse(removed);
        assertEquals(1, library.getBookCount());
    }

    @Test
    void removeBook_shouldReturnFalseWhenBookIsNull() {
        library.addBook(book1);

        boolean removed = library.removeBook(null);

        assertFalse(removed);
        assertEquals(1, library.getBookCount());
    }

    @Test
    void getBooks_shouldReturnEmptyListWhenNoBooks() {
        assertTrue(library.getBooks().isEmpty());
        assertEquals(0, library.getBookCount());
    }

    @Test
    void getBooks_shouldReturnCopyThatDoesNotAffectLibrary() {
        library.addBook(book1);

        List<Book> books = library.getBooks();

        assertThrows(UnsupportedOperationException.class, () -> books.add(book2));
        assertEquals(1, library.getBookCount());
    }

    @Test
    void getBookCount_shouldReturnZeroForNewLibrary() {
        assertEquals(0, library.getBookCount());
    }

    @Test
    void book_shouldThrowExceptionWhenTitleIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Book(null, "Author"));
        assertThrows(IllegalArgumentException.class, () -> new Book("  ", "Author"));
    }

    @Test
    void book_shouldThrowExceptionWhenAuthorIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Book("Title", null));
        assertThrows(IllegalArgumentException.class, () -> new Book("Title", "  "));
    }

    @Test
    void book_equalsAndHashCode_shouldWorkByTitleAndAuthor() {
        Book sameBook = new Book("Clean Code", "Robert Martin");
        Book otherBook = new Book("Clean Code", "Another Author");

        assertEquals(book1, sameBook);
        assertEquals(book1.hashCode(), sameBook.hashCode());
        assertFalse(book1.equals(otherBook));
    }
}
