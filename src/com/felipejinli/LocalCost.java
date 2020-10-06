package com.felipejinli;

public class LocalCost {
    // TODO suggested value 3.8 by Cox; paper value 4.127764022219371
    public static double occlusionCost = 4.127764022219371;
    public static int variance = 16;

    public static int getVariance() {
        return variance;
    }

    public static void setVariance(int variance) {
        LocalCost.variance = variance;
    }

    public static double getOcclusionCost() {
        return occlusionCost;
    }

    public static void setOcclusionCost(double occlusionCost) {
        LocalCost.occlusionCost = occlusionCost;
    }

    public static double getMatchCost(int pix1, int pix2) {
        return Math.pow((pix1 - pix2), 2)/variance;
    }
}
