package janmlecz.json;
/*Obliczenia na duzych liczbach wymiernych w systemie dziesietnym. Kazdy obiekt
 *klasy LiczbaDziesietna reprezentuje pewna liczbe okreslona w chwili jego
 *utworzenia i nie zmieniajaca sie pozniej. Zatem obiekty te sa niemutowalne.
 *
 *Reprezentowane liczby mieszcza sie w zakresie
 *od -99999 99999 99999 99999,99999 99999
 *do +99999 99999 99999 99999,99999 99999
 *             z krokiem co 0,00000 00001.
 *
 *Zaimplementowane dzialania to dodawanie, odejmowanie, mnozenie, a takze
 *porownywanie. Metody publiczne odpowiedzialne za wykonywanie dzialan zwracaja
 *nowy obiekt reprezentujacy wynik (z wyjatkiem oczywiscie porownan
 *zwracajacych wartosci logiczne). Jesli dokladny wynik jest
 *niereprezentowalny, rzucany jest wyjatek.
 */

public class LiczbaDziesietna
{
  private byte cyfry[];
  private boolean ujemna;

  private LiczbaDziesietna (byte c[], boolean u)
  {
    cyfry = c;
    ujemna = u;
  }////////////////////////////////////////////////////////////////////////////

  private static byte[] suroweZera ()
  {
    int i;
    byte tabl[];

    tabl = new byte[30];
    i = 0;
    while (i < 30)
      tabl[i++] = 0;
    return tabl;
  }////////////////////////////////////////////////////////////////////////////

  public LiczbaDziesietna ()
  {
    cyfry = suroweZera ();
    ujemna = false;
  }////////////////////////////////////////////////////////////////////////////

  private static byte[] bezwzglednieDodaj (byte a[], byte b[],
    boolean wynikowePrzeniesienie[])
  {
    int i, suma, przeniesienie;
    byte c[];

    i = 0;
    c = new byte[30];
    przeniesienie = 0;
    while (i < 30)
    {
      suma = a[i] + b[i] + przeniesienie;
      if (suma > 9)
      {
        suma -= 10;
        przeniesienie = 1;
      }
      else
        przeniesienie = 0;
      c[i++] = (byte) suma;
    }

    wynikowePrzeniesienie[0] = przeniesienie > 0;
    return c;
  }////////////////////////////////////////////////////////////////////////////

  private static byte[] bezwzglednieOdejmij (byte a[], byte b[],
    boolean wynikowePozyczenie[])
  {
    int i, roznica, pozyczenie;
    byte c[];

    i = 0;
    c = new byte[30];
    pozyczenie = 0;
    while (i < 30)
    {
      roznica = a[i] - b[i] - pozyczenie;
      if (roznica < 0)
      {
        roznica += 10;
        pozyczenie = 1;
      }
      else
        pozyczenie = 0;
      c[i++] = (byte) roznica;
    }

    wynikowePozyczenie[0] = pozyczenie > 0;
    return c;
  }////////////////////////////////////////////////////////////////////////////

  private static byte[] przeciwneCyfry (byte wejscie[])
  {
    return bezwzglednieOdejmij (suroweZera (), wejscie, new boolean[1]);
  }////////////////////////////////////////////////////////////////////////////

  public LiczbaDziesietna (String tekstObiektowy)
  {
    int dlugosc, i, granicaUlamka, miejsce, poczatek;
    char tekstZnaki[], znak;

    dlugosc = tekstObiektowy.length ();
    if (dlugosc > 100)
      throw new ZleZObliczeniamiDziesietnymi ();
    tekstZnaki = tekstObiektowy.toCharArray ();

    znak = tekstZnaki[0];
    if (znak == '-')
    {
      ujemna = true;
      poczatek = 1;
    }
    else
    {
      ujemna = false;
      if (znak == '+')
        poczatek = 1;
      else
        poczatek = 0;
    }

    i = poczatek;
    granicaUlamka = dlugosc;
    while (i < dlugosc)
    {
      znak = tekstZnaki[i];
      if (znak == '.' || znak == ',')
      {
        if (granicaUlamka != dlugosc)
          throw new ZleZObliczeniamiDziesietnymi ();
        granicaUlamka = i;
      }
      else if (znak < '0' || znak > '9')
        throw new ZleZObliczeniamiDziesietnymi ();
      ++i;
    }
    cyfry = new byte[30];

    i = granicaUlamka;
    miejsce = 10;
    while (i > poczatek)
    {
      if (miejsce >= 30)
        throw new ZleZObliczeniamiDziesietnymi ();
      cyfry[miejsce++] = (byte) (((int) tekstZnaki[--i]) - 48);
    }

    while (miejsce < 30)
      cyfry[miejsce++] = 0;

    i = granicaUlamka;
    miejsce = 10;
    while (++i < dlugosc)
    {
      if (miejsce <= 0)
        throw new ZleZObliczeniamiDziesietnymi ();
      cyfry[--miejsce] = (byte) (((int) tekstZnaki[i]) - 48);
    }

    while (miejsce > 0)
      cyfry[--miejsce] = 0;

    if (ujemna)
    {
      if (niezerowa (cyfry))
        cyfry = przeciwneCyfry (cyfry);
      else
        ujemna = false;
    }
  }////////////////////////////////////////////////////////////////////////////

