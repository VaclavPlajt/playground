package cz.plastique.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Simple illustration of lambda expression alternating their {@linkplain Class} after
 * (de)serialization.
 */
public class LambdaChangingClass {

  public interface SerializableSupplier<T> extends Serializable {

    T get();
  }

  private static final SerializableSupplier<Integer> FINAL_SUPPLIER = () -> 8;

  private static final SerializableSupplier<Integer> ANONYMOUS_FINAL_SUPPLIER = new SerializableSupplier<Integer>() {
    @Override
    public Integer get() {
      return 8;
    }
  };

  public static void main(String args[]) throws IOException, ClassNotFoundException {

    serializedeserializeAndPrintClass(FINAL_SUPPLIER);
    serializedeserializeAndPrintClass(ANONYMOUS_FINAL_SUPPLIER);
  }

  private static void serializedeserializeAndPrintClass(SerializableSupplier<Integer> finalSupplier)
      throws IOException, ClassNotFoundException {
    ByteArrayOutputStream outStr = new ByteArrayOutputStream();
    ObjectOutputStream oss = new ObjectOutputStream(outStr);

    oss.writeObject(finalSupplier);
    oss.flush();
    oss.close();

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(outStr.toByteArray()));

    @SuppressWarnings("unchecked")
    SerializableSupplier<Integer> supplierDeserialized =
        (SerializableSupplier<Integer>) ois.readObject();

    System.out.println(finalSupplier.getClass());
    System.out.println(supplierDeserialized.getClass());
    System.out.println(finalSupplier.getClass().equals(supplierDeserialized.getClass()));
  }

}

