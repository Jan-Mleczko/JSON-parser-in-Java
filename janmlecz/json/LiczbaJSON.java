package janmlecz.json;

public class LiczbaJSON extends WartoscJSON
{
  private LiczbaDziesietna w;

  public LiczbaJSON (LiczbaDziesietna a)
  {
    w = a;
  }

  public LiczbaDziesietna uzysLiczbe ()
  {
    return w;
  }

  public String toString ()
  {
    return w.toString ();
  }
}