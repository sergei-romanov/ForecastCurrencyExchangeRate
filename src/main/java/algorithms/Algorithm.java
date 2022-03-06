package algorithms;

import storage.Csv;

import java.util.List;

public enum Algorithm {
    ARITHMETIC_MEAN("ARITHMETIC_MEAN");

    final String name;

    Algorithm(String name) {
        this.name = name;
    }

    public List<Double> getMethod(Csv csv, Period period) {
        Algorithms algorithm = switch (this){
            case ARITHMETIC_MEAN -> new ArithmeticMean();
        };
        return algorithm.calculating(csv, period);
    }
}