  public LiczbaDziesietna (int wartosc, int wykl)
  {
    int i;

    wykl += 10;
    if (wykl < 0 || wykl >= 30)
      throw new ZleZObliczeniamiDziesietnymi ();
    if (wartosc < 0)
    {
      ujemna = true;
      wartosc = -wartosc;
      if (wartosc < 0)
        throw new ZleZObliczeniamiDziesietnymi ();
    }
    else
      ujemna = false;

    cyfry = new byte[30];

    i = 0;
    while (i < wykl)
      cyfry[i++] = 0;

    while (wartosc > 0)
    {
      if (i >= 30)
        throw new ZleZObliczeniamiDziesietnymi ();
      cyfry[i++] = (byte) (wartosc % 10);
      wartosc /= 10;
    }

    while (i < 30)
      cyfry[i++] = 0;

    if (ujemna)
      cyfry = bezwzglednieOdejmij (suroweZera (), cyfry, new boolean[1]);
  }////////////////////////////////////////////////////////////////////////////

  public boolean rowna (LiczbaDziesietna inna)
  {
    int i;

    if (inna == null)
      throw new ZleZObliczeniamiDziesietnymi ();
    if (inna == this)
      return true;
    if (inna.ujemna != ujemna)
      return false;

    i = 0;
    while (i < 30)
    {
      if (inna.cyfry[i] != cyfry[i])
        return false;
      ++i;
    }
    return true;
  }////////////////////////////////////////////////////////////////////////////

  private static boolean bezwzglednaWieksza (byte a[], byte b[])
  {
    int i;

    i = 30;
    while (i > 0)
    {
      --i;
      if (a[i] > b[i])
        return true;
      if (a[i] < b[i])
        return false;
    }
    return false;
  }////////////////////////////////////////////////////////////////////////////

  public boolean wiekszaNiz (LiczbaDziesietna inna)
  {
    int i;

    if (inna == null)
      throw new ZleZObliczeniamiDziesietnymi ();
    if (inna == this || ujemna && !inna.ujemna)
      return false;
    if (!ujemna && inna.ujemna)
      return true;

    return bezwzglednaWieksza (cyfry, inna.cyfry);
  }////////////////////////////////////////////////////////////////////////////

  private static boolean niezerowa (byte cyfry[])
  {
    int i;

    i = 0;
    while (i < 30)
    {
      if (cyfry[i] > 0)
        return true;
      ++i;
    }
    return false;
  }////////////////////////////////////////////////////////////////////////////

  public LiczbaDziesietna dodaj (LiczbaDziesietna druga)
  {
    byte cyfryWyniku[];
    boolean znacznik[], ujemnyWynik;

    znacznik = new boolean[1];
    cyfryWyniku = bezwzglednieDodaj (cyfry, druga.cyfry, znacznik);

    if (ujemna)
    {
      if (druga.ujemna)
      {
        /* Dodawanie dwoch ujemnych. */
        if (!znacznik[0] || !niezerowa (cyfryWyniku))
          throw new ZleZObliczeniamiDziesietnymi ();
        ujemnyWynik = true;
      }
      else
      {
        /* Dodawanie dodatniej do ujemnej. */
        ujemnyWynik = !znacznik[0] && niezerowa (cyfryWyniku);
      }
    }
    else
    {
      if (druga.ujemna)
      {
        /* Dodawanie ujemnej do dodatniej. */
        ujemnyWynik = !znacznik[0] && niezerowa (cyfryWyniku);
      }
      else
      {
        /* Dodawanie dwoch dodatnich. */
        if (znacznik[0])
          throw new ZleZObliczeniamiDziesietnymi ();
        ujemnyWynik = false;
      }
    }

    return new LiczbaDziesietna (cyfryWyniku, ujemnyWynik);
  }////////////////////////////////////////////////////////////////////////////

