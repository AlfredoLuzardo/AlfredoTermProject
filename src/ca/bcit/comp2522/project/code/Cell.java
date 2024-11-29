public abstract class Cell
{
    protected boolean clicked = false;
    protected String symbol = "";

    public void reveal(final String symbol)
    {
        clicked = true;
        this.symbol = symbol;
    }

    public boolean isClicked()
    {
        return clicked;
    }

    public abstract String toString();
}
