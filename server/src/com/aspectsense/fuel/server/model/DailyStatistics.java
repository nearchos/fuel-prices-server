package com.aspectsense.fuel.server.model;

import java.io.Serializable;

public class DailyStatistics implements Serializable {

    private DayStatistics todaysStatistics;
    private DayStatistics minusThreeDaysStatistics;
    private DayStatistics minusTenDaysStatistics;

    public DailyStatistics(DayStatistics todaysStatistics, DayStatistics minusThreeDaysStatistics, DayStatistics minusTenDaysStatistics) {
        this.todaysStatistics = todaysStatistics;
        this.minusThreeDaysStatistics = minusThreeDaysStatistics;
        this.minusTenDaysStatistics = minusTenDaysStatistics;
    }

    public DayStatistics getTodaysStatistics() {
        return todaysStatistics;
    }

    public DayStatistics getMinusThreeDaysStatistics() {
        return minusThreeDaysStatistics;
    }

    public DayStatistics getMinusTenDaysStatistics() {
        return minusTenDaysStatistics;
    }

    public static class DayStatistics {
        private int [] mins;
        private int [] maxs;
        private int [] medians;
        private double [] averages;

        public DayStatistics(int[] mins, int[] maxs, int[] medians, double[] averages) {
            this.mins = mins;
            this.maxs = maxs;
            this.medians = medians;
            this.averages = averages;
        }

        public int[] getMins() {
            return mins;
        }

        public int[] getMaxs() {
            return maxs;
        }

        public int[] getMedians() {
            return medians;
        }

        public double[] getAverages() {
            return averages;
        }
    }
}