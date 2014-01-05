FRED
====

Functional Reactive Event Driver - a lightweight library aims to facilitate Functional Reactive Programming in Java.

### Usage Example:
``` java
        FunctionalReactives.from(1, 2, 3, 4, 5)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input % 2 == 1;
                    }
                })
                .reduce(new FunctionAcc<Integer, Integer>() {
                    @Override
                    public Optional<Integer> apply(Integer acc, Integer next) {
                        return Optional.of(acc + next);
                    }
                })
                .map(new Function<Integer, Optional<String>>() {
                    @Override
                    public Optional<String> apply(Integer input) {
                        return Optional.of(input.toString());
                    }
                })
                .forEach(new FunctionVoid<Object>() {
                    @Override
                    public void apply(Object input) {
                        System.out.println(input);
                    }
                })
                .start();
                //Output: (result of 1+3 and 1+3+9 in String)
                //4
                //9

```
