package cyclops.container.immutable.tuple;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by johnmcclean on 04/10/2017.
 */
public class Tuple6Test {

    Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple;
    Tuple6<String, Integer, Integer, Integer, Integer, Integer> lazyT1;
    int called;

    @Before
    public void setUp() throws Exception {
        tuple = Tuple.tuple(2,
                            5,
                            10,
                            10,
                            10,
                            10);
        lazyT1 = Tuple.lazy(() -> {
                                called++;
                                return "hello";
                            },
                            () -> 1,
                            () -> 2,
                            () -> 3,
                            () -> 4,
                            () -> 8);
        called = 0;

    }

    @Test
    public void of() throws Exception {
        assertThat(Tuple6.of(2,
                             5,
                             10,
                             10,
                             10,
                             10),
                   equalTo(tuple));
    }

    @Test
    public void lazy() throws Exception {

        assertThat(called,
                   equalTo(0));
        assertThat(lazyT1._1(),
                   equalTo("hello"));
        assertThat(called++,
                   equalTo(1));
    }

    @Test
    public void _1() throws Exception {

        assertThat(tuple._1(),
                   equalTo(2));
        assertThat(tuple.map1(i -> i + 1)
                        ._1(),
                   equalTo(3));
        assertThat(tuple.lazyMap1(i -> i + 1)
                        ._1(),
                   equalTo(3));
    }

    @Test
    public void _2() throws Exception {

        assertThat(tuple._2(),
                   equalTo(5));
        assertThat(tuple.map2(i -> i + 2)
                        ._2(),
                   equalTo(7));
        assertThat(tuple.lazyMap2(i -> i + 2)
                        ._2(),
                   equalTo(7));
    }

    @Test
    public void _3() throws Exception {

        assertThat(tuple._3(),
                   equalTo(10));
        assertThat(tuple.map3(i -> i + 2)
                        ._3(),
                   equalTo(12));
        assertThat(tuple.lazyMap3(i -> i + 2)
                        ._3(),
                   equalTo(12));
    }

    @Test
    public void _4() throws Exception {

        assertThat(tuple._4(),
                   equalTo(10));
        assertThat(tuple.map4(i -> i + 3)
                        ._4(),
                   equalTo(13));
        assertThat(tuple.lazyMap4(i -> i + 3)
                        ._4(),
                   equalTo(13));
    }

    @Test
    public void _5() throws Exception {

        assertThat(tuple._5(),
                   equalTo(10));
        assertThat(tuple.map5(i -> i + 2)
                        ._5(),
                   equalTo(12));
        assertThat(tuple.lazyMap5(i -> i + 2)
                        ._5(),
                   equalTo(12));
    }

    @Test
    public void _6() throws Exception {

        assertThat(tuple._6(),
                   equalTo(10));
        assertThat(tuple.map6(i -> i + 3)
                        ._6(),
                   equalTo(13));
        assertThat(tuple.lazyMap6(i -> i + 3)
                        ._6(),
                   equalTo(13));
    }


    @Test
    public void map() throws Exception {
        assertThat(tuple.mapAll(i -> i + 1,
                                i -> i + 1,
                                i -> i + 1,
                                i -> i + 1,
                                i -> i + 1,
                                i -> i + 1),
                   equalTo(Tuple.tuple(3,
                                       6,
                                       11,
                                       11,
                                       11,
                                       11)));
    }


    @Test
    public void eager() throws Exception {
        assertThat(Tuple.lazy(() -> "z1",
                              () -> "x1",
                              () -> "w1",
                              () -> "new",
                              () -> "boo!",
                              () -> "hello")
                        .eager(),
                   equalTo(Tuple.tuple("z1",
                                       "x1",
                                       "w1",
                                       "new",
                                       "boo!",
                                       "hello")));
    }

    @Test
    public void memo() {
        Tuple6<String, Integer, Integer, Integer, Integer, Integer> lazy = Tuple.lazy(() -> {
                                                                                          called++;
                                                                                          return "lazy";
                                                                                      },
                                                                                      () -> 10,
                                                                                      () -> 5,
                                                                                      () -> 0,
                                                                                      () -> -4,
                                                                                      () -> 17);
        lazy._1();
        lazy._1();
        assertThat(called,
                   equalTo(2));
        called = 0;
        Tuple6<String, Integer, Integer, Integer, Integer, Integer> memo = lazy.memo();
        memo._1();
        memo._1();
        assertThat(called,
                   equalTo(1));
    }


    @Test
    public void lazyMap() throws Exception {
        assertThat(tuple.lazyMapAll(i -> i + 1,
                                    i -> i + 1,
                                    i -> i + 1,
                                    i -> i + 1,
                                    i -> i + 1,
                                    i -> i + 1),
                   equalTo(Tuple.tuple(3,
                                       6,
                                       11,
                                       11,
                                       11,
                                       11)));
        tuple.lazyMapAll(i -> {
                             called++;
                             return i + 1;
                         },
                         i -> i,
                         i -> i + 1,
                         i -> i,
                         i -> i + 1,
                         i -> i + 1);
        assertThat(called,
                   equalTo(0));
    }


    @Test
    public void visit() throws Exception {
        assertThat(tuple.fold((a, b, c, d, e, f) -> a + b + c + d + e + f),
                   equalTo(47));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(tuple.toString(),
                   equalTo("[2,5,10,10,10,10]"));
    }


    @Test
    public void transform() {
        assertThat(lazyT1.transform((a, b, c, d, e, f) -> a + b + c + d + e + f),
                   equalTo("hello12348"));
    }


}
