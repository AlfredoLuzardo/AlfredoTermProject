/**
 * Abstract Cell class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public abstract class Cell
{
    private static final int MAX_SIZE_SYMBOL       = 1;
    private static final String DEFAULT_SYMBOL     = "";
    private static final String ILLEGAL_SYMBOL_MSG = "symbol cannot be null or greater than max length";

    protected boolean clicked;
    protected String  symbol;

    /**
     * Constructor for Cell
     */
    public Cell()
    {
        clicked = false;
        symbol = DEFAULT_SYMBOL;
    }

    /**
     * Getter for clicked
     *
     * @return clicked
     */
    public boolean isClicked()
    {
        return clicked;
    }

    /**
     * Reveals the Cell
     *
     * @param symbol symbol
     */
    public void reveal(final String symbol)
    {
        validateSymbol(symbol);
        clicked = true;
        this.symbol = symbol;
    }

    /**
     * Returns a cell as its string equivalent
     *
     * @return symbol
     */
    public abstract String toString();

    /*
     * Validation for Symbol
     *
     * @throws IllegalArgumentException if symbol does not meet requirements
     */
    private static void validateSymbol(final String symbol)
    {
        if(symbol == null || symbol.length() > MAX_SIZE_SYMBOL)
        {
            throw new IllegalArgumentException(ILLEGAL_SYMBOL_MSG);
        }
    }
}
