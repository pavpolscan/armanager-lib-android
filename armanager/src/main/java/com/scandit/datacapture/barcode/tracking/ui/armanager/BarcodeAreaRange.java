package com.scandit.datacapture.barcode.tracking.ui.armanager;

import java.util.Arrays;
import java.util.Objects;

public class BarcodeAreaRange {
    private final float lowerBound;
    private final float upperBound;

    public BarcodeAreaRange(final float lowerBound,final float upperBound){
        //if developer will mix lower and upper, we will assign larger value to upper bound
        this.upperBound=(upperBound >= lowerBound ? upperBound : lowerBound);
        this.lowerBound=(lowerBound < upperBound ? lowerBound : upperBound);
    }

    public BarcodeAreaRange(final float[] barcodeAreaRange){
        Arrays.sort(barcodeAreaRange);
        this.lowerBound=barcodeAreaRange[0];
        this.upperBound=barcodeAreaRange[barcodeAreaRange.length-1];
    }

    public boolean isAreaWithinRange(final float area){
        if ((area>lowerBound && area<upperBound)
                || area==upperBound || area==lowerBound)
        {
            return true;
        }
        return false;
    }

    public boolean isLowerThanLowerBound(final float area){
        return area<lowerBound;
    }

    public boolean isGreaterThanUpperBound(final float area){
        return area>upperBound;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarcodeAreaRange that = (BarcodeAreaRange) o;
        return Float.compare(that.lowerBound, lowerBound) == 0 &&
                Float.compare(that.upperBound, upperBound) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerBound, upperBound);
    }
}
