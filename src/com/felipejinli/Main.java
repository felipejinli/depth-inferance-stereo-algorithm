package com.felipejinli;

import java.io.File;
import java.nio.file.*;
import java.util.Arrays;

public class Main {

    private static double min3(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    private static int findDisparity(int scanLineY, GrayscalePicture lImg, int lPix, GrayscalePicture rImg, int rPix) {
        // int intensityOffset = 255;
        // return (lImg.getGrayscale(lPix, scanLineY) - rImg.getGrayscale(rPix, scanLineY) + intensityOffset)/2;

        // int intensityOffset = 128;
        // return Math.abs(lImg.getGrayscale(lPix, scanLineY) - rImg.getGrayscale(rPix, scanLineY) + intensityOffset);

        int intensityOffset = 0;
        return Math.abs(lPix - rPix) + intensityOffset;
    }

    private static void setDisparityImgRow(int scanLineY, int[] tmpRow, int maxDisparity, GrayscalePicture dImg) {
        for (int i = 0; i < tmpRow.length; i++) {
            // dImg.setGrayscale(i, scanLineY, tmpRow[i]*255/maxDisparity);
            dImg.setGrayscale(i, scanLineY, tmpRow[i]*255/maxDisparity);
        }
    }

    private static GrayscalePicture disparityImg = null;
    private static GrayscalePicture leftImg, rightImg;
    private static int lWidth, rWidth, dWidth, dHeight;

    public static void main(String[] args) throws Exception {


        try {
            leftImg = new GrayscalePicture("vw1b.png");
            rightImg = new GrayscalePicture("vw1a.png");
            Path resultsDirP = Paths.get("/Users/felipejinli/Desktop/StereoAlgorithm/Stereo Pairs/Results");
            File resultsDir = resultsDirP.toFile();

            lWidth = leftImg.width();
            rWidth = rightImg.width();

            if ((lWidth == rWidth) && (leftImg.height() == rightImg.height())) {
                disparityImg = new GrayscalePicture(lWidth, leftImg.height());
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            System.out.println("Initial args parsing error" + e.getMessage());
        }

        assert disparityImg != null;
        dWidth = disparityImg.width();
        dHeight = disparityImg.height();

        System.out.printf("dWidth: %d; dHeight: %d", dWidth, dHeight);

        double[][] costTable = new double[dWidth][dWidth];
        int[][] moveTable = new int[dWidth][dWidth];

        System.out.printf("Dwidth: %d", dWidth);
        for (int k = 0; k < dHeight; k++) {

            int[] disparityTmpRow = new int[dWidth];
            int maxDisparity = 1; //TODO
            int tmpDisparity;

            for (int a = 1; a < lWidth; a++) {
                costTable[a][0] = a * LocalCost.getOcclusionCost();
            }
            for (int b = 1; b < rWidth; b++) {
                costTable[0][b] = b * LocalCost.getOcclusionCost();
            }
            for (int i = 1; i < dWidth; i++) {
                for (int j = 1; j < dWidth; j++) {
                    // System.out.printf("i: %d; j: %d%n", i, j);
                    int leftIntensity = leftImg.getGrayscale(i-1, k);
                    int rightIntensity = rightImg.getGrayscale(j-1, k);
                    double cmin;
                    double minD = costTable[i-1][j-1] + LocalCost.getMatchCost(leftIntensity, rightIntensity);
                    double minH = costTable[i-1][j] + LocalCost.getOcclusionCost();
                    double minV = costTable[i][j-1] + LocalCost.getOcclusionCost();
                    costTable[i][j] = cmin = min3(minD, minH, minV);
                    // System.out.printf("cmin: %f; minD: %f; minH: %f; minV: %f%n", cmin, minD, minH, minV);
                    if (cmin == minD) {
                        moveTable[i][j] = 1;
                    } else if (cmin == minH) {
                        // System.out.printf("We enter moveTable with value 2");
                        moveTable[i][j] = 2;
                    } else if (cmin == minV) {
                        moveTable[i][j] = 3;
                    } else {
                        throw new Exception("cmin error value");
                    }
                }
            }

            int lIndex = dWidth-1;
            int rIndex = dWidth-1;

            while ((lIndex != 0) && (rIndex != 0)) {
                // System.out.println(moveTable[lIndex][rIndex]);
                switch (moveTable[lIndex][rIndex]) {
                    case 1:
                       // disparityImg.setGrayscale(lIndex, k, findDisparity(k, leftImg, lIndex, rightImg, rIndex));
                        disparityTmpRow[lIndex] = tmpDisparity = findDisparity(k, leftImg, lIndex, rightImg, rIndex);
                        if (tmpDisparity > maxDisparity) maxDisparity = tmpDisparity;
                        lIndex--;
                        rIndex--;
                        // System.out.println("enter Matched \n");
                        break;
                    case 2:
                        // disparityImg.setGrayscale(lIndex, k, 0);
                        disparityTmpRow[lIndex] = 0;
                        lIndex--;
                        // System.out.println("enter Left Occlusion \n");
                        break;
                    case 3:
                        rIndex--;
                        break;
                }
            }
            System.out.println(Arrays.toString(disparityTmpRow));
            setDisparityImgRow(k, disparityTmpRow, maxDisparity, disparityImg);
        }

        disparityImg.show();
        disparityImg.save("Result1A.png");
        System.out.println("Ended main method! :)");
    }
}
