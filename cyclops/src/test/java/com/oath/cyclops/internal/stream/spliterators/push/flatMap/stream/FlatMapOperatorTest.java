package com.oath.cyclops.internal.stream.spliterators.push.flatMap.stream;

import com.oath.cyclops.internal.stream.spliterators.push.AbstractOperatorTest;
import com.oath.cyclops.internal.stream.spliterators.push.ArrayOfValuesOperator;
import com.oath.cyclops.internal.stream.spliterators.push.Fixtures;
import com.oath.cyclops.internal.stream.spliterators.push.FlatMapOperator;
import com.oath.cyclops.internal.stream.spliterators.push.Operator;
import com.oath.cyclops.internal.stream.spliterators.push.SingleValueOperator;
import cyclops.reactive.Spouts;

/**
 * Created by johnmcclean on 17/01/2017.
 */
public class FlatMapOperatorTest extends AbstractOperatorTest {


    public Operator<Integer> createEmpty() {
        return new FlatMapOperator<Integer, Integer>(new ArrayOfValuesOperator<>(),
                                                     i -> Spouts.of(i * 2));
    }

    public Operator<Integer> createOne() {
        return new FlatMapOperator<Integer, Integer>(new SingleValueOperator<>(1),
                                                     i -> Spouts.of(i * 2));
    }

    public Operator<Integer> createThree() {
        return new FlatMapOperator<Integer, Integer>(new ArrayOfValuesOperator<>(1,
                                                                                 2,
                                                                                 3),
                                                     i -> Spouts.of(i * 2));
    }

    public Operator<Integer> createTwoAndError() {
        return new FlatMapOperator<Integer, Integer>(Fixtures.twoAndErrorSource,
                                                     i -> Spouts.of(i * 2));
    }

    public Operator<Integer> createThreeErrors() {
        return new FlatMapOperator<Integer, Integer>(Fixtures.threeErrorsSource,
                                                     i -> Spouts.of(i * 2));
    }


}
