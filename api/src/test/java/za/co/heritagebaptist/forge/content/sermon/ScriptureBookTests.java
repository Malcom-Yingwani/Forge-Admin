package za.co.heritagebaptist.forge.content.sermon;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptureBookTests {
    @Test
    void containsAllSixtySixBooksInCanonicalOrder() {
        String[] expected = """
            GENESIS EXODUS LEVITICUS NUMBERS DEUTERONOMY JOSHUA JUDGES RUTH
            FIRST_SAMUEL SECOND_SAMUEL FIRST_KINGS SECOND_KINGS FIRST_CHRONICLES
            SECOND_CHRONICLES EZRA NEHEMIAH ESTHER JOB PSALMS PROVERBS ECCLESIASTES
            SONG_OF_SONGS ISAIAH JEREMIAH LAMENTATIONS EZEKIEL DANIEL HOSEA JOEL AMOS
            OBADIAH JONAH MICAH NAHUM HABAKKUK ZEPHANIAH HAGGAI ZECHARIAH MALACHI
            MATTHEW MARK LUKE JOHN ACTS ROMANS FIRST_CORINTHIANS SECOND_CORINTHIANS
            GALATIANS EPHESIANS PHILIPPIANS COLOSSIANS FIRST_THESSALONIANS
            SECOND_THESSALONIANS FIRST_TIMOTHY SECOND_TIMOTHY TITUS PHILEMON HEBREWS
            JAMES FIRST_PETER SECOND_PETER FIRST_JOHN SECOND_JOHN THIRD_JOHN JUDE REVELATION
            """.trim().split("\\s+");

        ScriptureBook[] books = ScriptureBook.values();
        assertThat(books).hasSize(66).startsWith(ScriptureBook.GENESIS).endsWith(ScriptureBook.REVELATION);
        assertThat(Arrays.stream(books).map(Enum::name).toList()).containsExactly(expected);
        assertThat(Arrays.stream(books).mapToInt(ScriptureBook::getBibleOrder).toArray())
            .containsExactly(IntStream.rangeClosed(1, 66).toArray());
        assertThat(books).allSatisfy(book -> assertThat(book.getDisplayName()).isNotBlank());
    }
}
