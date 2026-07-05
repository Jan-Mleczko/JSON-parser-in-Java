package janmlecz.json;

public class SlownikJSON extends WartoscJSON
{
  static class ElemL
  {
    public String klucz;
    public WartoscJSON wartosc;
    public ElemL dalej;
  }

  private ElemL lista;
  private boolean otwarty;

  public SlownikJSON ()
  {
    lista = null;
    otwarty = true;
  }////////////////////////////////////////////////////////////////////////////

  public void dodaj (String kcz, WartoscJSON wsc)
  {
    ElemL e;

    if (!otwarty)
      throw new IllegalStateException ();
    e = new ElemL ();
    e.klucz = kcz;
    e.wartosc = wsc;
    e.dalej = lista;
    lista = e;
  }////////////////////////////////////////////////////////////////////////////

  public void domknij ()
  {
    if (!otwarty)
      throw new IllegalStateException ();
    otwarty = false;
  }

  public WartoscJSON uzysWart (String kcz)
  {
    ElemL e;

    if (otwarty)
      throw new IllegalStateException ();
    e = lista;
    while (e != null)
    {
      if (e.klucz.equals (kcz))
        return e.wartosc;
      e = e.dalej;
    }
    return null;
  }////////////////////////////////////////////////////////////////////////////

  public String toString ()
  {
    StringBuilder bld;
    ElemL el;

    if (otwarty)
      return "<<<OTWARTY SLOWNIK>>>";
    bld = new StringBuilder ();
    bld.append ('{');
    el = lista;
    while (el != null)
    {
      bld.append (new TekstJSON (el.klucz));
      bld.append (':');
      bld.append (el.wartosc);
      el = el.dalej;
      if (el != null)
        bld.append (',');
    }
    bld.append ('}');
    return bld.toString ();
  }
}