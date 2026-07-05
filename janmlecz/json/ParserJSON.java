package janmlecz.json;

public class ParserJSON
{
  private char znaki[];
  private int ilznakow, biezacy;

  private ParserJSON (String zakodowany)
  {
    znaki = zakodowany.toCharArray ();
    ilznakow = zakodowany.length ();
    biezacy = 0;
  }////////////////////////////////////////////////////////////////////////////

  private static boolean czyBialy (char znak)
  {
    return znak == ' ' || znak == '\t' || znak == '\n' || znak == '\r';
  }////////////////////////////////////////////////////////////////////////////

  private void biale ()
  {
    while (biezacy < ilznakow && czyBialy (znaki[biezacy]))
      ++biezacy;
  }////////////////////////////////////////////////////////////////////////////

  private static boolean czyLiczbowy (char znak)
  {
    return znak >= '0' && znak <= '9' || znak == '-' || znak == '+'
    || znak == '.';
  }////////////////////////////////////////////////////////////////////////////

  private WartoscJSON leksWart;
  private static final int
    LCSTALA = 1, /*Stala liczbowa.*/
    TKSTALA = 2, /*Stala tekstowa.*/
    LOGSTALA = 3, /*Stala logiczna.*/
    NSTALA = 4, /*Stala NULL.*/
    OTTABL = 5, /*Otwarcie tablicy.*/
    ZKTABL = 6, /*Zamkniecie tablicy.*/
    OTSLOW = 7, /*Otwarcie slownika, obiektu.*/
    ZKSLOW = 8, /*Zamkniecie slownika.*/
    ROZDZ = 9, /*Rozdzielenie elementow tablicy albo slownika.*/
    ODDZKW = 10, /*Oddzielenie klucza od wartosci w slowniku.*/
    KONIECD = 11; /*Koniec danych w formacie JSON.*/

  private int leksem ()
  {
    int pozost, i, wycinp, wycink, wykl10;
    LiczbaDziesietna mantysa;
    StringBuilder tekstStalej;
    char znk;
    boolean jeszcze, ulamek;

    biale ();
    pozost = ilznakow - biezacy;
    if (pozost < 1)
      return KONIECD;
    switch (znaki[biezacy])
    {
      case 't':
        if (pozost >= 4 && znaki[biezacy + 1] == 'r'
        && znaki[biezacy + 2] == 'u' && znaki[biezacy + 3] == 'e')
        {
          biezacy += 4;
          leksWart = new WLogicznaJSON (true);
          return LOGSTALA;
        }
        throw new ZleZeSkladniaJSON ();
      case 'n':
        if (pozost >= 4 && znaki[biezacy + 1] == 'u'
        && znaki[biezacy + 2] == 'l' && znaki[biezacy + 3] == 'l')
        {
          biezacy += 4;
          leksWart = new WPustaJSON ();
          return NSTALA;
        }
        throw new ZleZeSkladniaJSON ();
      case 'f':
        if (pozost >= 5 && znaki[biezacy + 1] == 'a'
        && znaki[biezacy + 2] == 'l' && znaki[biezacy + 3] == 's'
        && znaki[biezacy + 4] == 'e')
        {
          biezacy += 5;
          leksWart = new WLogicznaJSON (false);
          return LOGSTALA;
        }
        throw new ZleZeSkladniaJSON ();
      case '[':
        ++biezacy;
        return OTTABL;
      case ']':
        ++biezacy;
        return ZKTABL;
      case '{':
        ++biezacy;
        return OTSLOW;
      case '}':
        ++biezacy;
        return ZKSLOW;
      case ',':
        ++biezacy;
        return ROZDZ;
      case ':':
        ++biezacy;
        return ODDZKW;
      case '"':
        tekstStalej = new StringBuilder ();
        jeszcze = true;
        while (jeszcze && ++biezacy < ilznakow)
        {
          znk = znaki[biezacy];
          switch (znk)
          {
            case '"':
              jeszcze = false;
              break;
            case '\\':
              if (++biezacy >= ilznakow)
                throw new ZleZeSkladniaJSON ();
              znk = znaki[biezacy];
              switch (znk)
              {
                case '"':
                  tekstStalej.append ('"');
                  break;
                case 'b':
                  tekstStalej.append ((char) 8);
                  break;
                case 'f':
                  tekstStalej.append ((char) 12);
                  break;
                case 'n':
                  tekstStalej.append ((char) 10);
                  break;
                case 'r':
                  tekstStalej.append ((char) 13);
                  break;
                case 't':
                  tekstStalej.append ((char) 9);
                  break;
                case 'u':
                  ++biezacy;
                  if (ilznakow - biezacy < 4)
                    throw new ZleZeSkladniaJSON ();
                  try
                  {
                    tekstStalej.append ((char) Integer.parseUnsignedInt (
                      new String (znaki, biezacy, 4), 16));
                  }
                  catch (NumberFormatException nfe)
                  {
                    throw new ZleZeSkladniaJSON ();
                  }
                  biezacy += 3;
                  break;
                default:
                  tekstStalej.append (znk);
              }
              break;
            default:
              tekstStalej.append (znk);
              break;
          }
        }
        ++biezacy;
        leksWart = new TekstJSON (tekstStalej.toString ());
        return TKSTALA;
    }

    i = biezacy;
    ulamek = false;
    while (i < ilznakow && czyLiczbowy (znaki[i]))
    {
      if (znaki[i] == '.')
        ulamek = true;
      ++i;
    }
    if (i > biezacy)
    {
      wycinp = biezacy;
      while (wycinp < i && znaki[wycinp] == '0')
        ++wycinp;
      wycink = i;
      if (ulamek)
      {
        while (wycink > biezacy && znaki[--wycink] == '0');
        ++wycink;
      }

      if (wycinp < wycink)
      {
        try
        {
          mantysa = new LiczbaDziesietna (
            new String (znaki, wycinp, wycink - wycinp)
          );
        }
        catch (ZleZObliczeniamiDziesietnymi exc)
        {
          throw new ZleZeSkladniaJSON ();
        }
      }
      else
        mantysa = new LiczbaDziesietna ();

      biezacy = i;
      if (i < ilznakow && (znaki[i] == 'E' || znaki[i] == 'e'))
      {
        ++biezacy;
        while (++i < ilznakow && czyLiczbowy (znaki[i])) ;
        try
        {
          wykl10 = Integer.parseInt (new String (znaki, biezacy, i - biezacy));
        }
        catch (NumberFormatException nfe)
        {
          throw new ZleZeSkladniaJSON ();
        }

        try
        {
          mantysa = mantysa.mnoz (new LiczbaDziesietna (1, wykl10));
        }
        catch (ZleZObliczeniamiDziesietnymi exc)
        {
          throw new ZleZeSkladniaJSON ();
        }
        biezacy = i;
      }

      leksWart = new LiczbaJSON (mantysa);
      return LCSTALA;
    }
    
    throw new ZleZeSkladniaJSON ();
  }////////////////////////////////////////////////////////////////////////////

