package cz.plastique.examples;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * An example of Builder pattern with defined order of building and optional items.
 *
 */
public class ClassToBuild<IN, K, OUT> {

  private final List<IN> input;
  private final Function<IN, K> keyExtractor;
  private final Function<IN, OUT> reducer;
  private final boolean option;

  private ClassToBuild(List<IN> input, Function<IN, K> keyExtractor,
      Function<IN, OUT> reducer, boolean option) {
    this.input = input;
    this.keyExtractor = keyExtractor;
    this.reducer = reducer;
    this.option = option;
  }

  List<OUT> getOutput() {
    return null;
  }

  @SuppressWarnings("unchecked")
  static <IN> KeyBuilder<IN> of(List<IN> input) {
    return (Builder<IN, ?, ?>) new Builder(input);
  }

  // ----------------- builders chain -----------------

  interface KeyBuilder<IN> {

    <K> ReduceBuilder<IN, K> keyBy(Function<IN, K> keyExtractor);
  }

  interface ReduceBuilder<IN, K> {

    <OUT> FinalBuilder<IN, K, OUT> reduceBy(Function<IN, OUT> reducer);
  }

  interface FinalBuilder<IN, K, OUT> {

    // optional builders
    FinalBuilder<IN, K, OUT> withFirstOption();
    FinalBuilder<IN, K, OUT> withSecondOption();

    // final method
    ClassToBuild<IN, K, OUT> output();
  }

  // ----------------- builder itself -----------------

  static class Builder<IN, K, OUT>
      implements KeyBuilder<IN>, ReduceBuilder<IN, K>, FinalBuilder<IN, K, OUT> {

    private List<IN> input;
    private Function<IN, K> keyExtractor;
    private Function<IN, OUT> reducer;
    private boolean option = false;

    private Builder(List<IN> input) {
      this.input = input;
    }

    @Override
    public <K> ReduceBuilder<IN, K> keyBy(Function<IN, K> keyExtractor) {
      @SuppressWarnings("unchecked")
      Builder<IN, K, ?> castedBuilder = (Builder<IN, K, ?>) this;
      castedBuilder.keyExtractor = Objects.requireNonNull(keyExtractor);
      return castedBuilder;
    }

    @Override
    public <OUT> FinalBuilder<IN, K, OUT> reduceBy(Function<IN, OUT> reducer) {
      @SuppressWarnings("unchecked")
      Builder<IN, K, OUT> castedBuilder = (Builder<IN, K, OUT>) this;
      castedBuilder.reducer = Objects.requireNonNull(reducer);
      return null;
    }

    @Override
    public FinalBuilder<IN, K, OUT> withFirstOption() {
      this.option = true;
      return this;
    }

    @Override
    public FinalBuilder<IN, K, OUT> withSecondOption() {
      //enable the second option
      return this;
    }

    @Override
    public ClassToBuild<IN, K, OUT> output() {
      return new ClassToBuild<>(input, keyExtractor, reducer, option);
    }
  }

  // ----------------- usage examples -----------------
  public static void main(String[] args) {

    List<String> input = Collections.emptyList();

    ClassToBuild<String, String, String> op1 = ClassToBuild
        .of(input)
        .keyBy(s -> s)
        .reduceBy(s -> s)
        .output();

    ClassToBuild<String, Integer, Integer> op2 = ClassToBuild
        .of(input)
        .keyBy(String::length)
        .reduceBy(String::length)
        .withFirstOption()
        .output();
  }
}
