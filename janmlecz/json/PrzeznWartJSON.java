package janmlecz.json;

abstract class PrzeznWartJSON
{
  abstract public void przekaz (WartoscJSON wsc);
  abstract public WartoscJSON zakoncz ();

  public PrzeznWartJSON wyzsze;
}