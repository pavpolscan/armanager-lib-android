package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.core.ui.DataCaptureView;

import java.util.Map;

public interface ARManager {

    static ARManager newInstance(Context context, DataCaptureView dataCaptureView) {
        return ARManagerImpl.newInstance(context, dataCaptureView);
    }

    ARView createView(Context context, @NonNull int[] rows);

    ARView setViewLayoutForRange(BarcodeAreaRange barcodeAreaRange,ARView arView);

    ARView setViewLayoutForRange(float[] barcodeAreaRange,ARView arView);

    ARView setViewLayoutForRange(Context context, float[] barcodeAreaRange,int[] viewRows);

    //    List<BarcodeAreaRange> defineRanges(float[] rangePoints);

    ARView getARViewFor(TrackedBarcode trackedBarcode, Map<String,String> arViewData);

    boolean barcodeAreaInSameRange(final TrackedBarcode trackedBarcode);
}
