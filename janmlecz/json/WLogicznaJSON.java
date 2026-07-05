package janmlecz.json;

public class WLogicznaJSON extends WartoscJSON
{
  private boolean wartosc;

  public WLogicznaJSON (boolean w)
  {
    wartosc = w;
  }

  public boolean uzysWart ()
  {
    return wartosc;
  }

  public String toString ()
  {
    return wartosc ? "true" : "false";
  }
}