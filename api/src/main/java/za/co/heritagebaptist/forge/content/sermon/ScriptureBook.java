package za.co.heritagebaptist.forge.content.sermon;

public enum ScriptureBook {
    GENESIS("Gen"),
    EXODUS("Exod"),
    LEVITICUS("Lev"),
    NUMBERS("Num"),
    DEUTERONOMY("Deut"),
    JOSHUA("Josh"),
    JUDGES("Judg"),
    RUTH("Ruth"),
    FIRST_SAMUEL("1 Sam"),
    SECOND_SAMUEL("2 Sam"),
    FIRST_KINGS("1 Kgs"),
    SECOND_KINGS("2 Kgs"),
    FIRST_CHRONICLES("1 Chr"),
    SECOND_CHRONICLES("2 Chr"),
    EZRA("Ezra"),
    NEHEMIAH("Neh"),
    ESTHER("Esth"),
    JOB("Job"),
    PSALMS("Ps"),
    PROVERBS("Prov"),
    ECCLESIASTES("Eccl"),
    SONG_OF_SONGS("Song"),
    ISAIAH("Isa"),
    JEREMIAH("Jer"),
    LAMENTATIONS("Lam"),
    EZEKIEL("Ezek"),
    DANIEL("Dan"),
    HOSEA("Hos"),
    JOEL("Joel"),
    AMOS("Amos"),
    OBADIAH("Obad"),
    JONAH("Jonah"),
    MICAH("Mic"),
    NAHUM("Nah"),
    HABAKKUK("Hab"),
    ZEPHANIAH("Zeph"),
    HAGGAI("Hag"),
    ZECHARIAH("Zech"),
    MALACHI("Mal"),
    MATTHEW("Matt"),
    MARK("Mark"),
    LUKE("Luke"),
    JOHN("John"),
    ACTS("Acts"),
    ROMANS("Rom"),
    FIRST_CORINTHIANS("1 Cor"),
    SECOND_CORINTHIANS("2 Cor"),
    GALATIANS("Gal"),
    EPHESIANS("Eph"),
    PHILIPPIANS("Phil"),
    COLOSSIANS("Col"),
    FIRST_THESSALONIANS("1 Thess"),
    SECOND_THESSALONIANS("2 Thess"),
    FIRST_TIMOTHY("1 Tim"),
    SECOND_TIMOTHY("2 Tim"),
    TITUS("Titus"),
    PHILEMON("Phlm"),
    HEBREWS("Heb"),
    JAMES("Jas"),
    FIRST_PETER("1 Pet"),
    SECOND_PETER("2 Pet"),
    FIRST_JOHN("1 John"),
    SECOND_JOHN("2 John"),
    THIRD_JOHN("3 John"),
    JUDE("Jude"),
    REVELATION("Rev");

    private final String displayName;

    ScriptureBook(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBibleOrder() {
        return ordinal() + 1;
    }
}
