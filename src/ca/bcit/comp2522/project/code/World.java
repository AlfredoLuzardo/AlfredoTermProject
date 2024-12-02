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

    /**
     * Constructor for World
     *
     * @param countryMap is the countryMap
     */
    public World(final Map<String, Country> countryMap)
    {
        validateCountryMap(countryMap);
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

    /*
     * Validation method for countryMap
     *
     * @param countryMap is the countryMap
     * @throws IllegalArgumentException if it doesn't meet the requirements
     */
    private static void validateCountryMap(final Map<String, Country> countryMap)
    {
        if(countryMap == null)
        {
            throw new IllegalArgumentException("Country map is null");
        }
    }
}
