import java.util.Map;

/**
 * World Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class World
{
    private final Map<String, Country> countryMap;

    public World(final Map<String, Country> countryMap)
    {
        this.countryMap = countryMap;
    }

    /**
     * Getter for countryMap
     *
     * @return countryMap
     */
    public Map<String,Country> getCountryMap()
    {
        return countryMap;
    }
}
