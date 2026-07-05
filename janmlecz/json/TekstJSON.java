package janmlecz.json;

public class TekstJSON extends WartoscJSON
{
  private String w;

  public TekstJSON (String a)
  {
    w = a;
  }

  public String uzysTekst ()
  {
    return w;
  }

  public String toString ()
  {
    StringBuilder bld;
    String szesn;
    int i, a, n;
    char znaki[], bznak;

    bld = new StringBuilder ();
    bld.append ('"');
    znaki = w.toCharArray ();
    i = 0;
    n = w.length ();

    while (i < n)
    {
      bznak = znaki[i++];
      if (bznak >= ' ' && bznak <= '~')
      {
        if (bznak == '"' || bznak == '\\')
          bld.append ('\\');
        bld.append (bznak);
      }
      else
      {
        a = (int) bznak;
        switch (a)
        {
          case 8:
            bld.append ("\\b");
            break;
          case 9:
            bld.append ("\\t");
            break;
          case 10:
            bld.append ("\\n");
            break;
          case 13:
            bld.append ("\\r");
            break;
          default:
            bld.append ("\\u");
            szesn = Integer.toString (a, 16).toUpperCase ();
            while (szesn.length () < 4)
              szesn = "0" + szesn;
            bld.append (szesn.substring (0, 4));
        }
      }
    }

    bld.append ('"');
    return bld.toString ();
  }
}