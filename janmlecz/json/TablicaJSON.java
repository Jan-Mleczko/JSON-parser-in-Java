package janmlecz.json;

public class TablicaJSON extends WartoscJSON
{
  static class ElemL
  {
    public WartoscJSON w;
    public ElemL poprzed;
  }

  private ElemL lista;
  private WartoscJSON zawart[];
  private int dlugosc;

  public TablicaJSON ()
  {
    lista = null;
    zawart = null;
    dlugosc = 0;
  }////////////////////////////////////////////////////////////////////////////

  public void dodaj (WartoscJSON nowa)
  {
    ElemL e;

    if (zawart != null)
      throw new IllegalStateException ();
    e = new ElemL ();
    e.w = nowa;
    e.poprzed = lista;
    lista = e;
    ++dlugosc;
  }////////////////////////////////////////////////////////////////////////////

  public void domknij ()
  {
    int i;
    ElemL e;

    if (zawart != null)
      throw new IllegalStateException ();    
    zawart = new WartoscJSON[dlugosc];
    i = dlugosc;
    e = lista;
    while (i > 0)
    {
      zawart[--i] = e.w;
      e = e.poprzed;
    }
  }////////////////////////////////////////////////////////////////////////////

  public int uzysRozm ()
  {
    if (zawart == null)
      throw new IllegalStateException ();
    return dlugosc;
  }////////////////////////////////////////////////////////////////////////////

  public WartoscJSON uzysElem (int indeks)
  {
    if (zawart == null)
      throw new IllegalStateException ();
    return zawart[indeks];
  }////////////////////////////////////////////////////////////////////////////

  public String toString ()
  {
    StringBuilder bld;
    int i;

    if (zawart == null)
      return "<<<OTWARTA TABLICA>>>";

    bld = new StringBuilder ();
    bld.append ('[');

    i = 0;
    while (i < dlugosc)
    {
      if (i > 0)
        bld.append (',');
      bld.append (zawart[i++]);
    }

    bld.append (']');
    return bld.toString ();
  }////////////////////////////////////////////////////////////////////////////
}