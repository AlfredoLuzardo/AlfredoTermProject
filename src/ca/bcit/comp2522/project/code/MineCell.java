import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * MineCell class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class MineCell extends Cell
{
    private static final int    IMAGE_HEIGHT    = 50;
    private static final int    IMAGE_WIDTH     = 50;
    private static final String MINE_IMAGE_PATH = "/resources/mine.png";
    private static final Image  MINE_IMAGE;

    static
    {
        MINE_IMAGE = new Image(MINE_IMAGE_PATH);
    }

    /**
     * Getter for imageView
     *
     * @return imageView
     */
    public ImageView getImageView()
    {
        final ImageView imageView;

        imageView = new ImageView(MINE_IMAGE);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);

        return imageView;
    }

    /**
     * Reveals the Cell
     *
     * @param symbol symbol
     */
    @Override
    public void reveal(final String symbol)
    {
        clicked = true;
        this.symbol = "*";
    }

    /**
     * Returns a cell as its string equivalent
     *
     * @return symbol
     */
    @Override
    public String toString()
    {
        return symbol;
    }
}
