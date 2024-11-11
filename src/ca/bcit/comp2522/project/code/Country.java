/**
 * Country Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class Country
{
    private String name;
    private String capitalCityName;
    private String[] facts;

    /**
     * Country Constructor
     *
     * @param name name
     * @param capitalCityName capitalCityName
     * @param facts facts
     */
    public Country(final String name, final String capitalCityName, final String[] facts)
    {
        validateName(name);
        validateName(capitalCityName);

        this.name = name;
        this.capitalCityName = capitalCityName;
        this.facts = facts;
    }

    /**
     * Accessor for name
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Accessor for capitalCityName
     *
     * @return capitalCityName
     */
    public String getCapitalCityName()
    {
        return capitalCityName;
    }

    /**
     * Accessor for facts
     *
     * @return facts
     */
    public String[] getFacts()
    {
        return facts;
    }

    /**
     * Setter for name
     *
     * @param name name
     */
    private void setName(String name)
    {
        this.name = name;
    }

    /**
     * Setter for capitalCityName
     *
     * @param capitalCityName capitalCityName
     */
    private void setCapitalCityName(String capitalCityName)
    {
        this.capitalCityName = capitalCityName;
    }

    /**
     * Setter for facts
     *
     * @param facts facts
     */
    private void setFacts(String[] facts)
    {
        this.facts = facts;
    }

    /*
     * Validation method for a name
     *
     * @param name is the name
     * @throws IllegalArgumentException if name is null or empty
     */
    private static void validateName(final String name)
    {
        if(name == null || name.isBlank())
        {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }
}
