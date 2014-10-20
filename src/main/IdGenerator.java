package main;
import java.math.BigInteger;
import java.security.SecureRandom;

public final class IdGenerator {
  private static SecureRandom random = new SecureRandom();

  public static String nextId() {
    return new BigInteger(130, random).toString(32);
  }
}