  public LiczbaDziesietna odejmij (LiczbaDziesietna odjemnik)
  {
    byte cyfryWyniku[];
    boolean znacznik[], ujemnyWynik;

    znacznik = new boolean[1];
    cyfryWyniku = bezwzglednieOdejmij (this.cyfry, odjemnik.cyfry, znacznik);

    if (ujemna)
    {
      if (odjemnik.ujemna)
      {
        /* Odejmowanie ujemnej od ujemnej. */
        ujemnyWynik = znacznik[0];
      }
      else
      {
        /* Odejmowanie dodatniej od ujemnej. */
        if (znacznik[0] || !niezerowa (cyfryWyniku))
          throw new ZleZObliczeniamiDziesietnymi ();
        ujemnyWynik = true;
      }
    }
    else
    {
      if (odjemnik.ujemna)
      {
        /* Odejmowanie ujemnej od dodatniej. */
        if (!znacznik[0])
          throw new ZleZObliczeniamiDziesietnymi ();
        ujemnyWynik = false;
      }
      else
      {
        /* Odejmowanie dodatniej od dodatniej. */
        ujemnyWynik = znacznik[0];
      }
    }

    return new LiczbaDziesietna (cyfryWyniku, ujemnyWynik);
  }////////////////////////////////////////////////////////////////////////////

  public LiczbaDziesietna mnoz (LiczbaDziesietna inna)
  {
    byte czynnikA[], czynnikB[], iloczyn[];
    boolean ujemnoscIloczynu;
    int i, suma, przeniesienie, ja, jb;

    ujemnoscIloczynu = ujemna;
    czynnikA = ujemna ? przeciwneCyfry (cyfry) : cyfry;
    if (inna.ujemna)
    {
      czynnikB = przeciwneCyfry (inna.cyfry);
      ujemnoscIloczynu = !ujemnoscIloczynu;
    }
    else
      czynnikB = inna.cyfry;

    iloczyn = new byte[30];
    i = -20;
    suma = 0;
    while (i <= 38)
    {
      ja = -10;
      while (ja < 20)
      {
        jb = i - ja;
        if (jb >= -10 && jb < 20)
          suma += czynnikA[ja + 10] * czynnikB[jb + 10];
        ++ja;
      }

      if (i >= -10 && i < 20)
      {
        iloczyn[i + 10] = (byte) (suma % 10);
        suma /= 10;
      }
      else if (suma > 0)
        throw new ZleZObliczeniamiDziesietnymi ();
      ++i;
    }

    if (ujemnoscIloczynu)
      iloczyn = przeciwneCyfry (iloczyn);
    return new LiczbaDziesietna (iloczyn,
      ujemnoscIloczynu && niezerowa (iloczyn));
  }////////////////////////////////////////////////////////////////////////////

  public String naTekst (int wyrownanieCalkowitej, int wyrownanieUlamkowej,
    char znakPodzialu, char znakWyrownania, boolean zaznaczacPlus)
  {
    StringBuilder wynik;
    int miejsce, calkowitychZnaczacych, ulamkowychZnaczacych, i;
    byte bwCyfry[];

    wynik = new StringBuilder ();
    if (ujemna)
    {
      bwCyfry = bezwzglednieOdejmij (suroweZera (), cyfry, new boolean[1]);
      wynik.append ('-');
    }
    else
    {
      bwCyfry = cyfry;
      if (zaznaczacPlus)
        wynik.append ('+');
    }

    miejsce = 0;
    while (miejsce < 10 && bwCyfry[miejsce] == 0)
      ++miejsce;
    ulamkowychZnaczacych = 10 - miejsce;

    miejsce = 29;
    while (miejsce >= 10 && bwCyfry[miejsce] == 0)
      --miejsce;
    calkowitychZnaczacych = miejsce - 9;

    i = calkowitychZnaczacych;
    while (i < wyrownanieCalkowitej)
    {
      wynik.append (znakWyrownania);
      ++i;
    }

    miejsce = calkowitychZnaczacych + 9;
    while (miejsce >= 10)
    {
      wynik.append ((char) (bwCyfry[miejsce] + 48));
      --miejsce;
    }

    if ((calkowitychZnaczacych | wyrownanieCalkowitej) > 0
    && (ulamkowychZnaczacych | wyrownanieUlamkowej) > 0)
      wynik.append (znakPodzialu);

    i = 10 - ulamkowychZnaczacych;
    miejsce = 10;
    while (miejsce > i)
      wynik.append ((char) (bwCyfry[--miejsce] + 48));

    i = ulamkowychZnaczacych;
    while (i < wyrownanieUlamkowej)
    {
      wynik.append (znakWyrownania);
      ++i;
    }

    return wynik.toString ();
  }////////////////////////////////////////////////////////////////////////////

  public String toString ()
  {
    return naTekst (1, 0, '.', '0', false);
  }////////////////////////////////////////////////////////////////////////////
}