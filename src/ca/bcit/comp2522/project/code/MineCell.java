public class MineCell extends Cell
{
    @Override
    public void reveal(String symbol)
    {
        clicked = true;
        this.symbol = "*";
    }

    @Override
    public String toString()
    {
        return symbol;
    }
}