  private WartoscJSON pCalosc ()
  {
    PrzeznWartJSON przeznaczenie;
    PrzeznDoTablJSON tbPrzezn;
    PrzeznDoSlowJSON slPrzezn;

    przeznaczenie = new PrzeznWyjsciowe ();
    do
    {
      if (przeznaczenie instanceof PrzeznDoSlowJSON)
      {
        slPrzezn = (PrzeznDoSlowJSON) przeznaczenie;
        if (!slPrzezn.jestKlucz ())
        {
          switch (leksem ())
          {
            case TKSTALA:
              slPrzezn.ustKlucz (((TekstJSON) leksWart).uzysTekst ());
              if (leksem () != ODDZKW)
                throw new ZleZeSkladniaJSON ();
              break;
            case ZKSLOW:
              przeznaczenie.wyzsze.przekaz (przeznaczenie.zakoncz ());
              przeznaczenie = przeznaczenie.wyzsze;
              break;
            case ROZDZ:
              break;
            default:
              throw new ZleZeSkladniaJSON ();
          }
          continue;
        }
      }

      switch (leksem ())
      {
        case LCSTALA:
        case TKSTALA:
        case LOGSTALA:
        case NSTALA:
          przeznaczenie.przekaz (leksWart);
          break;
        case OTTABL:
          tbPrzezn = new PrzeznDoTablJSON (new TablicaJSON ());
          tbPrzezn.wyzsze = przeznaczenie;
          przeznaczenie = tbPrzezn;
          break;
        case ROZDZ:
          break;
        case ZKTABL:
          if (!(przeznaczenie instanceof PrzeznDoTablJSON))
            throw new ZleZeSkladniaJSON ();
          przeznaczenie.wyzsze.przekaz (przeznaczenie.zakoncz ());
          przeznaczenie = przeznaczenie.wyzsze;
          break;
        case OTSLOW:
          slPrzezn = new PrzeznDoSlowJSON (new SlownikJSON ());
          slPrzezn.wyzsze = przeznaczenie;
          przeznaczenie = slPrzezn;
          break;
        default:
          throw new ZleZeSkladniaJSON ();
      }
    }
    while (przeznaczenie.wyzsze != null);

    biale ();
    if (biezacy < ilznakow)
      throw new ZleZeSkladniaJSON ();
    return przeznaczenie.zakoncz ();
  }////////////////////////////////////////////////////////////////////////////

  public static WartoscJSON parsuj (String zakodowany)
  {
    ParserJSON prs;

    prs = new ParserJSON (zakodowany);
    return prs.pCalosc ();
  }////////////////////////////////////////////////////////////////////////////
}