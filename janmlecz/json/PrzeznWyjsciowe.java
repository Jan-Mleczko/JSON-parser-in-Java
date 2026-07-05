package janmlecz.json;

class PrzeznWyjsciowe extends PrzeznWartJSON
{
  private WartoscJSON wartosc;

  public PrzeznWyjsciowe ()
  {
    wyzsze = null;
    wartosc = null;
  }

  public void przekaz (WartoscJSON wsc)
  {
    if (wartosc != null)
      throw new ZleZeSkladniaJSON ();
    wartosc = wsc;
  }

  public WartoscJSON zakoncz ()
  {
    return wartosc;
  }
}